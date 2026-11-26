package tungnn.tutor.java.tool.meddoc.core.inspect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;

public final class SuspiciousTextUtils {

    private static final Pattern DOSE_UNIT = Pattern.compile("[钱两斤升斗克]");
    private static final Pattern NUMBER = Pattern.compile("[\\d一二三四五六七八九十百半]");
    private static final Pattern STRAY = Pattern.compile("[\\u3040-\\u30FF\\uFFFD]");
    private static final Pattern OLD_FORMULA_WORD = Pattern.compile("(?iu)(?<!\\p{L})tễ(?!\\p{L})");
    private static final Pattern HAN = Pattern.compile("\\p{IsHan}");
    private static final Pattern SEGMENT_SPLIT = Pattern.compile("[；;。]");
    private static final Pattern HERB_NAME = Pattern.compile("^[\\s、，,]*([\\p{IsHan}]{2,5}?)(?=[\\d一二三四五六七八九十半（(，, ]|$)");
    private static final String SENTENCE_END = "。！？；：）】”」.!?;:)\"|";
    private static final int EXCERPT_LENGTH = 40;
    private static final int MIN_DANGLING_LENGTH = 20;

    private SuspiciousTextUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static boolean containsHan(String text) {
        return HAN.matcher(text).find();
    }

    public static List<Suspect> scanSource(MarkdownDocument doc) {
        var suspects = new ArrayList<Suspect>();
        var blocks = doc.blocks();
        for (int i = 0; i < blocks.size(); i++) {
            if (!(blocks.get(i) instanceof Paragraph(String text))) continue;
            var t = text.strip();
            if (STRAY.matcher(t).find()) {
                suspects.add(new Suspect(i, "Stray characters left after OCR cleanup", excerpt(t)));
            }
            if (isComposition(t)) {
                composition(i, t, suspects);
            } else if (t.length() >= MIN_DANGLING_LENGTH
                    && SENTENCE_END.indexOf(t.charAt(t.length() - 1)) < 0
                    && !t.startsWith("|")) {
                suspects.add(new Suspect(i, "Paragraph seems cut off (no sentence ending)", excerpt(t)));
            }
        }
        return suspects;
    }

    public static List<Suspect> scanTranslation(MarkdownDocument doc, LanguageCode target) {
        var suspects = new ArrayList<Suspect>();
        if (target == LanguageCode.ZH) return suspects;
        var blocks = doc.blocks();
        for (int i = 0; i < blocks.size(); i++) {
            var text =
                    switch (blocks.get(i)) {
                        case Heading h -> h.text();
                        case Paragraph p -> p.text();
                        default -> "";
                    };
            if (HAN.matcher(text).find()) {
                suspects.add(new Suspect(i, "Chinese text remains after translation", excerpt(text)));
            }
            if (target == LanguageCode.VI && OLD_FORMULA_WORD.matcher(text).find()) {
                suspects.add(new Suspect(i, "Contains 'tễ', use 'bài thuốc' instead", excerpt(text)));
            }
        }
        return suspects;
    }

    private static boolean isComposition(String text) {
        return text.contains("组成") || DOSE_UNIT.matcher(text).results().count() >= 3;
    }

    private static void composition(int index, String text, List<Suspect> suspects) {
        var seen = new HashSet<String>();
        for (var segment : SEGMENT_SPLIT.split(text)) {
            var s = segment.strip();
            if (s.isEmpty() || s.contains("组成") && s.length() < 6) continue;
            var herb = HERB_NAME.matcher(s.replaceFirst("^.*[】\\]]", ""));
            if (herb.find() && !seen.add(herb.group(1))) {
                suspects.add(new Suspect(index, "Herb listed more than once: " + herb.group(1), excerpt(s)));
            }
            if (!NUMBER.matcher(s).find()) {
                suspects.add(new Suspect(index, "Herb without a dose (dose may be cut off)", excerpt(s)));
            }
        }
    }

    private static String excerpt(String text) {
        var t = text.replaceAll("\\s+", " ").strip();
        return t.length() <= EXCERPT_LENGTH ? t : t.substring(0, EXCERPT_LENGTH) + "…";
    }
}
