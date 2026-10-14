package tungnn.tutor.java.spring.tool.doctrans.domain.text;

import java.util.Objects;
import tungnn.tutor.java.spring.tool.doctrans.shared.LanguageCode;

public record TextUnitTranslation(
    String textId, String original, String translation, LanguageCode targetLanguage) {

  public TextUnitTranslation {
    Objects.requireNonNull(textId, "textId must not be null");
    Objects.requireNonNull(original, "original must not be null");
    Objects.requireNonNull(translation, "translation must not be null");
    Objects.requireNonNull(targetLanguage, "targetLanguage must not be null");
  }
}
