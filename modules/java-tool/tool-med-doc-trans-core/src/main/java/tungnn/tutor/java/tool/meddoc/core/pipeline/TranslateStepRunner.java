package tungnn.tutor.java.tool.meddoc.core.pipeline;

import java.io.IOException;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.translator.LlmMarkdownTranslator;

public final class TranslateStepRunner implements StepRunner {

    private final LlmMarkdownTranslator translator;

    public TranslateStepRunner(LlmMarkdownTranslator translator) {
        this.translator = translator;
    }

    @Override
    public Step step() {
        return Step.TRANSLATE;
    }

    @Override
    public String description(PipelineState state) {
        return "Translating to " + state.target().languageName();
    }

    @Override
    public StepOutcome run(PipelineState state) throws IOException {
        var before = state.document();
        var translated = translator.translate(before, state.target());
        var file = state.write("04_translated.md", translated);
        var relabelFollows = state.runs(Step.REFINE_TARGET)
                && state.target() == LanguageCode.VI
                && state.chineseSource() != null;
        return new StepOutcome(
                QualityGateUtils.translate(
                        before,
                        translated,
                        state.target(),
                        translator.failureCount(),
                        translator.warnings(),
                        relabelFollows,
                        state.gatePolicy()),
                translated,
                file,
                StepDetailsUtils.sections(translator));
    }
}
