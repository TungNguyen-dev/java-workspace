package tungnn.tutor.java.tool.meddoc.core.pipeline;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.gate.QualityGateUtils;
import tungnn.tutor.java.tool.meddoc.utils.SourceDocumentUtils;

public final class LoadStepRunner implements StepRunner {

    private final Path source;

    public LoadStepRunner(Path source) {
        this.source = source;
    }

    @Override
    public Step step() {
        return Step.LOAD;
    }

    @Override
    public String description(PipelineState state) {
        return "Loading " + source.getFileName() + " as Markdown";
    }

    @Override
    public StepOutcome run(PipelineState state) throws IOException {
        var doc = SourceDocumentUtils.toMarkdown(source);
        var file = state.write(step().outputName("md"), doc);
        boolean needsChinese = state.needsChinese();
        return new StepOutcome(
                QualityGateUtils.load(doc, needsChinese, state.gatePolicy()),
                doc,
                file,
                List.of("Source: " + source.getFileName()));
    }
}
