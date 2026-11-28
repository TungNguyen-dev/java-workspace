package tungnn.tutor.java.tool.meddoc.core.quality;

import java.nio.file.Path;

public class QualityGateException extends RuntimeException {

    private final transient GateResult result;
    private final Path runDir;
    private final String report;

    public QualityGateException(GateResult result, Path runDir) {
        this(result, runDir, null);
    }

    private QualityGateException(GateResult result, Path runDir, String report) {
        super("Quality gate '%s' failed: %s; see %s"
                .formatted(result.stage(), String.join("; ", result.problems()), runDir.resolve("report.md")));
        this.result = result;
        this.runDir = runDir;
        this.report = report;
    }

    public QualityGateException withReport(String renderedReport) {
        var copy = new QualityGateException(result, runDir, renderedReport);
        copy.setStackTrace(getStackTrace());
        return copy;
    }

    public GateResult result() {
        return result;
    }

    public Path runDir() {
        return runDir;
    }

    public String report() {
        return report;
    }
}
