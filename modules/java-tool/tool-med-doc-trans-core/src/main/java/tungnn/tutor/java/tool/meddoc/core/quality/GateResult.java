package tungnn.tutor.java.tool.meddoc.core.quality;

import java.util.List;

public record GateResult(String stage, List<String> problems, List<String> metrics) {

    public GateResult {
        problems = List.copyOf(problems);
        metrics = List.copyOf(metrics);
    }

    public boolean passed() {
        return problems.isEmpty();
    }

    public String render() {
        var sb = new StringBuilder(passed() ? "PASS " : "FAIL ").append(stage);
        if (!problems.isEmpty()) {
            sb.append(": ").append(String.join("; ", problems));
        }
        if (!metrics.isEmpty()) {
            sb.append(" (").append(String.join(", ", metrics)).append(')');
        }
        return sb.toString();
    }
}
