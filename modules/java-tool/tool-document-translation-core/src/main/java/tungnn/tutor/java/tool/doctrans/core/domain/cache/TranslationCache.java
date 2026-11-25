package tungnn.tutor.java.tool.doctrans.core.domain.cache;

import java.util.Optional;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;

public interface TranslationCache {
    Optional<String> find(LanguageCode targetLanguage, String sourceText);

    void save(LanguageCode targetLanguage, String sourceText, String translation);
}
