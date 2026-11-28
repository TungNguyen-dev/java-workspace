package tungnn.tutor.java.tool.meddoc.service.report;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import tungnn.tutor.java.tool.meddoc.core.quality.GateResult;

public record StepReport(
        String name,
        StepStatus status,
        String outputFile,
        Integer blocksBefore,
        Integer blocksAfter,
        Duration elapsed,
        GateResult gate,
        List<String> details,
        boolean usesLlm,
        String llmUsage) {

    public StepReport {
        details = List.copyOf(details);
    }

    public static StepReport without(String name, StepStatus status, boolean usesLlm, String reason) {
        return new StepReport(
                name,
                status,
                null,
                null,
                null,
                null,
                null,
                reason == null ? List.of() : List.of(reason),
                usesLlm,
                null);
    }

    public String render(int index) {
        var sb = new StringBuilder("### ").append(index).append(". ").append(name).append(" — ");
        sb.append(status.text());
        if (elapsed != null) {
            sb.append(String.format(Locale.ROOT, " (%.1f s)", elapsed.toMillis() / 1000.0));
        }
        sb.append('\n');
        var body = new StringBuilder();
        if (outputFile != null) {
            body.append("- Output: ").append(outputFile);
            if (blocksBefore != null && blocksAfter != null) {
                body.append(" (blocks ").append(blocksBefore).append(" -> ").append(blocksAfter).append(')');
            } else if (blocksAfter != null) {
                body.append(" (blocks ").append(blocksAfter).append(')');
            }
            body.append('\n');
        }
        if (gate != null) {
            body.append("- Gate: ").append(gate.render()).append('\n');
        }
        details.forEach(d -> body.append("- ").append(d).append('\n'));
        if (status == StepStatus.DONE || status == StepStatus.FAILED) {
            body.append("- LLM: ")
                    .append(!usesLlm ? "not used" : llmUsage == null ? "0 calls" : llmUsage)
                    .append('\n');
        }
        return body.isEmpty() ? sb.toString() : sb.append('\n').append(body).toString();
    }
}
