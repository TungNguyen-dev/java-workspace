package tungnn.tutor.java.ai.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

class HttpUtilsTest {
  @Test
  void shouldRetryTransientFailures() {
    assertThat(HttpUtils.isTransient(new TransientAiException("x"))).isTrue();
    assertThat(HttpUtils.isTransient(new ResourceAccessException("x", new IOException()))).isTrue();
    assertThat(HttpUtils.isTransient(new IOException("x"))).isTrue();
    assertThat(
            HttpUtils.isTransient(
                HttpServerErrorException.create(
                    HttpStatus.BAD_GATEWAY,
                    "x",
                    new HttpHeaders(),
                    new byte[0],
                    StandardCharsets.UTF_8)))
        .isTrue();
    assertThat(
            HttpUtils.isTransient(
                HttpClientErrorException.create(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "x",
                    new HttpHeaders(),
                    new byte[0],
                    StandardCharsets.UTF_8)))
        .isTrue();
  }

  @Test
  void shouldNotRetryPermanentFailures() {
    assertThat(HttpUtils.isTransient(new NonTransientAiException("x"))).isFalse();
    assertThat(
            HttpUtils.isTransient(
                HttpClientErrorException.create(
                    HttpStatus.UNAUTHORIZED,
                    "x",
                    new HttpHeaders(),
                    new byte[0],
                    StandardCharsets.UTF_8)))
        .isFalse();
    assertThat(HttpUtils.isTransient(new IllegalStateException("x"))).isFalse();
  }
}
