package tungnn.tutor.java.tool.doctrans.core.cache;

import java.util.Optional;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;

public final class NoOpTranslationCache implements TranslationCache {

    public static final NoOpTranslationCache INSTANCE = new NoOpTranslationCache();

    private NoOpTranslationCache() {}

    @Override
    public Optional<String> find(LanguageCode targetLanguage, String sourceText) {
        return Optional.empty();
    }

    @Override
    public void save(LanguageCode targetLanguage, String sourceText, String translation) {}
}
