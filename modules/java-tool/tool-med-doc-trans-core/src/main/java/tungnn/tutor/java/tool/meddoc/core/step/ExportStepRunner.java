package tungnn.tutor.java.tool.meddoc.core.step;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import tungnn.tutor.java.infra.document.converter.MarkdownDocxConverterUtils;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.quality.QualityGateUtils;

public final class ExportStepRunner implements StepRunner {

    private static final Map<LanguageCode, String> LANGUAGE_SUFFIX = Map.of(
            LanguageCode.VI, "tieng_Viet",
            LanguageCode.EN, "tieng_Anh",
            LanguageCode.JA, "tieng_Nhat",
            LanguageCode.ZH, "tieng_Trung");

    @Override
    public Step step() {
        return Step.EXPORT;
    }

    @Override
    public String description(PipelineState state) {
        return "Writing the DOCX";
    }

    @Override
    public StepOutcome run(PipelineState state) throws IOException {
        var docx = state.runDir()
                .resolve(Step.EXPORT.filePrefix() + state.baseName() + "_" + LANGUAGE_SUFFIX.get(state.target()) + ".docx");
        MarkdownDocxConverterUtils.convertMarkdownToDocx(state.current(), docx);
        state.setDocx(docx);
        return new StepOutcome(QualityGateUtils.export(docx), null, docx, List.of());
    }
}
