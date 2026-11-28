package tungnn.tutor.java.tool.meddoc.core.pipeline;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.glossary.GlossarySource;

public final class GlossaryStepRunner implements StepRunner {

    private final GlossarySource source;

    public GlossaryStepRunner(GlossarySource source) {
        this.source = source;
    }

    @Override
    public Step step() {
        return Step.GLOSSARY;
    }

    @Override
    public String description(PipelineState state) {
        return source == null ? "Loading the glossary" : "Loading the glossary from " + source.describe();
    }

    @Override
    public Optional<String> skipReason(PipelineState state) {
        if (!state.runs(Step.TRANSLATE)) {
            return Optional.of("the translate step is not selected");
        }
        if (state.target() == LanguageCode.ZH) {
            return Optional.of("the target language is Chinese");
        }
        return source == null ? Optional.of("no glossary source is configured") : Optional.empty();
    }

    @Override
    public StepOutcome run(PipelineState state) throws IOException {
        var loaded = source.load();
        state.setGlossary(loaded);
        var inDocument = loaded.restrictTo(state.document().toMarkdown());
        var file = state.writeArtifact(step().outputName("md"), inDocument.render());
        return new StepOutcome(
                QualityGateUtils.glossary(loaded.size(), inDocument.size()),
                state.document(),
                file,
                List.of("Entries: " + inDocument.size() + " of " + loaded.size() + " found in the document"));
    }
}
