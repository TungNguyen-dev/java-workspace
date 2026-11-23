package tungnn.tutor.java.ai.config;

import java.util.Arrays;
import java.util.Locale;

public enum LlmProvider {
  // ===== Native Spring AI chat models =====
  OPENAI(
      LlmProviderKind.NATIVE,
      "https://api.openai.com/v1",
      "gpt-4o-mini",
      true,
      "OPENAI_API_KEY",
      "OPENAI_MODEL",
      "OPENAI_BASE_URL"),
  ANTHROPIC(
      LlmProviderKind.NATIVE,
      "https://api.anthropic.com",
      "claude-sonnet-5-5",
      true,
      "ANTHROPIC_API_KEY",
      "ANTHROPIC_MODEL",
      "ANTHROPIC_BASE_URL"),
  GOOGLE_GENAI(
      LlmProviderKind.NATIVE,
      null,
      "gemini-2.5-flash",
      true,
      "GEMINI_API_KEY",
      "GEMINI_LLM_MODEL",
      "GEMINI_BASE_URL"),
  OLLAMA(
      LlmProviderKind.NATIVE,
      "http://localhost:11434",
      "llama3",
      false,
      null,
      "OLLAMA_MODEL",
      "OLLAMA_NATIVE_BASE_URL"),
  DEEPSEEK(
      LlmProviderKind.NATIVE,
      "https://api.deepseek.com",
      "deepseek-chat",
      true,
      "DEEPSEEK_API_KEY",
      "DEEPSEEK_MODEL",
      "DEEPSEEK_BASE_URL"),
  MISTRAL_AI(
      LlmProviderKind.NATIVE,
      "https://api.mistral.ai",
      "mistral-small-latest",
      true,
      "MISTRAL_AI_API_KEY",
      "MISTRAL_AI_MODEL",
      "MISTRAL_AI_BASE_URL"),
  BEDROCK_CONVERSE(LlmProviderKind.NATIVE, null, null, false, null, "BEDROCK_MODEL", null),
  // ===== OpenAI-compatible endpoints =====
  HUGGING_FACE(
      LlmProviderKind.OPENAI_COMPATIBLE,
      "https://router.huggingface.co/v1",
      null,
      true,
      "HF_TOKEN",
      "HF_MODEL",
      "HF_BASE_URL"),
  OLLAMA_OPENAI(
      LlmProviderKind.OPENAI_COMPATIBLE,
      "http://localhost:11434/v1",
      "llama3",
      false,
      "OLLAMA_TOKEN",
      "OLLAMA_MODEL",
      "OLLAMA_BASE_URL"),
  GROQ(
      LlmProviderKind.OPENAI_COMPATIBLE,
      "https://api.groq.com/openai/v1",
      "llama-3.3-70b-versatile",
      true,
      "GROQ_API_KEY",
      "GROQ_MODEL",
      "GROQ_BASE_URL"),
  OPENROUTER(
      LlmProviderKind.OPENAI_COMPATIBLE,
      "https://openrouter.ai/api/v1",
      null,
      true,
      "OPENROUTER_API_KEY",
      "OPENROUTER_MODEL",
      "OPENROUTER_BASE_URL"),
  TOGETHER_AI(
      LlmProviderKind.OPENAI_COMPATIBLE,
      "https://api.together.xyz/v1",
      null,
      true,
      "TOGETHER_API_KEY",
      "TOGETHER_MODEL",
      "TOGETHER_BASE_URL"),
  NVIDIA_NIM(
      LlmProviderKind.OPENAI_COMPATIBLE,
      "https://integrate.api.nvidia.com/v1",
      null,
      true,
      "NVIDIA_API_KEY",
      "NVIDIA_MODEL",
      "NVIDIA_BASE_URL"),
  PERPLEXITY(
      LlmProviderKind.OPENAI_COMPATIBLE,
      "https://api.perplexity.ai",
      "sonar",
      true,
      "PERPLEXITY_API_KEY",
      "PERPLEXITY_MODEL",
      "PERPLEXITY_BASE_URL"),
  XAI_GROK(
      LlmProviderKind.OPENAI_COMPATIBLE,
      "https://api.x.ai/v1",
      null,
      true,
      "XAI_API_KEY",
      "XAI_MODEL",
      "XAI_BASE_URL"),
  LM_STUDIO(
      LlmProviderKind.OPENAI_COMPATIBLE,
      "http://localhost:1234/v1",
      null,
      false,
      "LM_STUDIO_API_KEY",
      "LM_STUDIO_MODEL",
      "LM_STUDIO_BASE_URL"),
  VLLM(
      LlmProviderKind.OPENAI_COMPATIBLE,
      "http://localhost:8000/v1",
      null,
      false,
      "VLLM_API_KEY",
      "VLLM_MODEL",
      "VLLM_BASE_URL"),
  CUSTOM_OPENAI(
      LlmProviderKind.OPENAI_COMPATIBLE,
      null,
      null,
      false,
      "LLM_API_KEY",
      "LLM_MODEL",
      "LLM_BASE_URL");
  private final LlmProviderKind kind;
  private final String defaultBaseUrl;
  private final String defaultModel;
  private final boolean apiKeyRequired;
  private final String apiKeyEnv;
  private final String modelEnv;
  private final String baseUrlEnv;

  LlmProvider(
      LlmProviderKind kind,
      String defaultBaseUrl,
      String defaultModel,
      boolean apiKeyRequired,
      String apiKeyEnv,
      String modelEnv,
      String baseUrlEnv) {
    this.kind = kind;
    this.defaultBaseUrl = defaultBaseUrl;
    this.defaultModel = defaultModel;
    this.apiKeyRequired = apiKeyRequired;
    this.apiKeyEnv = apiKeyEnv;
    this.modelEnv = modelEnv;
    this.baseUrlEnv = baseUrlEnv;
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
              .orElseThrow(() ->
                  new IllegalArgumentException(
                      "Unknown LLM provider '%s'. Supported: %s"
                          .formatted(name, Arrays.toString(values()))));
    };
  }

  public LlmProviderKind kind() {
    return kind;
  }

  public boolean isOpenAiCompatible() {
    return kind == LlmProviderKind.OPENAI_COMPATIBLE;
  }

  public String defaultBaseUrl() {
    return defaultBaseUrl;
  }

  public String defaultModel() {
    return defaultModel;
  }

  public boolean apiKeyRequired() {
    return apiKeyRequired;
  }

  public boolean baseUrlRequired() {
    return isOpenAiCompatible() && defaultBaseUrl == null;
  }

  public String apiKeyEnv() {
    return apiKeyEnv;
  }

  public String modelEnv() {
    return modelEnv;
  }

  public String baseUrlEnv() {
    return baseUrlEnv;
  }
}
