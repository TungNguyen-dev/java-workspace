package tungnn.tutor.java.ai.util;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

public final class HttpUtils {
    private static final Duration MAX_CONNECT_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration RETRY_DELAY = Duration.ofSeconds(2);
    private static final Duration MAX_RETRY_DELAY = Duration.ofSeconds(20);
    private static final Duration RETRY_JITTER = Duration.ofMillis(500);

    private HttpUtils() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }

    public static RestClient.Builder restClientBuilder(Duration timeout) {
        var connectTimeout = timeout.compareTo(MAX_CONNECT_TIMEOUT) < 0 ? timeout : MAX_CONNECT_TIMEOUT;
        var httpClient = HttpClient.newBuilder().connectTimeout(connectTimeout).build();
        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(timeout);
        return RestClient.builder().requestFactory(requestFactory);
    }

    public static RetryTemplate retryTemplate(int maxRetries) {
        return new RetryTemplate(RetryPolicy.builder()
                .maxRetries(maxRetries)
                .delay(RETRY_DELAY)
                .multiplier(2)
                .maxDelay(MAX_RETRY_DELAY)
                .jitter(RETRY_JITTER)
                .predicate(HttpUtils::isTransient)
                .build());
    }

    static boolean isTransient(Throwable error) {
        for (var cause = error; cause != null; cause = cause.getCause() == cause ? null : cause.getCause()) {
            if (cause instanceof NonTransientAiException) {
                return false;
            }
            if (cause instanceof TransientAiException
                    || cause instanceof ResourceAccessException
                    || cause instanceof IOException) {
                return true;
            }
            if (cause instanceof RestClientResponseException response) {
                var status = response.getStatusCode().value();
                return status >= 500 || status == 408 || status == 429;
            }
        }
        return false;
    }
}
