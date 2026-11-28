package tungnn.tutor.java.infra.document.adapter.markdown.transform;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.CodeBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.ListItem;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.OrderedList;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

public final class MarkdownTransformUtils {

    private MarkdownTransformUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

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
                            .map(MarkdownTransformUtils::textOf)
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

    public static MarkdownAction stripBold() {
        return edit(blocks -> {
            blocks.replaceAll(block -> switch (block) {
                case Heading(int level, String text) ->
                    new Heading(level, text.replace("**", "").strip());
                case Paragraph(String text)
                when text.strip().startsWith("**") ->
                    new Paragraph(text.replace("**", "").strip());
                default -> block;
            });
            return blocks;
        });
    }

    public static MarkdownAction transformSection(String headingText, int level, UnaryOperator<String> transformer) {
        return transformSection(headingText, level, transformer, null);
    }

    public static MarkdownAction transformSection(
            String headingText,
            int level,
            UnaryOperator<String> transformer,
            BiConsumer<String, RuntimeException> onError) {
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
                    String result;
                    try {
                        result = transformer.apply(content);
                    } catch (RuntimeException e) {
                        if (onError == null) {
                            throw e;
                        }
                        onError.accept(parentTitle(out, level), e);
                        out.addAll(section);
                        i = end;
                        continue;
                    }
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

    private static String parentTitle(List<MarkdownBlock> blocks, int level) {
        for (int i = blocks.size() - 1; i >= 0; i--) {
            if (blocks.get(i) instanceof Heading h && h.level() < level) {
                return normalizeTitle(h.text());
            }
        }
        return "";
    }

    public static String normalizeTitle(String s) {
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

    public static MarkdownAction relabelHeadings(MarkdownDocument source, int level, Map<String, String> labels) {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(labels, "labels must not be null");
        var sourceHeadings = source.blocks().stream()
                .filter(Heading.class::isInstance)
                .map(Heading.class::cast)
                .toList();
        return edit(blocks -> {
            var positions = new ArrayList<Integer>();
            for (int i = 0; i < blocks.size(); i++) {
                if (blocks.get(i) instanceof Heading) {
                    positions.add(i);
                }
            }
            if (positions.size() != sourceHeadings.size()) {
                throw new IllegalStateException("Heading count differs from the source: %d vs %d"
                        .formatted(positions.size(), sourceHeadings.size()));
            }
            for (int k = 0; k < positions.size(); k++) {
                if (((Heading) blocks.get(positions.get(k))).level()
                        != sourceHeadings.get(k).level()) {
                    throw new IllegalStateException(
                            "Heading %d has a different level than in the source".formatted(k + 1));
                }
            }
            for (int k = 0; k < positions.size(); k++) {
                var sourceHeading = sourceHeadings.get(k);
                var label = labels.get(normalizeTitle(sourceHeading.text()));
                if (sourceHeading.level() == level && label != null) {
                    blocks.set(positions.get(k), new Heading(level, label));
                }
            }
            return blocks;
        });
    }

    // "1. text" would be parsed back as an ordered list, whose items the translator skips; "1、text" stays a paragraph.

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
