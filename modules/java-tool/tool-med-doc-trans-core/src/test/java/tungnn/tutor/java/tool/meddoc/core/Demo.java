package tungnn.tutor.java.tool.meddoc.core;

import tungnn.tutor.java.infra.document.converter.DocxMarkdownConverterUtils;

import java.io.IOException;
import java.nio.file.Path;

public class Demo {

    static void main() throws IOException {
        var docxPath = Path.of("storage/tool-med-doc-trans-core/Chương 3 Hoà Giải .docx");
        var mdPath = Path.of("storage/tool-med-doc-trans-core/Chương 3 Hoà Giải .md");
        DocxMarkdownConverterUtils.convertDocxToMarkdown(docxPath, mdPath);
    }
}
