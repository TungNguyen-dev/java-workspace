package tungnn.tutor.java.starter.mdrefiner;

import java.io.IOException;
import java.nio.file.Path;
import tungnn.tutor.java.infra.document.converter.DocxMarkdownConverterUtils;

public class DocxToMarkdownApp {

  static void main() throws IOException {
    var docxPath = Path.of("storage/sample/Chuong 4 Thanh nhiet_VN_1795342097.docx");
    var mdPath = Path.of("storage/sample/Chuong 4 Thanh nhiet_VN_1795342097.md");
    DocxMarkdownConverterUtils.convertDocxToMarkdown(docxPath, mdPath);
  }
}
