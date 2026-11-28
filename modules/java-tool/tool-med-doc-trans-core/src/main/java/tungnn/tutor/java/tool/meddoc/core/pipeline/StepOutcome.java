package tungnn.tutor.java.tool.meddoc.core.pipeline;

import java.nio.file.Path;
import java.util.List;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.tool.meddoc.core.gate.GateResult;

public record StepOutcome(GateResult gate, MarkdownDocument document, Path output, List<String> details) {

    public StepOutcome {
        details = List.copyOf(details);
    }
}
