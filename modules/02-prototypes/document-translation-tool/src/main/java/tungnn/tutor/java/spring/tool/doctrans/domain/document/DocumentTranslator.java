package tungnn.tutor.java.spring.tool.doctrans.domain.document;

import tungnn.tutor.java.spring.tool.doctrans.shared.LanguageCode;

public interface DocumentTranslator {

  DocumentTranslationResult translate(Document document, LanguageCode targetLanguage);
}
