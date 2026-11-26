package tungnn.tutor.java.tool.crawler.service;

import java.time.Duration;
import java.util.Objects;

public record WebCrawlOptions(int maxAttempts, Duration retryBackoff) {

    private static final int DEFAULT_MAX_ATTEMPTS = 2;
    private static final Duration DEFAULT_RETRY_BACKOFF = Duration.ofSeconds(2);

    public WebCrawlOptions {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts must be positive: " + maxAttempts);
        }
        Objects.requireNonNull(retryBackoff, "retryBackoff must not be null");
        if (retryBackoff.isNegative()) {
            throw new IllegalArgumentException("retryBackoff must not be negative: " + retryBackoff);
        }
    }

    public static WebCrawlOptions defaults() {
        return new WebCrawlOptions(DEFAULT_MAX_ATTEMPTS, DEFAULT_RETRY_BACKOFF);
    }
}
