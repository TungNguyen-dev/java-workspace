package tungnn.tutor.java.tool.hub.plugin.docstrans;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;

final class TrackingTextTranslator implements TextTranslator {
    private final TextTranslator delegate;
    private final AtomicInteger success = new AtomicInteger();
    private final AtomicInteger failure = new AtomicInteger();
    private final AtomicReference<String> firstError = new AtomicReference<>();

    TrackingTextTranslator(TextTranslator delegate) {
        this.delegate = delegate;
    }

    @Override
    public TextTranslationResult.Single translate(TextTranslationRequest.Single request) {
        TextTranslationResult.Single result = delegate.translate(request);
        record(result);
        return result;
    }

    @Override
    public TextTranslationResult.Batch translateBatch(TextTranslationRequest.Batch request) {
        TextTranslationResult.Batch results = delegate.translateBatch(request);
        for (TextTranslationResult.Single result : results.results()) {
            record(result);
        }
        return results;
    }

    private void record(TextTranslationResult.Single result) {
        if (result instanceof TextTranslationResult.Failure(TextTranslationResult.Error error)) {
            failure.incrementAndGet();
            firstError.compareAndSet(null, error == null ? "unknown error" : error.message());
        } else {
            success.incrementAndGet();
        }
    }

    @Override
    public int maxConcurrency() {
        return delegate.maxConcurrency();
    }

    int successCount() {
        return success.get();
    }

    int failureCount() {
        return failure.get();
    }

    String firstError() {
        return firstError.get();
    }
}
