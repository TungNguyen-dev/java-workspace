package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.memory;

import java.util.Optional;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationMemory;

public final class NoOpTextTranslationMemory implements TextTranslationMemory {
  public static final NoOpTextTranslationMemory INSTANCE = new NoOpTextTranslationMemory();

  private NoOpTextTranslationMemory() {}

  @Override
  public Optional<String> find(LanguageCode targetLanguage, String sourceText) {
    return Optional.empty();
  }

  @Override
  public void save(LanguageCode targetLanguage, String sourceText, String translation) {}
}
