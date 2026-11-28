package tungnn.tutor.java.tool.meddoc.core.pipeline;

import java.util.List;
import java.util.Optional;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.inspect.SuspiciousTextUtils;

public final class ScanSourceStepRunner implements StepRunner {

    @Override
    public Step step() {
        return Step.SCAN_SOURCE;
    }

    @Override
    public String description(PipelineState state) {
        return "Scanning the Chinese source for suspicious text";
    }

    @Override
    public Optional<String> skipReason(PipelineState state) {
        return state.needsChinese() ? Optional.empty() : Optional.of("no Chinese source in this run");
    }

    @Override
    public StepOutcome run(PipelineState state) {
        var found = SuspiciousTextUtils.tagged(SuspiciousTextUtils.scanSource(state.document()), state.current());
        state.suspects().addAll(found);
        return new StepOutcome(
                QualityGateUtils.scan(step(), found.size()), state.document(), null, List.of());
    }
}
