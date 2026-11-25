package tungnn.tutor.java.tool.doctrans.core.domain.text;

import java.util.List;
import java.util.Objects;

public record TextTranslationBatchRequest(
        List<TextTranslationRequest.TextUnit> textUnits,
        LanguageCode targetLanguage,
        TextTranslationRequest.TranslationContext translationContext,
        TextTranslationRequest.TranslationGlossary translationGlossary) {

    public TextTranslationBatchRequest {
        Objects.requireNonNull(textUnits, "textUnits must not be null");
        textUnits = List.copyOf(textUnits);
    }
}
