package tungnn.tutor.java.tool.meddoc.service.report;

public enum StepStatus {
    DONE("done"),
    FAILED("failed"),
    SKIPPED("skipped"),
    NOT_SELECTED("not selected"),
    NOT_RUN("not run (the run stopped earlier)");

    private final String text;

    StepStatus(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}
