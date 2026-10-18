package tungnn.tutor.java.spring.tool.doctrans.app.model;

import java.nio.file.Path;
import java.util.Set;

public record TranslateDocumentResult(Set<DocumentResult> documents) {

  public record DocumentResult(Path relativePath, boolean success, Exception error) {}
}
