package tungnn.tutor.java.ai.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import tungnn.tutor.java.ai.util.StringUtils;

public record LlmClientProperties(
        LlmProvider provider,
        Optional<String> baseUrl,
        Optional<String> apiKey,
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
    public static final String EXTRA_EFFORT = "effort";
    public static final String EXTRA_EXECUTABLE = "executable";
    public static final String EXTRA_TIMEOUT_SECONDS = "timeoutSeconds";
    public static final String NO_API_KEY = "not-needed";

    public LlmClientProperties {
        Objects.requireNonNull(provider, "provider must not be null");
        extra = normalizeExtra(extra);
        baseUrl = resolveBaseUrl(
                provider, Optional.ofNullable(baseUrl).flatMap(v -> v).orElse(null));
        apiKey = resolveApiKey(
                provider, Optional.ofNullable(apiKey).flatMap(v -> v).orElse(null), extra);
        model = resolveModel(provider, model);
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
        provider.apiKeyEnv().map(env).ifPresent(builder::apiKey);
        provider.modelEnv().map(env).ifPresent(builder::model);
        provider.baseUrlEnv().map(env).ifPresent(builder::baseUrl);
        provider.extraEnv().forEach((key, envVar) -> builder.extra(key, env.apply(envVar)));
        return builder.build();
    }

    private static Map<String, String> normalizeExtra(Map<String, String> extra) {
        if (extra == null) {
            return Map.of();
        }
        var normalized = new HashMap<String, String>();
        extra.forEach((key, value) -> {
            if (key != null && StringUtils.isNotBlank(value)) {
                normalized.put(key, value.trim());
            }
        });
        return Map.copyOf(normalized);
    }

    private static Optional<String> clean(String value) {
        return Optional.ofNullable(value).filter(StringUtils::isNotBlank).map(String::trim);
    }

    private static Optional<String> resolveBaseUrl(LlmProvider provider, String baseUrl) {
        var resolved = clean(baseUrl).or(provider::defaultBaseUrl);
        if (resolved.isEmpty() && provider.baseUrlRequired()) {
            throw new IllegalArgumentException(provider + ": baseUrl must not be blank"
                    + envHint(provider.baseUrlEnv().orElse(null)));
        }
        return resolved;
    }

    private static Optional<String> resolveApiKey(LlmProvider provider, String apiKey, Map<String, String> extra) {
        var resolved = clean(apiKey);
        if (resolved.isPresent()) {
            return resolved;
        }
        if (provider.apiKeyRequired() && !isVertexAi(provider, false, extra)) {
            throw new IllegalArgumentException(provider + ": apiKey must not be blank"
                    + envHint(provider.apiKeyEnv().orElse(null)));
        }
        return provider.isOpenAiCompatible() ? Optional.of(NO_API_KEY) : Optional.empty();
    }

    private static String resolveModel(LlmProvider provider, String model) {
        return Optional.ofNullable(model)
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .or(provider::defaultModel)
                .orElseThrow(() -> new IllegalArgumentException(provider
                        + ": model must not be blank"
                        + envHint(provider.modelEnv().orElse(null))));
    }

    private static boolean isVertexAi(LlmProvider provider, boolean hasApiKey, Map<String, String> extra) {
        return provider == LlmProvider.GOOGLE_GENAI && !hasApiKey && extra.containsKey(EXTRA_PROJECT);
    }

    private static String envHint(String envName) {
        return envName == null ? "" : " (set " + envName + ")";
    }

    public boolean vertexAi() {
        return isVertexAi(provider, apiKey.isPresent(), extra);
    }

    public String extra(String key) {
        return extra.get(key);
    }

    @Override
    public String toString() {
        return ("LlmClientProperties[provider=%s, baseUrl=%s, apiKey=%s, model=%s, temperature=%s, maxTokens=%s,"
                        + " timeout=%s, maxRetries=%d, extra=%s]")
                .formatted(
                        provider,
                        baseUrl,
                        apiKey.isPresent() ? "***" : "<none>",
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
                    provider,
                    Optional.ofNullable(baseUrl),
                    Optional.ofNullable(apiKey),
                    model,
                    temperature,
                    maxTokens,
                    timeout,
                    maxRetries,
                    extra);
        }
    }
}
