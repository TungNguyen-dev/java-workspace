package tungnn.tutor.java.tool.mdrefine.core;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.ai.util.LlmOutputUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.CodeBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.ListItem;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.OrderedList;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

public final class MarkdownActions {

    private static final Locale VI = Locale.forLanguageTag("vi");
    private static final String OPENING_MARKS = "([{（【《「『“‘\"'";
    private static final Pattern MUC_PREFIX =
            Pattern.compile("^(?:Mục|Tiết)\\s+(?:thứ\\s+\\p{L}+|\\d+|[IVXLC]+)(?![\\p{L}\\p{N}])\\s*[:.\\-–—]?\\s*");
    private static final Pattern NAME_PREFIX =
            Pattern.compile("^\\s*(?:\\d+\\.\\s*)?([^:：(（\\n]+?)\\s*([(（][^)）]*[)）])?\\s*[:：]\\s*");
    private static final Pattern BRACKET_TITLE = Pattern.compile("(?s)^(?:【([^】]+)】|\\[([^\\]]+)\\](?!\\())\\s*(.*)$");

    private MarkdownActions() {}

    public static MarkdownAction edit(Function<List<MarkdownBlock>, List<MarkdownBlock>> fn) {
        return doc -> new MarkdownDocument(fn.apply(new ArrayList<>(doc.blocks())));
    }

    public static MarkdownAction mergeToParagraph(int from, int count, String separator) {
        return edit(blocks -> {
            checkRange(blocks, from, count);
            var text = new StringBuilder();
            for (int i = from; i < from + count; i++) {
                var t = textOf(blocks.get(i));
                if (t.isEmpty()) continue;
                if (!text.isEmpty()) text.append(separator);
                text.append(t);
            }
            blocks.subList(from, from + count).clear();
            blocks.add(from, new Paragraph(text.toString()));
            return blocks;
        });
    }

    public static MarkdownAction mergeToHeading(int from, int count, int level, String separator) {
        return doc -> {
            var merged = mergeToParagraph(from, count, separator).apply(doc);
            return toHeading(from, level).apply(merged);
        };
    }

    public static MarkdownAction renameHeading(int level, String from, String to) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        var wanted = normalizeTitle(from);
        return edit(blocks -> {
            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i) instanceof Heading(int level1, String text)
                        && level1 == level
                        && normalizeTitle(text).equalsIgnoreCase(wanted)) {
                    blocks.set(i, new Heading(level, to));
                }
            }
            return blocks;
        });
    }

    public static MarkdownAction headingToInlineLabel(int level, String headingText, String label) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        var wanted = normalizeTitle(headingText);
        return edit(blocks -> {
            var out = new ArrayList<MarkdownBlock>();
            for (int i = 0; i < blocks.size(); i++) {
                var block = blocks.get(i);
                if (!(block instanceof Heading(int level1, String text))
                        || level1 != level
                        || !normalizeTitle(text).equalsIgnoreCase(wanted)) {
                    out.add(block);
                    continue;
                }
                int next = i + 1;
                while (next < blocks.size() && blocks.get(next) instanceof Paragraph(String t) && t.isBlank()) {
                    next++;
                }
                if (next < blocks.size() && blocks.get(next) instanceof Paragraph(String content)) {
                    out.add(new Paragraph(label + content.strip()));
                    i = next; // content Paragraph already merged
                } else {
                    out.add(new Paragraph(label.stripTrailing()));
                }
            }
            return out;
        });
    }

    public static MarkdownAction titleCaseHeading(int level) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        return edit(blocks -> {
            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i) instanceof Heading(int level1, String text) && level1 == level) {
                    blocks.set(i, new Heading(level, titleCase(text)));
                }
            }
            return blocks;
        });
    }

    public static MarkdownAction headingMainUpperParenTitle(int level) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        return edit(blocks -> {
            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i) instanceof Heading(int level1, String text) && level1 == level) {
                    blocks.set(i, new Heading(level, mainUpperParenTitle(text)));
                }
            }
            return blocks;
        });
    }

    private static String mainUpperParenTitle(String text) {
        var s = Normalizer.normalize(text == null ? "" : text, Normalizer.Form.NFC);
        var out = new StringBuilder(s.length());
        var outside = new StringBuilder();
        var inside = new StringBuilder();
        int depth = 0;
        for (int i = 0; i < s.length(); ) {
            int cp = s.codePointAt(i);
            i += Character.charCount(cp);
            boolean open = cp == '(' || cp == '（' || cp == '《';
            boolean close = cp == ')' || cp == '）' || cp == '》';
            if (open && depth == 0) {
                out.append(outside.toString().toUpperCase(VI));
                outside.setLength(0);
                out.appendCodePoint(cp);
                depth = 1;
            } else if (depth > 0) {
                if (open) {
                    depth++;
                }
                if (close && --depth == 0) {
                    out.append(titleCase(inside.toString()));
                    inside.setLength(0);
                    out.appendCodePoint(cp);
                } else {
                    inside.appendCodePoint(cp);
                }
            } else {
                outside.appendCodePoint(cp);
            }
        }
        out.append(outside.toString().toUpperCase(VI));
        out.append(titleCase(inside.toString())); // unclosed bracket
        return out.toString();
    }

    private static String titleCase(String text) {
        var s = Normalizer.normalize(text == null ? "" : text, Normalizer.Form.NFC);
        var out = new StringBuilder(s.length());
        boolean startOfWord = true;
        for (int i = 0; i < s.length(); ) {
            int cp = s.codePointAt(i);
            i += Character.charCount(cp);
            if (Character.isWhitespace(cp)) {
                out.appendCodePoint(cp);
                startOfWord = true;
            } else if (Character.isLetter(cp)) {
                var ch = new String(Character.toChars(cp));
                out.append(startOfWord ? ch.toUpperCase(VI) : ch.toLowerCase(VI));
                startOfWord = false;
            } else {
                out.appendCodePoint(cp);
                if (OPENING_MARKS.indexOf(cp) < 0) {
                    startOfWord = false;
                }
            }
        }
        return out.toString();
    }

    public static MarkdownAction insertHeadingAfterHeading(int afterLevel, int level, String text) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        var heading = new Heading(level, text);
        return edit(blocks -> {
            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i) instanceof Heading h && h.level() == afterLevel) {
                    blocks.add(i + 1, heading);
                    break;
                }
            }
            return blocks;
        });
    }

    public static MarkdownAction toHeading(int index, int level) {
        return edit(blocks -> {
            checkRange(blocks, index, 1);
            blocks.set(index, new Heading(level, textOf(blocks.get(index))));
            return blocks;
        });
    }

    public static MarkdownAction orderedListToParagraphs(boolean keepNumber) {
        return edit(blocks -> {
            var out = new ArrayList<MarkdownBlock>();
            for (var block : blocks) {
                if (!(block instanceof OrderedList(int number, List<ListItem> items))) {
                    out.add(block);
                    continue;
                }
                for (var item : items) {
                    var text = item.blocks().stream()
                            .map(MarkdownActions::textOf)
                            .filter(t -> !t.isEmpty())
                            .collect(Collectors.joining("\n"));
                    out.add(new Paragraph(keepNumber ? number + ". " + text : text));
                    number++;
                }
            }
            return out;
        });
    }

    public static MarkdownAction removeFirstOrderedList() {
        return edit(blocks -> {
            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i) instanceof OrderedList) {
                    blocks.remove(i);
                    break;
                }
            }
            return blocks;
        });
    }

    public static MarkdownAction removeIf(Predicate<MarkdownBlock> predicate) {
        return edit(blocks -> {
            blocks.removeIf(predicate);
            return blocks;
        });
    }

    public static MarkdownAction replaceText(String regex, String replacement) {
        var pattern = Pattern.compile(regex);
        return doc -> new MarkdownDocument(doc.blocks().stream()
                .map(b -> mapText(b, s -> pattern.matcher(s).replaceAll(replacement)))
                .toList());
    }

    public static MarkdownAction normalize(MarkdownRefiner refiner) {
        return doc -> new MarkdownDocument(
                doc.blocks().stream().map(b -> refineBlock(b, refiner)).toList());
    }

    private static MarkdownBlock refineBlock(MarkdownBlock block, MarkdownRefiner r) {
        return switch (block) {
            case Heading h -> new Heading(h.level(), r.refine("Heading", h.text()));
            case Paragraph p -> new Paragraph(r.refine("Paragraph", p.text()));
            case CodeBlock c -> c;
            case OrderedList l ->
                new OrderedList(
                        l.startNumber(),
                        l.items().stream()
                                .map(item -> new ListItem(item.blocks().stream()
                                        .map(b -> refineBlock(b, r))
                                        .toList()))
                                .toList());
        };
    }

    public static MarkdownAction headingIf(Pattern pattern, int level) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        return edit(blocks -> {
            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i) instanceof Paragraph(String text)) {
                    var raw = Normalizer.normalize(text, Normalizer.Form.NFC).strip();
                    if (pattern.matcher(raw).find()) {
                        blocks.set(i, new Heading(level, raw.replaceAll("\\s+", " ")));
                    }
                }
            }
            return blocks;
        });
    }

    public static MarkdownAction muc(int level) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        return edit(blocks -> {
            for (int i = 0; i < blocks.size(); i++) {
                if (!(blocks.get(i) instanceof Paragraph(String text))) {
                    continue;
                }
                var raw = Normalizer.normalize(text, Normalizer.Form.NFC).strip();
                var m = MUC_PREFIX.matcher(raw);
                if (!m.find()) {
                    continue;
                }
                var title = raw.substring(m.end()).replaceAll("\\s+", " ").strip();
                if (!title.isEmpty()) {
                    blocks.set(i, new Heading(level, title));
                }
            }
            return blocks;
        });
    }

    public static MarkdownAction transformSection(String headingText, int level, UnaryOperator<String> transformer) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        var wanted = normalizeTitle(headingText);
        return edit(blocks -> {
            var out = new ArrayList<MarkdownBlock>();
            int i = 0;
            while (i < blocks.size()) {
                var block = blocks.get(i++);
                out.add(block);
                if (!(block instanceof Heading(int level1, String text))
                        || level1 != level
                        || !normalizeTitle(text).equalsIgnoreCase(wanted)) {
                    continue;
                }
                int end = i;
                while (end < blocks.size() && !(blocks.get(end) instanceof Heading next && next.level() <= level)) {
                    end++;
                }
                var section = blocks.subList(i, end);
                var content = section.stream()
                        .map(MarkdownBlock::toMarkdown)
                        .collect(Collectors.joining("\n"))
                        .strip();
                if (content.isEmpty()) {
                    out.addAll(section);
                } else {
                    var result = transformer.apply(content);
                    var newBlocks = result == null || result.isBlank()
                            ? section
                            : MarkdownDocumentUtils.parse(result.strip()).blocks();
                    out.addAll(newBlocks.isEmpty() ? section : newBlocks);
                }
                i = end;
            }
            return out;
        });
    }

    public static MarkdownAction llmSection(String headingText, int level, LlmClient llmClient, String systemPrompt) {
        Objects.requireNonNull(llmClient, "llmClient must not be null");
        return transformSection(
                headingText, level, content -> LlmOutputUtils.clean(llmClient.chat(systemPrompt, content)));
    }

    public static MarkdownAction fixHeadingName(int level) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        return edit(blocks -> {
            var out = new ArrayList<MarkdownBlock>();
            for (int i = 0; i < blocks.size(); i++) {
                var block = blocks.get(i);
                if (!(block instanceof Heading h)
                        || h.level() != level
                        || i + 1 >= blocks.size()
                        || !(blocks.get(i + 1) instanceof Paragraph(String text))) {
                    out.add(block);
                    continue;
                }
                // Match on the plain name: drop the bracketed source suffix at the end of the header (if any).
                var name = normalizeTitle(h.text().replaceAll("\\s*[(（][^)）]*[)）]\\s*$", ""));
                var m = NAME_PREFIX.matcher(Normalizer.normalize(text, Normalizer.Form.NFC));
                if (!m.find()) {
                    out.add(block);
                    continue;
                }
                var prefixName = normalizeTitle(m.group(1));
                var rest = Normalizer.normalize(text, Normalizer.Form.NFC)
                        .substring(m.end())
                        .strip();
                boolean sameName = !name.isEmpty() && prefixName.equalsIgnoreCase(name);
                boolean emptyHeader = name.isEmpty() && !prefixName.matches("(?iu)(công dụng|chủ trị)");
                if (!sameName && !emptyHeader) {
                    out.add(block);
                    continue;
                }
                var fullName = m.group(2) == null ? prefixName : prefixName + " " + normalizeTitle(m.group(2));
                out.add(sameName ? h : new Heading(level, fullName));
                if (!rest.isEmpty()) {
                    out.add(new Paragraph(rest));
                }
                i++; // the following Paragraph is already handled
            }
            return out;
        });
    }

    private static String normalizeTitle(String s) {
        return Normalizer.normalize(s == null ? "" : s, Normalizer.Form.NFC)
                .strip()
                .replaceAll("\\s+", " ");
    }

    public static MarkdownAction headingBefore(Pattern pattern, int level) {
        return headingBefore(pattern, level, false);
    }

    public static MarkdownAction headingBefore(Pattern pattern, int level, boolean numbered) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        return edit(blocks -> {
            int counter = 0;
            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i) instanceof Heading h && h.level() < level) {
                    counter = 0;
                    continue;
                }
                if (!(blocks.get(i) instanceof Paragraph(String text))
                        || !pattern.matcher(Normalizer.normalize(text, Normalizer.Form.NFC)
                                        .strip())
                                .find()) {
                    continue;
                }
                int prev = i - 1;
                while (prev >= 0 && blocks.get(prev) instanceof Paragraph(String t) && t.isBlank()) prev--;
                if (prev >= 0 && blocks.get(prev) instanceof Paragraph(String prevText)) {
                    var title = Normalizer.normalize(prevText, Normalizer.Form.NFC)
                            .strip()
                            .replaceAll("\\s+", " ");
                    blocks.set(prev, new Heading(level, numbered ? ++counter + ". " + title : title));
                }
            }
            return blocks;
        });
    }

    public static MarkdownAction thanhPhan(int level) {
        return thanhPhan(level, false);
    }

    public static MarkdownAction thanhPhan(int level, boolean numbered) {
        return headingBefore(Pattern.compile("^【\\s*Thành phần\\s*】"), level, numbered);
    }

    public static MarkdownAction bracketHeading(int level) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        return edit(blocks -> {
            var out = new ArrayList<MarkdownBlock>();
            for (var block : blocks) {
                if (block instanceof Paragraph(String text)) {
                    var m = BRACKET_TITLE.matcher(text.strip());
                    if (m.matches()) {
                        var title = (m.group(1) != null ? m.group(1) : m.group(2))
                                .strip()
                                .replaceAll("\\s+", " ");
                        var content = m.group(3).strip();
                        if (!title.isEmpty()) {
                            out.add(new Heading(level, title));
                            if (!content.isEmpty()) out.add(new Paragraph(content));
                            continue;
                        }
                    }
                }
                out.add(block);
            }
            return out;
        });
    }

    // ---------- helpers ----------

    public static String textOf(MarkdownBlock block) {
        return switch (block) {
            case Heading h -> h.text().strip();
            case Paragraph p -> p.text().strip();
            case CodeBlock c -> c.code() == null ? "" : c.code().strip();
            case OrderedList l -> l.toMarkdown().strip();
        };
    }

    private static MarkdownBlock mapText(MarkdownBlock block, UnaryOperator<String> fn) {
        return switch (block) {
            case Heading h -> new Heading(h.level(), fn.apply(h.text()));
            case Paragraph p -> new Paragraph(fn.apply(p.text()));
            case CodeBlock c -> c;
            case OrderedList l ->
                new OrderedList(
                        l.startNumber(),
                        l.items().stream()
                                .map(item -> new ListItem(item.blocks().stream()
                                        .map(b -> mapText(b, fn))
                                        .toList()))
                                .toList());
        };
    }

    private static void checkRange(List<MarkdownBlock> blocks, int from, int count) {
        if (from < 0 || count < 1 || from + count > blocks.size()) {
            throw new IllegalArgumentException(
                    "Invalid range: from=%d, count=%d, size=%d".formatted(from, count, blocks.size()));
        }
    }
}
