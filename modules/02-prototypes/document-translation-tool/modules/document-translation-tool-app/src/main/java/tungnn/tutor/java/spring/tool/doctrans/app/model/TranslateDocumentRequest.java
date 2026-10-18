package tungnn.tutor.java.spring.tool.doctrans.app.model;

import java.nio.file.Path;
import java.util.Set;

public record TranslateDocumentRequest(Set<Document> documents, String targetLanguageCode) {

  public record Document(Path path, Path relativePath) {}
}
