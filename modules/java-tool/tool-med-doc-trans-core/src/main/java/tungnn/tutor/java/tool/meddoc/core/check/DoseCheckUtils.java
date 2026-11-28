package tungnn.tutor.java.tool.meddoc.core.check;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

public final class DoseCheckUtils {

    private static final int SECTION_LEVEL = 3;
    private static final String CN_NUMBER = "[零一二两三四五六七八九十百半]+|nửa";
    private static final String NUMBER = "(?:\\d+(?:[.,]\\d+)?|" + CN_NUMBER + ")";
    private static final String RANGE = "(?:[~～\\-–—至]|đến)";
    private static final String UNIT =
            "(?:克|毫升|升|钱|两|分|枚|片|个|条|粒|丸|只|斤|[gG]ram|[gG]am|mg|[mM][lL]|g|chỉ|lạng|cân|tiền|phân|quả|trái|lát|miếng|viên|con|lít)";
    private static final Pattern DOSE =
            Pattern.compile("(" + NUMBER + ")(?:\\s*" + RANGE + "\\s*(" + NUMBER + "))?\\s*" + UNIT);
    private static final String DIGITS = "零一二三四五六七八九";

    public record Result(int sections, List<DoseMismatch> mismatches) {

        public Result {
            mismatches = List.copyOf(mismatches);
        }
    }

    private DoseCheckUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static Result compare(MarkdownDocument source, MarkdownDocument translated) {
        var sourceSections = split(source);
        var translatedSections = split(translated);
        var mismatches = new ArrayList<DoseMismatch>();
        if (sourceSections.size() != translatedSections.size()) {
            compareSection("document", whole(source), whole(translated), mismatches);
            return new Result(1, mismatches);
        }
        for (int i = 0; i < sourceSections.size(); i++) {
            var s = sourceSections.get(i);
            compareSection(s.title(), s.text(), translatedSections.get(i).text(), mismatches);
        }
        return new Result(sourceSections.size(), mismatches);
    }

    public static List<String> doses(String text) {
        var result = new ArrayList<String>();
        var matcher = DOSE.matcher(text);
        while (matcher.find()) {
            addNumber(matcher.group(1), result);
            addNumber(matcher.group(2), result);
        }
        result.sort(String::compareTo);
        return result;
    }

    public static String render(Result result) {
        var sb = new StringBuilder("# Dose check\n\n");
        sb.append("Sections compared: ").append(result.sections()).append("\n\n");
        if (result.mismatches().isEmpty()) {
            return sb.append("No mismatch.\n").toString();
        }
        sb.append("| Section | In the source only | In the translation only |\n| --- | --- | --- |\n");
        for (var m : result.mismatches()) {
            sb.append("| %s | %s | %s |%n"
                    .formatted(m.section().replace("|", "\\|"), String.join(", ", m.missing()), String.join(", ", m.extra())));
        }
        return sb.toString();
    }

    private record Section(String title, String text) {}

    private static String whole(MarkdownDocument doc) {
        return doc.toMarkdown();
    }

    private static List<Section> split(MarkdownDocument doc) {
        var sections = new ArrayList<Section>();
        var title = "preamble";
        var text = new StringBuilder();
        for (var block : doc.blocks()) {
            if (block instanceof Heading h && h.level() == SECTION_LEVEL) {
                sections.add(new Section(title, text.toString()));
                title = h.text();
                text = new StringBuilder();
            } else {
                text.append(block.toMarkdown()).append('\n');
            }
        }
        sections.add(new Section(title, text.toString()));
        return sections;
    }

    private static void compareSection(String title, String source, String translated, List<DoseMismatch> out) {
        var expected = doses(source);
        var actual = doses(translated);
        var missing = new ArrayList<>(expected);
        var extra = new ArrayList<>(actual);
        for (var value : actual) {
            missing.remove(value);
        }
        for (var value : expected) {
            extra.remove(value);
        }
        if (!missing.isEmpty() || !extra.isEmpty()) {
            out.add(new DoseMismatch(title, missing, extra));
        }
    }

    private static void addNumber(String raw, List<String> out) {
        if (raw == null) {
            return;
        }
        var value = parse(raw);
        if (value != null) {
            out.add(value.stripTrailingZeros().toPlainString());
        }
    }

    private static BigDecimal parse(String raw) {
        if (raw.chars().allMatch(c -> Character.isDigit(c) || c == '.' || c == ',')) {
            return new BigDecimal(raw.replace(',', '.'));
        }
        return parseChinese(raw);
    }

    private static BigDecimal parseChinese(String raw) {
        if (raw.equals("nửa")) {
            return new BigDecimal("0.5");
        }
        if (raw.equals("半")) {
            return new BigDecimal("0.5");
        }
        int total = 0;
        int current = 0;
        boolean sawDigit = false;
        for (char c : raw.toCharArray()) {
            int digit = c == '两' ? 2 : DIGITS.indexOf(c);
            if (digit >= 0) {
                current = digit;
                sawDigit = true;
            } else if (c == '十' || c == '百') {
                total += (sawDigit ? current : 1) * (c == '十' ? 10 : 100);
                current = 0;
                sawDigit = false;
            } else {
                return null;
            }
        }
        return BigDecimal.valueOf(total + (sawDigit ? current : 0));
    }
}
