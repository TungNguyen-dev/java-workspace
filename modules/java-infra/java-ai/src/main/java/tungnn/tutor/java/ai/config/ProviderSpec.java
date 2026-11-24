package tungnn.tutor.java.ai.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

record ProviderSpec(
        boolean openAiCompatible,
        Optional<String> defaultBaseUrl,
        Optional<String> defaultModel,
        boolean apiKeyRequired,
        Optional<String> apiKeyEnv,
        Optional<String> modelEnv,
        Optional<String> baseUrlEnv,
        Map<String, String> extraEnv) {

    ProviderSpec {
        extraEnv = Map.copyOf(extraEnv);
    }

    static ProviderSpec nativeApi(String prefix) {
        return withPrefix(false, prefix);
    }

    static ProviderSpec openAiCompatible(String prefix) {
        return withPrefix(true, prefix);
    }

    private static ProviderSpec withPrefix(boolean openAiCompatible, String prefix) {
        return new ProviderSpec(
                openAiCompatible,
                Optional.empty(),
                Optional.empty(),
                true,
                Optional.of(prefix + "_API_KEY"),
                Optional.of(prefix + "_MODEL"),
                Optional.of(prefix + "_BASE_URL"),
                Map.of());
    }

    ProviderSpec baseUrl(String value) {
        return new ProviderSpec(
                openAiCompatible,
                Optional.of(value),
                defaultModel,
                apiKeyRequired,
                apiKeyEnv,
                modelEnv,
                baseUrlEnv,
                extraEnv);
    }

    ProviderSpec model(String value) {
        return new ProviderSpec(
                openAiCompatible,
                defaultBaseUrl,
                Optional.of(value),
                apiKeyRequired,
                apiKeyEnv,
                modelEnv,
                baseUrlEnv,
                extraEnv);
    }

    ProviderSpec apiKeyOptional() {
        return new ProviderSpec(
                openAiCompatible, defaultBaseUrl, defaultModel, false, apiKeyEnv, modelEnv, baseUrlEnv, extraEnv);
    }

    ProviderSpec apiKeyEnv(String name) {
        return new ProviderSpec(
                openAiCompatible,
                defaultBaseUrl,
                defaultModel,
                apiKeyRequired,
                Optional.ofNullable(name),
                modelEnv,
                baseUrlEnv,
                extraEnv);
    }

    ProviderSpec modelEnv(String name) {
        return new ProviderSpec(
                openAiCompatible,
                defaultBaseUrl,
                defaultModel,
                apiKeyRequired,
                apiKeyEnv,
                Optional.ofNullable(name),
                baseUrlEnv,
                extraEnv);
    }

    ProviderSpec baseUrlEnv(String name) {
        return new ProviderSpec(
                openAiCompatible,
                defaultBaseUrl,
                defaultModel,
                apiKeyRequired,
                apiKeyEnv,
                modelEnv,
                Optional.ofNullable(name),
                extraEnv);
    }

    ProviderSpec extraEnv(String key, String envVar) {
        var merged = new LinkedHashMap<>(extraEnv);
        merged.put(key, envVar);
        return new ProviderSpec(
                openAiCompatible,
                defaultBaseUrl,
                defaultModel,
                apiKeyRequired,
                apiKeyEnv,
                modelEnv,
                baseUrlEnv,
                merged);
    }
}
