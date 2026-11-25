package tungnn.tutor.java.tool.meddoc.core.gate;

public record GatePolicy(
        int minSourceHan,
        double minOcrHanRetention,
        double minRefineSourceHanRetention,
        int maxFailedSections,
        double minBlockRatio,
        double maxBlockRatio,
        double maxUntranslatedRatio,
        int maxRefineTargetLlmFailures) {

    public static GatePolicy defaults() {
        return new GatePolicy(1, 0.95, 0.80, 0, 0.8, 1.25, 0.05, 0);
    }
}
