package tungnn.tutor.java.tool.hub.plugin.docstrans;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchPolicy;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchRequest;
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
    public TextTranslationResult translate(TextTranslationRequest request) {
        TextTranslationResult result = delegate.translate(request);
        record(result);
        return result;
    }

    @Override
    public List<TextTranslationResult> translateBatch(TextTranslationBatchRequest request) {
        List<TextTranslationResult> results = delegate.translateBatch(request);
        for (TextTranslationResult result : results) {
            record(result);
        }
        return results;
    }

    @Override
    public TextTranslationBatchPolicy batchPolicy() {
        return delegate.batchPolicy();
    }

    private void record(TextTranslationResult result) {
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
