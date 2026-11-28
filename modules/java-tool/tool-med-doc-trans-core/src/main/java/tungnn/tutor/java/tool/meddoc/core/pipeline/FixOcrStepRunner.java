package tungnn.tutor.java.tool.meddoc.core.pipeline;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.ocr.OcrCleanup;

public final class FixOcrStepRunner implements StepRunner {

    private final Supplier<OcrCleanup> cleanup;

    public FixOcrStepRunner(Supplier<OcrCleanup> cleanup) {
        this.cleanup = cleanup;
    }

    @Override
    public Step step() {
        return Step.FIX_OCR;
    }

    @Override
    public String description(PipelineState state) {
        return "Normalizing and fixing OCR errors";
    }

    @Override
    public StepOutcome run(PipelineState state) throws IOException {
        var before = state.document();
        var result = cleanup.get().clean(before);
        state.setFixes(result.fixes());
        var file = state.write(step().outputName("md"), result.document());
        return new StepOutcome(
                QualityGateUtils.fixOcr(before, state.document(), state.gatePolicy()),
                state.document(),
                file,
                List.of("OCR fixes: " + result.fixes().size() + " blocks changed"));
    }
}
