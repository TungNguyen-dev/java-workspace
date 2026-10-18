package tungnn.tutor.java.tool.doctrans.core.domain.document;

import java.nio.file.Path;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;

public interface DocumentTranslationRequest {

  DocumentSource documentSource();

  LanguageCode targetLanguage();

  record DocumentSource(Path path) {}

  record DocumentTranslationContext() {}

  record DocumentTranslationGlossary() {}

  record Standard(
      DocumentSource documentSource,
      LanguageCode targetLanguage,
      DocumentTranslationContext translationContext,
      DocumentTranslationGlossary translationGlossary)
      implements DocumentTranslationRequest {}
}
