package tungnn.tutor.java.spring.tool.doctrans.domain.text;

import java.util.List;
import java.util.Objects;
import tungnn.tutor.java.spring.tool.doctrans.shared.TranslationError;

public sealed interface TextTranslationResult
    permits TextTranslationResult.Success, TextTranslationResult.Failure {

  record Success(List<TextUnitTranslation> translations) implements TextTranslationResult {

    public Success {
      Objects.requireNonNull(translations, "translations must not be null");

      translations = List.copyOf(translations);
    }
  }

  record Failure(TranslationError error) implements TextTranslationResult {

    public Failure {
      Objects.requireNonNull(error, "error must not be null");
    }
  }
}
