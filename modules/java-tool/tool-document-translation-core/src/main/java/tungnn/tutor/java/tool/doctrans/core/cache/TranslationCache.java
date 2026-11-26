package tungnn.tutor.java.tool.doctrans.core.cache;

import java.util.Optional;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;

public interface TranslationCache {

    Optional<String> find(LanguageCode targetLanguage, String sourceText);

    void save(LanguageCode targetLanguage, String sourceText, String translation);
}
