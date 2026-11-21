package tungnn.tutor.java.ai.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import tungnn.tutor.java.ai.util.StringUtils;

public record LlmClientProperties(
    LlmProvider provider,
    String baseUrl,
    String apiKey,
    String model,
    Double temperature,
    Integer maxTokens,
    Duration timeout,
    int maxRetries,
    Map<String, String> extra) {
  public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(120);
  public static final String EXTRA_PROJECT = "project";
  public static final String EXTRA_LOCATION = "location";
  public static final String EXTRA_REGION = "region";
  public static final String NO_API_KEY = "not-needed";

  public LlmClientProperties {
    Objects.requireNonNull(provider, "provider must not be null");
    extra = extra == null ? Map.of() : Map.copyOf(extra);
    baseUrl = StringUtils.isBlank(baseUrl) ? provider.defaultBaseUrl() : baseUrl.trim();
    if (baseUrl == null && provider.baseUrlRequired()) {
      throw new IllegalArgumentException(
          provider + ": baseUrl must not be blank" + envHint(provider.baseUrlEnv()));
    }
    var vertexAi =
        provider == LlmProvider.GOOGLE_GENAI && StringUtils.isNotBlank(extra.get(EXTRA_PROJECT));
    if (StringUtils.isBlank(apiKey)) {
      if (provider.apiKeyRequired() && !vertexAi) {
        throw new IllegalArgumentException(
            provider + ": apiKey must not be blank" + envHint(provider.apiKeyEnv()));
      }
      apiKey = provider.isOpenAiCompatible() ? NO_API_KEY : null;
    } else {
      apiKey = apiKey.trim();
    }
    model = StringUtils.isBlank(model) ? provider.defaultModel() : model.trim();
    if (model == null) {
      throw new IllegalArgumentException(
          provider + ": model must not be blank" + envHint(provider.modelEnv()));
    }
    timeout = Objects.requireNonNullElse(timeout, DEFAULT_TIMEOUT);
    if (timeout.isNegative() || timeout.isZero()) {
      throw new IllegalArgumentException("timeout must be positive");
    }
    if (maxRetries < 0) {
      throw new IllegalArgumentException("maxRetries must be >= 0");
    }
  }

  public static Builder builder(LlmProvider provider) {
    return new Builder(provider);
  }

  public static LlmClientProperties fromEnv(LlmProvider provider) {
    return fromEnv(provider, System::getenv);
  }

  public static LlmClientProperties fromEnv(LlmProvider provider, UnaryOperator<String> env) {
    var builder = builder(provider);
    if (provider.apiKeyEnv() != null) {
      builder.apiKey(env.apply(provider.apiKeyEnv()));
    }
    if (provider.modelEnv() != null) {
      builder.model(env.apply(provider.modelEnv()));
    }
    if (provider.baseUrlEnv() != null) {
      builder.baseUrl(env.apply(provider.baseUrlEnv()));
    }
    if (provider == LlmProvider.BEDROCK_CONVERSE) {
      builder.extra(EXTRA_REGION, env.apply("AWS_REGION"));
    }
    if (provider == LlmProvider.GOOGLE_GENAI) {
      builder.extra(EXTRA_PROJECT, env.apply("GOOGLE_CLOUD_PROJECT"));
      builder.extra(EXTRA_LOCATION, env.apply("GOOGLE_CLOUD_LOCATION"));
    }
    return builder.build();
  }

  private static String envHint(String env) {
    return env == null ? "" : " (set " + env + ")";
  }

  public String extra(String key) {
    return extra.get(key);
  }

  @Override
  public String toString() {
    return ("LlmClientProperties[provider=%s, baseUrl=%s, apiKey=%s, model=%s, temperature=%s, maxTokens=%s, timeout=%s, maxRetries=%d, extra=%s]")
        .formatted(
            provider,
            baseUrl,
            apiKey == null ? "null" : "",
            model,
            temperature,
            maxTokens,
            timeout,
            maxRetries,
            extra);
  }

  public static final class Builder {
    private final LlmProvider provider;
    private final Map<String, String> extra = new HashMap<>();
    private String baseUrl;
    private String apiKey;
    private String model;
    private Double temperature;
    private Integer maxTokens;
    private Duration timeout;
    private int maxRetries;

    private Builder(LlmProvider provider) {
      this.provider = provider;
    }

    public Builder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    public Builder apiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    public Builder model(String model) {
      this.model = model;
      return this;
    }

    public Builder temperature(Double temperature) {
      this.temperature = temperature;
      return this;
    }

    public Builder maxTokens(Integer maxTokens) {
      this.maxTokens = maxTokens;
      return this;
    }

    public Builder timeout(Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    public Builder maxRetries(int maxRetries) {
      this.maxRetries = maxRetries;
      return this;
    }

    public Builder extra(String key, String value) {
      if (StringUtils.isNotBlank(value)) {
        extra.put(key, value.trim());
      }
      return this;
    }

    public Builder extra(Map<String, String> values) {
      if (values != null) {
        values.forEach(this::extra);
      }
      return this;
    }

    public LlmClientProperties build() {
      return new LlmClientProperties(
          provider, baseUrl, apiKey, model, temperature, maxTokens, timeout, maxRetries, extra);
    }
  }
}
