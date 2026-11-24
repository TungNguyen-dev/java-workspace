package tungnn.tutor.java.tool.hub.plugin.meddoc;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.function.UnaryOperator;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.ai.config.LlmClientProperties;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.util.LlmClientUtils;
import tungnn.tutor.java.tool.hub.api.ToolException;

final class MedLlmClientFactory {

    static final List<String> PROVIDERS = List.of(
            "deepseek",
            "google-genai",
            "openai",
            "anthropic",
            "mistral-ai",
            "groq",
            "openrouter",
            "hugging-face",
            "ollama-openai",
            "ollama",
            "lm-studio");

    private static final String CONFIG_FILE = "llm.properties";
    private static final String ENV_DEFAULT_PROVIDER = "DOCTRANS_TRANSLATOR_PROVIDER";

    private final Path configFile;

    MedLlmClientFactory(Path dataDir) {
        this.configFile = dataDir.resolve(CONFIG_FILE);
    }

    static String providerParamType() {
        return "enum:" + String.join("|", PROVIDERS);
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    String defaultProvider() {
        UnaryOperator<String> lookup = lookup();
        String configured = blankToNull(lookup.apply(ENV_DEFAULT_PROVIDER));
        if (configured != null && PROVIDERS.contains(configured.toLowerCase(Locale.ROOT))) {
            return configured.toLowerCase(Locale.ROOT);
        }
        for (String p : PROVIDERS) {
            var keyEnv = LlmProvider.from(p).apiKeyEnv();
            if (keyEnv.isPresent() && blankToNull(lookup.apply(keyEnv.get())) != null) {
                return p;
            }
        }
        return PROVIDERS.getFirst();
    }

    LlmClient create(String providerName, String modelOverride) throws ToolException {
        UnaryOperator<String> lookup = lookup();
        try {
            LlmProvider provider = LlmProvider.from(providerName);
            String model = blankToNull(modelOverride);
            UnaryOperator<String> withModel = key ->
                    model != null && provider.modelEnv().filter(key::equals).isPresent() ? model : lookup.apply(key);
            return LlmClientUtils.create(LlmClientProperties.fromEnv(provider, withModel));
        } catch (IllegalArgumentException e) {
            throw new ToolException(
                    "Incomplete LLM configuration: " + e.getMessage()
                            + ". Set the environment variable or add it to the file " + configFile.toAbsolutePath(),
                    e);
        }
    }

    private UnaryOperator<String> lookup() {
        Properties file = loadConfigFile();
        return key -> {
            if (key == null) return null;
            String env = blankToNull(System.getenv(key));
            return env != null ? env : blankToNull(file.getProperty(key));
        };
    }

    private Properties loadConfigFile() {
        Properties p = new Properties();
        if (Files.isRegularFile(configFile)) {
            try (Reader r = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
                p.load(r);
            } catch (IOException ignored) {
            }
        }
        return p;
    }
}
