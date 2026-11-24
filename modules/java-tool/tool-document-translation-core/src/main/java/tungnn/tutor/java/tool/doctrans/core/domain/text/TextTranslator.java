package tungnn.tutor.java.tool.doctrans.core.domain.text;

public interface TextTranslator {
    int DEFAULT_MAX_CONCURRENCY = 5;

    TextTranslationResult translate(TextTranslationRequest request);

    default int maxConcurrency() {
        return DEFAULT_MAX_CONCURRENCY;
    }
}
