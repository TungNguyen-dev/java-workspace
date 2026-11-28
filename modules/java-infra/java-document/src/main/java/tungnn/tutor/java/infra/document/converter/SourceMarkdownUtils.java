package tungnn.tutor.java.infra.document.converter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.infra.document.converter.DocxMarkdownConverterUtils;

public final class SourceMarkdownUtils {

    private SourceMarkdownUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static MarkdownDocument toMarkdown(Path source) throws IOException {
        var name = source.getFileName().toString().toLowerCase(Locale.ROOT);
        if (name.endsWith(".docx")) {
            return MarkdownDocumentUtils.parse(DocxMarkdownConverterUtils.toMarkdown(source));
        }
        if (name.endsWith(".md") || name.endsWith(".markdown") || name.endsWith(".txt")) {
            return MarkdownDocumentUtils.readDocument(source);
        }
        throw new IllegalArgumentException("Unsupported source format (expected .docx, .md, .markdown or .txt): " + source);
    }

    public static String baseName(Path source) {
        var name = source.getFileName().toString();
        var dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }
}
