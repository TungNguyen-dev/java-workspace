package tungnn.tutor.java.ai.config;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public enum LlmProvider {
    // ===== Native Spring AI chat models =====
    OPENAI(ProviderSpec.nativeApi("OPENAI").baseUrl("https://api.openai.com/v1").model("gpt-4o-mini")),
    ANTHROPIC(ProviderSpec.nativeApi("ANTHROPIC")
            .baseUrl("https://api.anthropic.com")
            .model("claude-sonnet-5-5")),
    GOOGLE_GENAI(ProviderSpec.nativeApi("GEMINI")
            .model("gemini-2.5-flash")
            .modelEnv("GEMINI_LLM_MODEL")
            .extraEnv(LlmClientProperties.EXTRA_PROJECT, "GOOGLE_CLOUD_PROJECT")
            .extraEnv(LlmClientProperties.EXTRA_LOCATION, "GOOGLE_CLOUD_LOCATION")),
    OLLAMA(ProviderSpec.nativeApi("OLLAMA")
            .baseUrl("http://localhost:11434")
            .model("llama3")
            .apiKeyOptional()
            .apiKeyEnv(null)
            .baseUrlEnv("OLLAMA_NATIVE_BASE_URL")),
    DEEPSEEK(ProviderSpec.nativeApi("DEEPSEEK")
            .baseUrl("https://api.deepseek.com")
            .model("deepseek-chat")),
    MISTRAL_AI(ProviderSpec.nativeApi("MISTRAL_AI")
            .baseUrl("https://api.mistral.ai")
            .model("mistral-small-latest")),
    BEDROCK_CONVERSE(ProviderSpec.nativeApi("BEDROCK")
            .apiKeyOptional()
            .apiKeyEnv(null)
            .baseUrlEnv(null)
            .extraEnv(LlmClientProperties.EXTRA_REGION, "AWS_REGION")),
    // ===== OpenAI-compatible endpoints =====
    HUGGING_FACE(ProviderSpec.openAiCompatible("HF")
            .baseUrl("https://router.huggingface.co/v1")
            .apiKeyEnv("HF_TOKEN")),
    OLLAMA_OPENAI(ProviderSpec.openAiCompatible("OLLAMA")
            .baseUrl("http://localhost:11434/v1")
            .model("llama3")
            .apiKeyOptional()
            .apiKeyEnv("OLLAMA_TOKEN")),
    GROQ(ProviderSpec.openAiCompatible("GROQ")
            .baseUrl("https://api.groq.com/openai/v1")
            .model("llama-3.3-70b-versatile")),
    OPENROUTER(ProviderSpec.openAiCompatible("OPENROUTER").baseUrl("https://openrouter.ai/api/v1")),
    TOGETHER_AI(ProviderSpec.openAiCompatible("TOGETHER").baseUrl("https://api.together.xyz/v1")),
    NVIDIA_NIM(ProviderSpec.openAiCompatible("NVIDIA").baseUrl("https://integrate.api.nvidia.com/v1")),
    PERPLEXITY(ProviderSpec.openAiCompatible("PERPLEXITY")
            .baseUrl("https://api.perplexity.ai")
            .model("sonar")),
    XAI_GROK(ProviderSpec.openAiCompatible("XAI").baseUrl("https://api.x.ai/v1")),
    LM_STUDIO(ProviderSpec.openAiCompatible("LM_STUDIO")
            .baseUrl("http://localhost:1234/v1")
            .apiKeyOptional()),
    VLLM(ProviderSpec.openAiCompatible("VLLM")
            .baseUrl("http://localhost:8000/v1")
            .apiKeyOptional()),
    CUSTOM_OPENAI(ProviderSpec.openAiCompatible("LLM").apiKeyOptional());

    private final ProviderSpec spec;

    LlmProvider(ProviderSpec spec) {
        this.spec = spec;
    }

    public static LlmProvider from(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("LLM provider must not be blank");
        }
        var normalized = name.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace('.', '_');
        return switch (normalized) {
            case "GEMINI", "GOOGLE" -> GOOGLE_GENAI;
            case "HUGGINGFACE", "HF" -> HUGGING_FACE;
            case "MISTRAL" -> MISTRAL_AI;
            case "BEDROCK" -> BEDROCK_CONVERSE;
            case "XAI", "GROK" -> XAI_GROK;
            case "TOGETHER" -> TOGETHER_AI;
            case "NVIDIA" -> NVIDIA_NIM;
            default ->
                Arrays.stream(values())
                        .filter(p -> p.name().equals(normalized))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Unknown LLM provider '%s'. Supported: %s".formatted(name, Arrays.toString(values()))));
        };
    }

    public boolean isOpenAiCompatible() {
        return spec.openAiCompatible();
    }

    public Optional<String> defaultBaseUrl() {
        return spec.defaultBaseUrl();
    }

    public Optional<String> defaultModel() {
        return spec.defaultModel();
    }

    public boolean apiKeyRequired() {
        return spec.apiKeyRequired();
    }

    public boolean baseUrlRequired() {
        return isOpenAiCompatible() && spec.defaultBaseUrl().isEmpty();
    }

    public Optional<String> apiKeyEnv() {
        return spec.apiKeyEnv();
    }

    public Optional<String> modelEnv() {
        return spec.modelEnv();
    }

    public Optional<String> baseUrlEnv() {
        return spec.baseUrlEnv();
    }

    public Map<String, String> extraEnv() {
        return spec.extraEnv();
    }
}
