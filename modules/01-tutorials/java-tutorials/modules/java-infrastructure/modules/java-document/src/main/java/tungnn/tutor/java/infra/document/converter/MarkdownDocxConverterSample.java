package tungnn.tutor.java.infra.document.converter;

import java.io.IOException;
import java.nio.file.Path;

public class MarkdownDocxConverterSample {

  static void main(String[] args) throws IOException {
    var mdPath = Path.of(args[0]);
    var docPath =
        mdPath.getParent().resolve(mdPath.getFileName().toString().replace(".md", ".docx"));
    MarkdownDocxConverterUtils.convertMarkdownToDocx(mdPath, docPath);
  }
}
