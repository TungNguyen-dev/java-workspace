package tungnn.tutor.java.tool.meddoc.core.pipeline;

import java.io.IOException;
import java.util.Optional;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.translator.LlmMarkdownTranslator;

public final class EditStepRunner implements StepRunner {

    private final LlmMarkdownTranslator editor;

    public EditStepRunner(LlmMarkdownTranslator editor) {
        this.editor = editor;
    }

    @Override
    public Step step() {
        return Step.EDIT;
    }

    @Override
    public String description(PipelineState state) {
        return "Editing the Vietnamese text into a friendlier style";
    }

    @Override
    public Optional<String> skipReason(PipelineState state) {
        return state.target() == LanguageCode.VI ? Optional.empty() : Optional.of(ONLY_VI);
    }

    @Override
    public StepOutcome run(PipelineState state) throws IOException {
        var before = state.document();
        var edited = editor.edit(before);
        var file = state.write("05_edited.md", edited);
        return new StepOutcome(
                QualityGateUtils.edit(
                        before, edited, state.target(), editor.failureCount(), editor.warnings(), state.gatePolicy()),
                edited,
                file,
                StepDetailsUtils.sections(editor));
    }
}
