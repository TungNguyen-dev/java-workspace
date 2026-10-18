package tungnn.tutor.java.tool.doctrans.core.domain.document;

import java.nio.file.Path;

public interface DocumentTranslationResult {

  record DocumentTranslation(Path path) {}

  record Error(String code, String message) {}

  record Success(DocumentTranslation translation) implements DocumentTranslationResult {}

  record Failure(Error error) implements DocumentTranslationResult {}
}
