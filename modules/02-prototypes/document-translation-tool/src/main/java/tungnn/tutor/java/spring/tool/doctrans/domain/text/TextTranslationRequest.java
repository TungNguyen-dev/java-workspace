package tungnn.tutor.java.spring.tool.doctrans.domain.text;

import java.util.List;
import java.util.Objects;
import tungnn.tutor.java.spring.tool.doctrans.shared.LanguageCode;

public interface TextTranslationRequest {

  List<TextUnit> texts();

  LanguageCode targetLanguage();

  record TextBatchTranslationRequest(List<TextUnit> texts, LanguageCode targetLanguage)
      implements TextTranslationRequest {

    public TextBatchTranslationRequest {
      Objects.requireNonNull(texts, "texts must not be null");
      Objects.requireNonNull(targetLanguage, "targetLanguage must not be null");

      texts = List.copyOf(texts);
    }
  }
}
