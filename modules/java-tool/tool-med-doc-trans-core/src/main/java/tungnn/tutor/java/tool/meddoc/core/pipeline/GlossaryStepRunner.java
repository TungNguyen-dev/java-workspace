package tungnn.tutor.java.tool.meddoc.core.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.glossary.GlossaryExtractor;

public final class GlossaryStepRunner implements StepRunner {

    private final Supplier<GlossaryExtractor> extractor;

    public GlossaryStepRunner(Supplier<GlossaryExtractor> extractor) {
        this.extractor = extractor;
    }

    @Override
    public Step step() {
        return Step.GLOSSARY;
    }

    @Override
    public String description(PipelineState state) {
        return "Building a glossary of herb and formula names";
    }

    @Override
    public Optional<String> skipReason(PipelineState state) {
        if (!state.runs(Step.TRANSLATE)) {
            return Optional.of("the translate step is not selected");
        }
        return state.target() == LanguageCode.ZH ? Optional.of("the target language is Chinese") : Optional.empty();
    }

    @Override
    public StepOutcome run(PipelineState state) throws IOException {
        var result = extractor.get().extract(state.document(), state.target());
        state.setGlossary(result.glossary());
        var file = state.writeArtifact(step().outputName("md"), result.glossary().render());
        var details = new ArrayList<String>();
        details.add("Terms: " + result.glossary().terms().size() + " of " + result.candidates() + " candidates");
        if (result.failedBatches() > 0) {
            var note = result.failedBatches() + " glossary batches failed, the translation uses a partial glossary";
            details.add(note);
            state.skipped().add("glossary: " + note);
        }
        return new StepOutcome(
                QualityGateUtils.glossary(result.glossary().terms().size(), result.candidates()),
                state.document(),
                file,
                details);
    }
}
