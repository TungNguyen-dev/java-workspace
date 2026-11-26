package tungnn.tutor.java.tool.hub.plugin.docstrans;

import tungnn.tutor.java.tool.doctrans.core.text.TextTranslationRequest;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.function.UnaryOperator;
import tungnn.tutor.java.ai.config.LlmClientProperties;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.util.LlmClientUtils;
import tungnn.tutor.java.tool.doctrans.core.text.TextTranslator;
import tungnn.tutor.java.tool.doctrans.core.text.impl.DummyTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.text.impl.LlmTextTranslator;
import tungnn.tutor.java.tool.hub.api.ToolException;

final class TranslatorFactory {

    static final String DUMMY = "dummy";
    static final List<String> PROVIDERS = List.of(
            DUMMY,
            "google-genai",
            "openai",
            "anthropic",
            "deepseek",
            "mistral-ai",
            "groq",
            "openrouter",
            "hugging-face",
            "ollama-openai",
            "ollama",
            "lm-studio");
    static final String CONFIG_FILE = "llm.properties";

    private static final String ENV_DEFAULT_PROVIDER = "DOCTRANS_TRANSLATOR_PROVIDER";
    private static final String ENV_MAX_CONCURRENCY = "DOCTRANS_MAX_CONCURRENCY";
    private static final String ENV_MAX_BATCH_UNITS = "DOCTRANS_MAX_BATCH_UNITS";
    private static final String ENV_MAX_BATCH_CHARS = "DOCTRANS_MAX_BATCH_CHARS";

    private final Path configFile;

    TranslatorFactory(Path dataDir) {
        Objects.requireNonNull(dataDir, "dataDir must not be null");
        this.configFile = dataDir.resolve(CONFIG_FILE);
    }

    static String providerParamType() {
        return "enum:" + String.join("|", PROVIDERS);
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private static Integer parseInt(String s) {
        try {
            return s == null ? null : Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    String defaultProvider() {
        UnaryOperator<String> lookup = lookup();
        String configured = blankToNull(lookup.apply(ENV_DEFAULT_PROVIDER));
        if (configured != null) {
            String normalized = configured.toLowerCase(Locale.ROOT);
            if (PROVIDERS.contains(normalized)) {
                return normalized;
            }
        }
        for (String p : PROVIDERS) {
            if (DUMMY.equals(p)) {
                continue;
            }
            var keyEnv = LlmProvider.from(p).apiKeyEnv();
            if (keyEnv.isPresent() && blankToNull(lookup.apply(keyEnv.get())) != null) {
                return p;
            }
        }
        return DUMMY;
    }

    TextTranslator create(String providerName, String modelOverride) throws ToolException {
        if (providerName == null || DUMMY.equalsIgnoreCase(providerName)) {
            return new DummyTextTranslator();
        }
        UnaryOperator<String> lookup = lookup();
        try {
            LlmProvider provider = LlmProvider.from(providerName);
            String model = blankToNull(modelOverride);
            UnaryOperator<String> withModel = key ->
                    model != null && provider.modelEnv().filter(key::equals).isPresent() ? model : lookup.apply(key);
            LlmClientProperties props = LlmClientProperties.fromEnv(provider, withModel);
            var client = LlmClientUtils.create(props);
            Integer concurrency = parseInt(lookup.apply(ENV_MAX_CONCURRENCY));
            try {
                return LlmTextTranslator.create(client, concurrency);
            } catch (IllegalArgumentException e) {
                throw new ToolException("Invalid translator settings: " + e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            throw new ToolException("Incomplete LLM configuration: "
                    + e.getMessage()
                    + ". Set the environment variable or add it to the file "
                    + configFile.toAbsolutePath());
        }
    }

    TextTranslationRequest.BatchOverride batchOverride() throws ToolException {
        UnaryOperator<String> lookup = lookup();
        var override = new TextTranslationRequest.BatchOverride(
                parseInt(lookup.apply(ENV_MAX_BATCH_UNITS)), parseInt(lookup.apply(ENV_MAX_BATCH_CHARS)));
        try {
            override.applyTo(new TextTranslationRequest.BatchPolicy(1, 1));
        } catch (IllegalArgumentException e) {
            throw new ToolException("Invalid batch settings: " + e.getMessage());
        }
        return override;
    }

    private UnaryOperator<String> lookup() {
        Properties file = loadConfigFile();
        return key -> {
            if (key == null) {
                return null;
            }
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
