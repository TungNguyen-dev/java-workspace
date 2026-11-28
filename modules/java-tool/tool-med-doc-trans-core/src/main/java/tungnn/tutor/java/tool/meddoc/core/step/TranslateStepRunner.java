package tungnn.tutor.java.tool.meddoc.core.step;

import java.io.IOException;
import java.util.ArrayList;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.quality.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.core.format.MedFormatPipelines;
import tungnn.tutor.java.tool.meddoc.core.translate.LlmMarkdownTranslator;

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
        state.setSourceForCheck(before);
        var translated = translator.translate(before, state.target(), state.glossary());
        var finish = state.target() == LanguageCode.VI && state.chineseSource() != null;
        // The gate runs on the raw translation: the finishing pass adds blocks and would skew the block ratio.
        var gate = QualityGateUtils.translate(
                before,
                translated,
                state.target(),
                translator.failureCount(),
                translator.warnings(),
                finish,
                state.gatePolicy());
        var details = new ArrayList<>(StepDetailsUtils.sections(translator));
        var result = translated;
        if (finish) {
            var skippedFrom = state.skipped().size();
            result = MedFormatPipelines.finishTarget(state.skipped(), state.chineseSource())
                    .run(translated);
            StepDetailsUtils.skippedSince(state.skipped(), skippedFrom, details);
        }
        var file = state.write(step().outputName("md"), result);
        return new StepOutcome(gate, result, file, details);
    }
}
