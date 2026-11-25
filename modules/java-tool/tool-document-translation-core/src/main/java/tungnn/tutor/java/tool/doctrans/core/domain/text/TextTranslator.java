package tungnn.tutor.java.tool.doctrans.core.domain.text;

import java.util.List;

public interface TextTranslator {
    int DEFAULT_MAX_CONCURRENCY = 5;

    TextTranslationResult translate(TextTranslationRequest request);

    default List<TextTranslationResult> translateBatch(TextTranslationBatchRequest request) {
        return request.textUnits().stream()
                .map(textUnit -> translate(new TextTranslationRequest.Standard(
                        textUnit,
                        request.targetLanguage(),
                        request.translationContext(),
                        request.translationGlossary())))
                .toList();
    }

    default TextTranslationBatchPolicy batchPolicy() {
        return TextTranslationBatchPolicy.SINGLE;
    }

    default int maxConcurrency() {
        return DEFAULT_MAX_CONCURRENCY;
    }
}
