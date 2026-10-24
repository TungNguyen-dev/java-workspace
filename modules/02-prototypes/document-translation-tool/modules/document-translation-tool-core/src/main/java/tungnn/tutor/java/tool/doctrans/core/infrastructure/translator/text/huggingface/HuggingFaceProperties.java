package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.huggingface;

import java.time.Duration;
import java.util.Objects;

/** Connection settings for the Hugging Face Inference Providers router (OpenAI-compatible). */
public record HuggingFaceProperties(
    String baseUrl, String token, String model, double temperature, Duration timeout) {

  public static final String DEFAULT_BASE_URL = "https://router.huggingface.co/v1";

  public HuggingFaceProperties {
    baseUrl = (baseUrl == null || baseUrl.isBlank()) ? DEFAULT_BASE_URL : baseUrl.trim();
    if (token == null || token.isBlank()) {
      throw new IllegalArgumentException("Hugging Face token must not be blank (set HF_TOKEN)");
    }
    if (model == null || model.isBlank()) {
      throw new IllegalArgumentException("Hugging Face model must not be blank");
    }
    timeout = Objects.requireNonNullElse(timeout, Duration.ofSeconds(120));
  }
}
