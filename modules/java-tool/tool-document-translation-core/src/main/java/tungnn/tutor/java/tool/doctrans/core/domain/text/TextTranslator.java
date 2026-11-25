package tungnn.tutor.java.tool.doctrans.core.domain.text;

public interface TextTranslator {

    int DEFAULT_MAX_CONCURRENCY = 5;

    TextTranslationResult.Single translate(TextTranslationRequest.Single request);

    default TextTranslationResult.Batch translateBatch(TextTranslationRequest.Batch request) {
        return new TextTranslationResult.Batch(request.textUnits().stream()
                .map(textUnit -> translate(request.single(textUnit)))
                .toList());
    }

    default int maxConcurrency() {
        return DEFAULT_MAX_CONCURRENCY;
    }
}
