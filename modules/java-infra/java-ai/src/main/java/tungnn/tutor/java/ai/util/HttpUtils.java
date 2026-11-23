package tungnn.tutor.java.ai.util;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

public final class HttpUtils {
  private static final Duration MAX_CONNECT_TIMEOUT = Duration.ofSeconds(30);
  private static final Duration RETRY_DELAY = Duration.ofSeconds(2);

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
    return new RetryTemplate(
        RetryPolicy.builder().maxRetries(maxRetries).delay(RETRY_DELAY).multiplier(2).build());
  }
}
