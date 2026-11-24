package tungnn.tutor.java.ai.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;

class LlmClientPropertiesTest {
  @Test
  void shouldApplyProviderDefaults() {
    var props = LlmClientProperties.builder(LlmProvider.DEEPSEEK).apiKey(" key ").build();
    assertThat(props.baseUrl()).hasValue("https://api.deepseek.com");
    assertThat(props.model()).isEqualTo("deepseek-chat");
    assertThat(props.apiKey()).hasValue("key");
    assertThat(props.timeout()).isEqualTo(LlmClientProperties.DEFAULT_TIMEOUT);
    assertThat(props.maxRetries()).isZero();
    assertThat(props.temperature()).isNull();
  }

  @Test
  void shouldFailWhenRequiredApiKeyMissing() {
    assertThatThrownBy(() -> LlmClientProperties.builder(LlmProvider.OPENAI).build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("OPENAI_API_KEY");
  }

  @Test
  void shouldFailWhenModelHasNoDefault() {
    assertThatThrownBy(
            () -> LlmClientProperties.builder(LlmProvider.HUGGING_FACE).apiKey("k").build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("HF_MODEL");
  }

  @Test
  void customOpenAiShouldRequireBaseUrl() {
    assertThatThrownBy(
            () -> LlmClientProperties.builder(LlmProvider.CUSTOM_OPENAI).model("m").build())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("baseUrl");
  }

  @Test
  void openAiCompatibleWithoutKeyShouldUsePlaceholder() {
    var props = LlmClientProperties.builder(LlmProvider.OLLAMA_OPENAI).build();
    assertThat(props.apiKey()).hasValue(LlmClientProperties.NO_API_KEY);
    assertThat(props.baseUrl()).hasValue("http://localhost:11434/v1");
  }

  @Test
  void googleGenAiOnVertexShouldNotRequireApiKey() {
    var props =
        LlmClientProperties.builder(LlmProvider.GOOGLE_GENAI)
            .extra(LlmClientProperties.EXTRA_PROJECT, "my-project")
            .build();
    assertThat(props.apiKey()).isEmpty();
    assertThat(props.vertexAi()).isTrue();
    assertThat(props.extra(LlmClientProperties.EXTRA_PROJECT)).isEqualTo("my-project");
  }

  @Test
  void shouldRejectInvalidTimeoutAndRetries() {
    var builder = LlmClientProperties.builder(LlmProvider.OLLAMA);
    assertThatThrownBy(() -> builder.timeout(Duration.ZERO).build())
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> builder.timeout(null).maxRetries(-1).build())
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void toStringShouldMaskApiKey() {
    var props = LlmClientProperties.builder(LlmProvider.GROQ).apiKey("secret-key").build();
    assertThat(props.toString()).doesNotContain("secret-key").contains("apiKey=***");
  }

  @Test
  void fromEnvShouldReadProviderVariables() {
    var env =
        Map.of(
            "HF_TOKEN", "hf_xxx",
            "HF_MODEL", "Qwen/Qwen3-8B",
            "HF_BASE_URL", "https://example.test/v1");
    var props = LlmClientProperties.fromEnv(LlmProvider.HUGGING_FACE, env::get);
    assertThat(props.apiKey()).hasValue("hf_xxx");
    assertThat(props.model()).isEqualTo("Qwen/Qwen3-8B");
    assertThat(props.baseUrl()).hasValue("https://example.test/v1");
  }

  @Test
  void providerLookupShouldBeLenient() {
    assertThat(LlmProvider.from("google-genai")).isEqualTo(LlmProvider.GOOGLE_GENAI);
    assertThat(LlmProvider.from("gemini")).isEqualTo(LlmProvider.GOOGLE_GENAI);
    assertThat(LlmProvider.from("huggingface")).isEqualTo(LlmProvider.HUGGING_FACE);
    assertThat(LlmProvider.from("ollama-openai")).isEqualTo(LlmProvider.OLLAMA_OPENAI);
    assertThat(LlmProvider.from("Mistral_AI")).isEqualTo(LlmProvider.MISTRAL_AI);
    assertThatThrownBy(() -> LlmProvider.from("nope")).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void apiKeyShouldTakePrecedenceOverVertexProject() {
    var env =
        Map.of(
            "GEMINI_API_KEY", "key",
            "GOOGLE_CLOUD_PROJECT", "my-project");
    var props = LlmClientProperties.fromEnv(LlmProvider.GOOGLE_GENAI, env::get);
    assertThat(props.apiKey()).hasValue("key");
    assertThat(props.vertexAi()).isFalse();
  }

  @Test
  void fromEnvShouldReadProviderExtras() {
    var env = Map.of("AWS_REGION", "eu-west-1", "BEDROCK_MODEL", "m");
    var props = LlmClientProperties.fromEnv(LlmProvider.BEDROCK_CONVERSE, env::get);
    assertThat(props.extra(LlmClientProperties.EXTRA_REGION)).isEqualTo("eu-west-1");
  }

  @Test
  void shouldTolerateNullAndBlankExtraValues() {
    var extra = new java.util.HashMap<String, String>();
    extra.put("a", null);
    extra.put("project", " ");
    extra.put("region", " us-east-1 ");
    var props =
        new LlmClientProperties(
            LlmProvider.BEDROCK_CONVERSE, null, null, "m", null, null, null, 0, extra);
    assertThat(props.extra()).containsOnlyKeys("region");
    assertThat(props.extra("region")).isEqualTo("us-east-1");
  }
}
