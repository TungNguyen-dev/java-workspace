package tungnn.tutor.java.tool.meddoc.core.markdown;

import static tungnn.tutor.java.infra.document.adapter.markdown.transform.MarkdownTransformUtils.edit;
import static tungnn.tutor.java.infra.document.adapter.markdown.transform.MarkdownTransformUtils.headingBefore;
import static tungnn.tutor.java.infra.document.adapter.markdown.transform.MarkdownTransformUtils.normalizeTitle;
import static tungnn.tutor.java.infra.document.adapter.markdown.transform.MarkdownTransformUtils.orderedListToParagraphs;
import static tungnn.tutor.java.infra.document.adapter.markdown.transform.MarkdownTransformUtils.textOf;
import static tungnn.tutor.java.infra.document.adapter.markdown.transform.MarkdownTransformUtils.transformSection;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import tungnn.tutor.java.ai.chat.TextChat;
import tungnn.tutor.java.ai.util.LlmOutputUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.OrderedList;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.infra.document.adapter.markdown.transform.MarkdownAction;
import tungnn.tutor.java.infra.document.adapter.markdown.transform.MarkdownTransformUtils;

public final class MedMarkdownActions {

    private static final Locale VI = Locale.forLanguageTag("vi");
    private static final String OPENING_MARKS = "([{（【《「『“‘\"'";
    private static final Pattern NAME_PREFIX =
            Pattern.compile("^\\s*(?:\\d+\\.\\s*)?([^:：(（\\n]+?)\\s*([(（][^)）]*[)）])?\\s*[:：]\\s*");
    private static final Pattern BRACKET_TITLE = Pattern.compile("(?s)^(?:【([^】]+)】|\\[([^\\]]+)\\](?!\\())\\s*(.*)$");
    private static final Pattern CHAPTER_ALONE = Pattern.compile("^第[一二三四五六七八九十百\\d]+章$");
    private static final Pattern CHAPTER_WITH_TITLE = Pattern.compile("^第[一二三四五六七八九十百\\d]+章\\s*\\S.*$");
    private static final Pattern NUMBERED_START = Pattern.compile("^(\\d{1,2})\\s*[.．、]\\s*(?=\\D)");
    private static final Pattern LIST_NUMBER = Pattern.compile("^(\\d{1,2})\\s*[.．]\\s*(?=\\D)");
    private static final Pattern ENUMERATION_NUMBER = Pattern.compile("^(\\d{1,2})、\\s*");
    private static final Pattern LIST_ITEM_NUMBER =
            Pattern.compile("^\\s*(?:(\\d{1,2})\\s*[.．、]|[(（](\\d{1,2})[)）])\\s*(?=\\D)");
    private static final Pattern CHAPTER_NAME = Pattern.compile("^[^\\d\\s，。,;；.．、][^，。,;；]{0,30}$");
    private static final Pattern SECTION_LINE = Pattern.compile("^第[一二三四五六七八九十百\\d]+[章节節]");
    private static final String HAN_GAP = "(?<=\\p{IsHan})\\s+(?=\\p{IsHan})";
    private static final int MAX_OBJECTIVE_LENGTH = 80;
    private static final int MIN_BROKEN_LENGTH = 20;

    private MedMarkdownActions() {}

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

    public static MarkdownAction sectionHeading(Pattern prefix, int level, Function<String, String> titleFromNext) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        return edit(blocks -> {
            for (int i = 0; i < blocks.size(); i++) {
                if (!(blocks.get(i) instanceof Paragraph(String text))) {
                    continue;
                }
                var raw = Normalizer.normalize(text, Normalizer.Form.NFC).strip();
                var m = prefix.matcher(raw);
                if (!m.find()) {
                    continue;
                }
                var title = raw.substring(m.end())
                        .replaceAll("\\s+", " ")
                        .replaceAll(HAN_GAP, "")
                        .strip();
                var hint = titleFromNext.apply(nextNonBlankText(blocks, i + 1));
                if (hint != null && (title.isEmpty() || (hint.length() > title.length() && hint.startsWith(title)))) {
                    title = hint;
                }
                if (!title.isEmpty()) {
                    blocks.set(i, new Heading(level, title));
                }
            }
            return blocks;
        });
    }

    public static MarkdownAction mergeBrokenParagraphs() {
        return edit(blocks -> {
            for (int i = 0; i < blocks.size(); i++) {
                if (!(blocks.get(i) instanceof Paragraph(String first)) || !canContinue(first)) {
                    continue;
                }
                var merged = first.strip();
                while (canContinue(merged)) {
                    int next = i + 1;
                    while (next < blocks.size() && blocks.get(next) instanceof Paragraph(String t) && t.isBlank()) {
                        next++;
                    }
                    if (next >= blocks.size()
                            || !(blocks.get(next) instanceof Paragraph(String tail))
                            || !continuesSentence(tail)) {
                        break;
                    }
                    merged += tail.strip();
                    blocks.subList(i + 1, next + 1).clear();
                }
                blocks.set(i, new Paragraph(merged));
            }
            return blocks;
        });
    }

    public static MarkdownAction bookTitleToParen(int level) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        return edit(blocks -> {
            for (int i = 0; i < blocks.size(); i++) {
                if (!(blocks.get(i) instanceof Heading(int level1, String text)) || level1 != level) {
                    continue;
                }
                int open = text.indexOf('《');
                if (open < 0) {
                    continue;
                }
                var head = normalizeTitle(text.substring(0, open));
                var book = normalizeTitle(text.substring(open).replace("《", " ").replace("》", ""));
                if (!head.isEmpty() && !book.isEmpty()) {
                    blocks.set(i, new Heading(level, head + " (" + book + ")"));
                }
            }
            return blocks;
        });
    }

    private static String nextNonBlankText(List<MarkdownBlock> blocks, int from) {
        for (int i = from; i < blocks.size(); i++) {
            if (!(blocks.get(i) instanceof Paragraph(String text))) {
                return null;
            }
            if (!text.isBlank()) {
                return normalizeTitle(text);
            }
        }
        return null;
    }

    private static boolean canContinue(String text) {
        var s = text.strip();
        if (s.length() < MIN_BROKEN_LENGTH || isStructural(s)) {
            return false;
        }
        int last = s.codePointBefore(s.length());
        return isHan(last) || last == '，' || last == '、' || last == '；';
    }

    private static boolean continuesSentence(String text) {
        var s = text.strip();
        if (s.isEmpty() || isStructural(s) || NUMBERED_START.matcher(s).find()) {
            return false;
        }
        int first = s.codePointAt(0);
        return isHan(first) || first == '，' || first == '、';
    }

    private static boolean isStructural(String s) {
        return s.startsWith("【")
                || s.startsWith("#")
                || s.startsWith("|")
                || SECTION_LINE.matcher(s).find();
    }

    private static boolean isHan(int codePoint) {
        return Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.HAN;
    }

    public static MarkdownAction llmSection(
            String headingText,
            int level,
            TextChat chat,
            String systemPrompt,
            BiConsumer<String, RuntimeException> onError) {
        Objects.requireNonNull(chat, "chat must not be null");
        return transformSection(
                headingText, level, content -> LlmOutputUtils.clean(chat.chat(systemPrompt, content)), onError);
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

    public static MarkdownAction headingBeforeLabel(String label, int level) {
        return headingBeforeLabel(label, level, false);
    }

    public static MarkdownAction headingBeforeLabel(String label, int level, boolean numbered) {
        return headingBefore(Pattern.compile("^【\\s*" + Pattern.quote(label) + "\\s*】"), level, numbered);
    }

    public static MarkdownAction chapterTitle(int level) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        return edit(blocks -> {
            int first = nextNonBlankParagraph(blocks, 0);
            if (first < 0) {
                return blocks;
            }
            var title = normalizeTitle(textOf(blocks.get(first)));
            if (CHAPTER_ALONE.matcher(title).matches()) {
                int second = nextNonBlankParagraph(blocks, first + 1);
                var name = second < 0 ? "" : normalizeTitle(textOf(blocks.get(second)));
                if (second < 0 || !CHAPTER_NAME.matcher(name).matches()) {
                    blocks.set(first, new Heading(level, title));
                    return blocks;
                }
                blocks.subList(first, second + 1).clear();
                blocks.add(first, new Heading(level, title + " " + name));
            } else if (CHAPTER_WITH_TITLE.matcher(title).matches()) {
                blocks.set(first, new Heading(level, title));
            }
            return blocks;
        });
    }

    public static MarkdownAction removeNumberedAfterHeading(int level) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        return edit(blocks -> {
            int heading = -1;
            for (int i = 0; i < blocks.size() && heading < 0; i++) {
                if (blocks.get(i) instanceof Heading h && h.level() == level) {
                    heading = i;
                }
            }
            if (heading < 0) {
                return blocks;
            }
            int last = heading;
            int expected = 1;
            for (int k = heading + 1; k < blocks.size(); k++) {
                var block = blocks.get(k);
                if (block instanceof Paragraph(String t) && t.isBlank()) {
                    continue;
                }
                if (block instanceof OrderedList) {
                    last = k;
                    break;
                }
                // Only a short run numbered 1, 2, 3... counts as learning objectives; anything else is real content.
                if (!(block instanceof Paragraph(String t))
                        || t.strip().length() > MAX_OBJECTIVE_LENGTH
                        || numberOf(t) != expected) {
                    break;
                }
                expected++;
                last = k;
            }
            if (last > heading) {
                blocks.subList(heading + 1, last + 1).clear();
            }
            return blocks;
        });
    }

    public static MarkdownAction listNumbersToEnumerationMark() {
        return edit(blocks -> {
            blocks.replaceAll(block -> block instanceof Paragraph(String text)
                    ? new Paragraph(LIST_NUMBER.matcher(text).replaceFirst("$1、"))
                    : block);
            return blocks;
        });
    }

    public static MarkdownAction enumerationMarkToListNumber() {
        return edit(blocks -> {
            blocks.replaceAll(block -> block instanceof Paragraph(String text)
                    ? new Paragraph(ENUMERATION_NUMBER.matcher(text).replaceFirst("$1. "))
                    : block);
            return blocks;
        });
    }

    private static int numberOf(String text) {
        var m = NUMBERED_START.matcher(text.strip());
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }

    private static int nextNonBlankParagraph(List<MarkdownBlock> blocks, int from) {
        for (int i = from; i < blocks.size(); i++) {
            if (!(blocks.get(i) instanceof Paragraph(String text))) {
                return -1;
            }
            if (!text.isBlank()) {
                return i;
            }
        }
        return -1;
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

    public static MarkdownAction removeSections(int level, Set<String> headingTexts) {
        return removeSections(level, headingTexts, removedHan -> {});
    }

    public static MarkdownAction removeSections(int level, Set<String> headingTexts, IntConsumer onRemovedHan) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("level must be 1..6");
        }
        var wanted = headingTexts.stream()
                .map(MarkdownTransformUtils::normalizeTitle)
                .collect(Collectors.toSet());
        return edit(blocks -> {
            var out = new ArrayList<MarkdownBlock>();
            int removedHan = 0;
            int i = 0;
            while (i < blocks.size()) {
                var block = blocks.get(i);
                if (!(block instanceof Heading(int level1, String text))
                        || level1 != level
                        || !wanted.contains(normalizeTitle(text))) {
                    out.add(block);
                    i++;
                    continue;
                }
                int end = i + 1;
                while (end < blocks.size() && !(blocks.get(end) instanceof Heading next && next.level() <= level)) {
                    end++;
                }
                for (int k = i; k < end; k++) {
                    removedHan += (int) textOf(blocks.get(k))
                            .codePoints()
                            .filter(cp -> Character.UnicodeScript.of(cp) == Character.UnicodeScript.HAN)
                            .count();
                }
                i = end;
            }
            onRemovedHan.accept(removedHan);
            return out;
        });
    }

    public static MarkdownAction unnumberLists() {
        return doc -> {
            var out = new ArrayList<>(orderedListToParagraphs(false).apply(doc).blocks());
            int i = 0;
            while (i < out.size()) {
                if (!(out.get(i) instanceof Paragraph(String first)) || listItemNumber(first) != 1) {
                    i++;
                    continue;
                }
                int expected = 1;
                int k = i;
                while (k < out.size() && out.get(k) instanceof Paragraph(String text)) {
                    if (!text.isBlank()) {
                        if (listItemNumber(text) != expected) {
                            break;
                        }
                        out.set(
                                k,
                                new Paragraph(LIST_ITEM_NUMBER
                                        .matcher(text)
                                        .replaceFirst("")
                                        .strip()));
                        expected++;
                    }
                    k++;
                }
                i = k;
            }
            return new MarkdownDocument(out);
        };
    }

    private static int listItemNumber(String text) {
        var m = LIST_ITEM_NUMBER.matcher(text);
        if (!m.find()) {
            return -1;
        }
        return Integer.parseInt(m.group(1) != null ? m.group(1) : m.group(2));
    }
}
