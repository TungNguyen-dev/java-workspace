package tungnn.tutor.java.tool.meddoc.core;

import java.nio.file.Path;
import java.util.List;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;

public record MedDocReport(
        String sourceName,
        LanguageCode targetLanguage,
        Path outputDocx,
        List<OcrFix> ocrFixes,
        int translatedSegments,
        int failedSegments,
        String firstError,
        List<String> skippedRefineSteps,
        List<Suspect> suspects) {

    private static final int MAX_FIX_EXAMPLES = 10;
    private static final int MAX_SUSPECTS = 50;

    public String render() {
        var sb = new StringBuilder();
        sb.append("# Translation report: ").append(sourceName).append("\n\n");
        sb.append("- Target language: ")
                .append(targetLanguage.languageName())
                .append(" (")
                .append(targetLanguage.name())
                .append(")\n");
        sb.append("- Output: ").append(outputDocx.getFileName()).append('\n');
        sb.append("- Segments translated: ").append(translatedSegments);
        if (failedSegments > 0) {
            sb.append(", failed (kept as the original): ")
                    .append(failedSegments)
                    .append(" (first error: ")
                    .append(firstError)
                    .append(')');
        }
        sb.append("\n- OCR fixes: ").append(ocrFixes.size()).append(" blocks changed\n");
        if (!skippedRefineSteps.isEmpty()) {
            sb.append("- Refine steps skipped (document layout did not match): ")
                    .append(String.join("; ", skippedRefineSteps))
                    .append('\n');
        }

        sb.append("\n## OCR fix examples\n\n");
        if (ocrFixes.isEmpty()) sb.append("None.\n");
        ocrFixes.stream()
                .limit(MAX_FIX_EXAMPLES)
                .forEach(f -> sb.append("- #")
                        .append(f.blockIndex())
                        .append(": `")
                        .append(shorten(f.before()))
                        .append("` → `")
                        .append(shorten(f.after()))
                        .append("`\n"));

        sb.append("\n## To verify against the original book\n\n");
        if (suspects.isEmpty()) sb.append("Nothing suspicious detected.\n");
        suspects.stream()
                .limit(MAX_SUSPECTS)
                .forEach(s -> sb.append("- #")
                        .append(s.blockIndex())
                        .append(" ")
                        .append(s.reason())
                        .append(": ")
                        .append(s.excerpt())
                        .append('\n'));
        if (suspects.size() > MAX_SUSPECTS) {
            sb.append("- … and ").append(suspects.size() - MAX_SUSPECTS).append(" more\n");
        }
        return sb.toString();
    }

    private static String shorten(String text) {
        var t = text.replaceAll("\\s+", " ").strip();
        return t.length() <= 60 ? t : t.substring(0, 60) + "…";
    }
}
