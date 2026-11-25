package tungnn.tutor.java.tool.doctrans.core.domain.text;

import java.util.List;
import java.util.Objects;

public sealed interface TextTranslationRequest permits TextTranslationRequest.Single, TextTranslationRequest.Batch {

    LanguageCode targetLanguage();

    TranslationContext translationContext();

    TranslationGlossary translationGlossary();

    record TextUnit(String id, String content) {}

    record TranslationContext() {}

    record TranslationGlossary() {}

    record Single(
            TextUnit textUnit,
            LanguageCode targetLanguage,
            TranslationContext translationContext,
            TranslationGlossary translationGlossary)
            implements TextTranslationRequest {}

    record Batch(
            List<TextUnit> textUnits,
            LanguageCode targetLanguage,
            TranslationContext translationContext,
            TranslationGlossary translationGlossary)
            implements TextTranslationRequest {

        public Batch {
            Objects.requireNonNull(textUnits, "textUnits must not be null");
            textUnits = List.copyOf(textUnits);
        }

        public Single single(TextUnit textUnit) {
            return new Single(textUnit, targetLanguage, translationContext, translationGlossary);
        }
    }
}
