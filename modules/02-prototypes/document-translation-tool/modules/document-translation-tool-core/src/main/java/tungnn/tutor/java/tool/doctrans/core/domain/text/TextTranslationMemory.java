package tungnn.tutor.java.tool.doctrans.core.domain.text;

import java.util.Optional;

public interface TextTranslationMemory {

  Optional<String> find(LanguageCode targetLanguage, String sourceText);

  void save(LanguageCode targetLanguage, String sourceText, String translation);
}
