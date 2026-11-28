package tungnn.tutor.java.tool.meddoc.core.step;

import java.io.IOException;
import java.util.Optional;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.quality.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.translate.LlmMarkdownTranslator;

public final class PolishTargetStepRunner implements StepRunner {

    private final LlmMarkdownTranslator polisher;

    public PolishTargetStepRunner(LlmMarkdownTranslator polisher) {
        this.polisher = polisher;
    }

    @Override
    public Step step() {
        return Step.POLISH_TARGET;
    }

    @Override
    public String description(PipelineState state) {
        return "Polishing the Vietnamese text into a friendlier style";
    }

    @Override
    public Optional<String> skipReason(PipelineState state) {
        return state.target() == LanguageCode.VI ? Optional.empty() : Optional.of(ONLY_VI);
    }

    @Override
    public StepOutcome run(PipelineState state) throws IOException {
        var before = state.document();
        var polished = polisher.polish(before);
        var file = state.write(step().outputName("md"), polished);
        return new StepOutcome(
                QualityGateUtils.polishTarget(
                        before, polished, state.target(), polisher.failureCount(), polisher.warnings(), state.gatePolicy()),
                polished,
                file,
                StepDetailsUtils.sections(polisher));
    }
}
