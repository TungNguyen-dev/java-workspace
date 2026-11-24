package tungnn.tutor.java.tool.meddoc.core;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;

public final class TrackingTextTranslator implements TextTranslator {
    private final TextTranslator delegate;
    private final AtomicInteger success = new AtomicInteger();
    private final AtomicInteger failure = new AtomicInteger();
    private final AtomicReference<String> firstError = new AtomicReference<>();

    public TrackingTextTranslator(TextTranslator delegate) {
        this.delegate = delegate;
    }

    @Override
    public TextTranslationResult translate(TextTranslationRequest request) {
        var result = delegate.translate(request);
        if (result instanceof TextTranslationResult.Failure(TextTranslationResult.Error error)) {
            failure.incrementAndGet();
            firstError.compareAndSet(null, error == null ? "unknown error" : error.message());
        } else {
            success.incrementAndGet();
        }
        return result;
    }

    @Override
    public int maxConcurrency() {
        return delegate.maxConcurrency();
    }

    public int successCount() {
        return success.get();
    }

    public int failureCount() {
        return failure.get();
    }

    public String firstError() {
        return firstError.get();
    }
}
