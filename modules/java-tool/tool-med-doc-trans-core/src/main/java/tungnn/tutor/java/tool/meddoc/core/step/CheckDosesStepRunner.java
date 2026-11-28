package tungnn.tutor.java.tool.meddoc.core.step;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.quality.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.check.DoseCheckUtils;

public final class CheckDosesStepRunner implements StepRunner {

    @Override
    public Step step() {
        return Step.CHECK_DOSES;
    }

    @Override
    public String description(PipelineState state) {
        return "Comparing doses and quantities with the source";
    }

    @Override
    public Optional<String> skipReason(PipelineState state) {
        if (state.sourceForCheck() == null) {
            return Optional.of("no source text to compare with in this run");
        }
        return state.target() == LanguageCode.ZH ? Optional.of("the target language is Chinese") : Optional.empty();
    }

    @Override
    public StepOutcome run(PipelineState state) throws IOException {
        var result = DoseCheckUtils.compare(state.sourceForCheck(), state.document());
        var file = state.writeArtifact(step().outputName("md"), DoseCheckUtils.render(result));
        return new StepOutcome(
                QualityGateUtils.checkDoses(result, state.gatePolicy()),
                state.document(),
                file,
                List.of("Sections compared: " + result.sections()));
    }
}
