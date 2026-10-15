package tungnn.tutor.java.spring.tool.doctrans.domain.document;

import java.util.Objects;
import tungnn.tutor.java.spring.tool.doctrans.shared.TranslationError;

public sealed interface DocumentTranslationResult
    permits DocumentTranslationResult.Success, DocumentTranslationResult.Failure {

  DocumentTranslationResult {} implements DocumentTranslationResult {

    public Failure {
      Objects.requireNonNull(error, "error must not be null");
    }
  }

  record Success() implements
record Failure(TranslationError error)
}
