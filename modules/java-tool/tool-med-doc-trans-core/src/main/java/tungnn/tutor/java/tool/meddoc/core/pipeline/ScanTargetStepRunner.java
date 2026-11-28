package tungnn.tutor.java.tool.meddoc.core.pipeline;

import java.util.List;
import java.util.Optional;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.inspect.SuspiciousTextUtils;

public final class ScanTargetStepRunner implements StepRunner {

    @Override
    public Step step() {
        return Step.SCAN_TARGET;
    }

    @Override
    public String description(PipelineState state) {
        return "Scanning the translation for suspicious text";
    }

    @Override
    public Optional<String> skipReason(PipelineState state) {
        var translated =
                state.runs(Step.TRANSLATE) || state.runs(Step.POLISH_TARGET) || state.runs(Step.FORMAT_TARGET);
        return translated ? Optional.empty() : Optional.of("no translation in this run");
    }

    @Override
    public StepOutcome run(PipelineState state) {
        var found = SuspiciousTextUtils.tagged(
                SuspiciousTextUtils.scanTranslation(state.document(), state.target()), state.current());
        state.suspects().addAll(found);
        return new StepOutcome(
                QualityGateUtils.scan(step(), found.size()), state.document(), null, List.of());
    }
}
