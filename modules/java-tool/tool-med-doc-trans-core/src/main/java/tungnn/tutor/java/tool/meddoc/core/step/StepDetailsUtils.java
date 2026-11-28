package tungnn.tutor.java.tool.meddoc.core.step;

import java.util.ArrayList;
import java.util.List;
import tungnn.tutor.java.tool.meddoc.core.translate.LlmMarkdownTranslator;

final class StepDetailsUtils {

    private StepDetailsUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    static List<String> sections(LlmMarkdownTranslator translator) {
        var details = new ArrayList<String>();
        details.add("Sections processed by the LLM: " + translator.successCount()
                + ", failed (kept as before): " + translator.failureCount());
        if (translator.firstError() != null) {
            details.add("First error: " + translator.firstError());
        }
        translator.warnings().forEach(w -> details.add("Warning: " + w));
        return details;
    }

    static void skippedSince(List<String> skipped, int from, List<String> details) {
        skipped.subList(from, skipped.size()).forEach(s -> details.add("Skipped: " + s));
    }
}
