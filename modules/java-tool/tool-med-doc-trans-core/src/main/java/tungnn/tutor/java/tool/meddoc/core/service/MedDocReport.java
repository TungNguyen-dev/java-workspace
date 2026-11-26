package tungnn.tutor.java.tool.meddoc.core.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.gate.GateResult;
import tungnn.tutor.java.tool.meddoc.core.inspect.Suspect;
import tungnn.tutor.java.tool.meddoc.core.ocr.OcrFix;

public record MedDocReport(
        String sourceName,
        LanguageCode targetLanguage,
        Path outputDocx,
        Path finalFile,
        Path runDir,
        Set<Step> steps,
        String failure,
        List<OcrFix> ocrFixes,
        int translatedSections,
        int failedSections,
        String firstError,
        List<String> skippedRefineSteps,
        List<String> translationWarnings,
        List<Suspect> suspects,
        List<GateResult> gates) {

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
        sb.append("- Steps: ")
                .append(steps.stream().map(Step::cliName).collect(Collectors.joining(", ")))
                .append('\n');
        sb.append("- Output: ").append(outputText()).append('\n');
        if (failure != null) {
            sb.append("- Error: ").append(failure).append('\n');
        }
        sb.append("- Run folder: ").append(runDir).append('\n');
        if (steps.contains(Step.TRANSLATE)) {
            sb.append("- Sections translated by the LLM: ").append(translatedSections);
            if (failedSections > 0) {
                sb.append(", failed (kept as the original): ")
                        .append(failedSections)
                        .append(" (first error: ")
                        .append(firstError)
                        .append(')');
            }
            sb.append('\n');
        }
        sb.append("- OCR fixes: ")
                .append(steps.contains(Step.OCR) ? ocrFixes.size() + " blocks changed" : "step not run")
                .append('\n');
        if (!skippedRefineSteps.isEmpty()) {
            sb.append("- Refine steps skipped or partly failed: ")
                    .append(String.join("; ", skippedRefineSteps))
                    .append('\n');
        }
        if (!translationWarnings.isEmpty()) {
            sb.append("- Translation warnings: ")
                    .append(String.join("; ", translationWarnings))
                    .append('\n');
        }

        sb.append("\n## Quality gates\n\n");
        if (gates.isEmpty()) sb.append("None.\n");
        gates.forEach(g -> sb.append("- ").append(g.render()).append('\n'));

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

    private String outputText() {
        if (outputDocx != null) {
            return outputDocx.getFileName().toString();
        }
        if (failure != null) {
            return "none (the run failed)";
        }
        return "none (export not selected), last file: " + finalFile.getFileName();
    }

    private static String shorten(String text) {
        var t = text.replaceAll("\\s+", " ").strip();
        return t.length() <= 60 ? t : t.substring(0, 60) + "…";
    }
}
