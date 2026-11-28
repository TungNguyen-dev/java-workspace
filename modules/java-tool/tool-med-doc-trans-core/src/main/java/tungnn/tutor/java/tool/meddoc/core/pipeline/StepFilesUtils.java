package tungnn.tutor.java.tool.meddoc.core.pipeline;

import java.io.IOException;
import java.nio.file.Path;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

public final class StepFilesUtils {

    private StepFilesUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static Path write(Path runDir, String fileName, MarkdownDocument document) throws IOException {
        var path = runDir.resolve(fileName);
        MarkdownDocumentUtils.writeDocument(document, path);
        return path;
    }
}
