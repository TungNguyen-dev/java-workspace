package tungnn.tutor.java.spring.tool.doctrans.app.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.deepseek.DeepseekProperties;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.huggingface.HuggingFaceProperties;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.ollama.OllamaProperties;

/** Selects and configures the {@code TextTranslator} implementation. */
@ConfigurationProperties(prefix = "doctrans.translator")
public record TranslatorConfig(
    @DefaultValue("gemini") String provider,
    @DefaultValue HuggingFace huggingface,
    @DefaultValue Ollama ollama,
    @DefaultValue Deepseek deepseek) {

  public record HuggingFace(
      @DefaultValue("https://router.huggingface.co/v1") String baseUrl,
      String token,
      String model,
      @DefaultValue("0") double temperature,
      @DefaultValue("120") int timeoutSeconds) {

    public HuggingFaceProperties toProperties() {
      return new HuggingFaceProperties(
          baseUrl, token, model, temperature, Duration.ofSeconds(timeoutSeconds));
    }
  }

  public record Ollama(
      @DefaultValue("http://localhost:11434/v1") String baseUrl,
      @DefaultValue("ollama") String token,
      String model,
      @DefaultValue("0") double temperature,
      @DefaultValue("120") int timeoutSeconds) {

    public OllamaProperties toProperties() {
      return new OllamaProperties(
          baseUrl, token, model, temperature, Duration.ofSeconds(timeoutSeconds));
    }
  }

  public record Deepseek(
      @DefaultValue("https://api.deepseek.com") String baseUrl,
      String token,
      @DefaultValue("deepseek-chat") String model,
      @DefaultValue("0") double temperature,
      @DefaultValue("120") int timeoutSeconds) {

    public DeepseekProperties toProperties() {
      return new DeepseekProperties(
          baseUrl, token, model, temperature, Duration.ofSeconds(timeoutSeconds));
    }
  }
}
