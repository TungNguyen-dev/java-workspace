package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.ollama;

import java.time.Duration;
import java.util.Objects;

/** Connection settings for the Ollama OpenAI-compatible API endpoint. */
public record OllamaProperties(
    String baseUrl, String token, String model, double temperature, Duration timeout) {

  public static final String DEFAULT_BASE_URL = "http://localhost:11434/v1";

  public OllamaProperties {
    baseUrl = (baseUrl == null || baseUrl.isBlank()) ? DEFAULT_BASE_URL : baseUrl.trim();
    token = (token == null || token.isBlank()) ? "ollama" : token.trim();
    if (model == null || model.isBlank()) {
      throw new IllegalArgumentException("Ollama model must not be blank");
    }
    timeout = Objects.requireNonNullElse(timeout, Duration.ofSeconds(120));
  }
}
