package tungnn.tutor.java.spring.tool.doctrans.app.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.huggingface.HuggingFaceProperties;

/** Selects and configures the {@code TextTranslator} implementation. */
@ConfigurationProperties(prefix = "doctrans.translator")
public record TranslatorConfig(
    @DefaultValue("gemini") String provider, @DefaultValue HuggingFace huggingface) {

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
}
