package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.deepseek;

import java.time.Duration;
import java.util.Objects;

/** Connection settings for the DeepSeek OpenAI-compatible API. */
public record DeepseekProperties(
    String baseUrl, String token, String model, double temperature, Duration timeout) {

  public static final String DEFAULT_BASE_URL = "https://api.deepseek.com";
  public static final String DEFAULT_MODEL = "deepseek-chat";

  public DeepseekProperties {
    baseUrl = (baseUrl == null || baseUrl.isBlank()) ? DEFAULT_BASE_URL : baseUrl.trim();
    if (token == null || token.isBlank()) {
      throw new IllegalArgumentException("DeepSeek token must not be blank (set DEEPSEEK_API_KEY)");
    }
    model = (model == null || model.isBlank()) ? DEFAULT_MODEL : model.trim();
    timeout = Objects.requireNonNullElse(timeout, Duration.ofSeconds(120));
  }
}
