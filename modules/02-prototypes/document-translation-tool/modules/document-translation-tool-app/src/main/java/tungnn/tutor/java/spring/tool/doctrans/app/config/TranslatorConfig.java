package tungnn.tutor.java.spring.tool.doctrans.app.config;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import tungnn.tutor.java.ai.config.LlmClientProperties;
import tungnn.tutor.java.ai.config.LlmProvider;

/**
 * Selects and configures the {@code TextTranslator} implementation.
 *
 * <p>{@code provider} is {@code dummy} or any {@link LlmProvider} name ({@code google-genai},
 * {@code hugging-face}, {@code ollama-openai}, {@code deepseek}, {@code anthropic}, ...; see {@link
 * LlmProvider#from(String)}). The matching entry of {@code llm} supplies its settings; values left
 * blank fall back to the provider defaults / environment hints of {@code java-ai}.
 */
@ConfigurationProperties(prefix = "doctrans.translator")
public record TranslatorConfig(
    @DefaultValue("google-genai") String provider, Map<String, LlmSettings> llm) {

  public static final String DUMMY = "dummy";

  public TranslatorConfig {
    provider = (provider == null || provider.isBlank()) ? "google-genai" : provider.trim();
    llm = llm == null ? Map.of() : Map.copyOf(llm);
  }

  public boolean isDummy() {
    return DUMMY.equalsIgnoreCase(provider);
  }

  public LlmProvider llmProvider() {
    return LlmProvider.from(provider);
  }

  /** Settings block for the selected provider (empty settings when none is configured). */
  public LlmSettings llmSettings() {
    var selected = llmProvider();
    var exact = llm.get(provider);
    if (exact != null) {
      return exact;
    }
    return llm.entrySet().stream()
        .filter(entry -> entry.getKey() != null && LlmProvider.from(entry.getKey()) == selected)
        .map(Map.Entry::getValue)
        .filter(Objects::nonNull)
        .findFirst()
        .orElseGet(LlmSettings::empty);
  }

  public LlmClientProperties llmClientProperties() {
    return llmSettings().toProperties(llmProvider());
  }

  public record LlmSettings(
      String baseUrl,
      String apiKey,
      String model,
      Double temperature,
      Integer maxTokens,
      @DefaultValue("120") int timeoutSeconds,
      @DefaultValue("0") int maxRetries,
      Integer maxConcurrency,
      Map<String, String> extra) {

    static LlmSettings empty() {
      return new LlmSettings(null, null, null, null, null, 120, 0, null, null);
    }

    public LlmClientProperties toProperties(LlmProvider provider) {
      return LlmClientProperties.builder(provider)
          .baseUrl(baseUrl)
          .apiKey(apiKey)
          .model(model)
          .temperature(temperature)
          .maxTokens(maxTokens)
          .timeout(Duration.ofSeconds(timeoutSeconds))
          .maxRetries(maxRetries)
          .extra(extra)
          .build();
    }
  }
}
