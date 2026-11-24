package tungnn.tutor.java.ai.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.bedrock.converse.BedrockProxyChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import tungnn.tutor.java.ai.config.LlmClientProperties;
import tungnn.tutor.java.ai.config.LlmProvider;

class ChatModelUtilsTest {
  private static LlmClientProperties props(LlmProvider provider) {
    return LlmClientProperties.builder(provider)
        .apiKey("test-key")
        .model("test-model")
        .baseUrl(provider == LlmProvider.CUSTOM_OPENAI ? "http://localhost:9999/v1" : null)
        .temperature(0.0)
        .extra(LlmClientProperties.EXTRA_REGION, "us-east-1")
        .build();
  }

  @ParameterizedTest
  @EnumSource(LlmProvider.class)
  void shouldCreateChatModelForEveryProvider(LlmProvider provider) {
    var chatModel = ChatModelUtils.create(props(provider));
    assertThat(chatModel).isNotNull();
    if (provider.isOpenAiCompatible()) {
      assertThat(chatModel).isInstanceOf(OpenAiChatModel.class);
    }
  }

  @Test
  void nativeProvidersShouldUseDedicatedModules() {
    assertThat(ChatModelUtils.create(props(LlmProvider.OPENAI)))
        .isInstanceOf(OpenAiChatModel.class);
    assertThat(ChatModelUtils.create(props(LlmProvider.ANTHROPIC)))
        .isInstanceOf(AnthropicChatModel.class);
    assertThat(ChatModelUtils.create(props(LlmProvider.GOOGLE_GENAI)))
        .isInstanceOf(GoogleGenAiChatModel.class);
    assertThat(ChatModelUtils.create(props(LlmProvider.OLLAMA)))
        .isInstanceOf(OllamaChatModel.class);
    assertThat(ChatModelUtils.create(props(LlmProvider.DEEPSEEK)))
        .isInstanceOf(DeepSeekChatModel.class);
    assertThat(ChatModelUtils.create(props(LlmProvider.MISTRAL_AI)))
        .isInstanceOf(MistralAiChatModel.class);
    assertThat(ChatModelUtils.create(props(LlmProvider.BEDROCK_CONVERSE)))
        .isInstanceOf(BedrockProxyChatModel.class);
  }

  @Test
  void llmClientsShouldExposeProviderAndModel() {
    var client = LlmClientUtils.create(props(LlmProvider.GROQ));
    assertThat(client.provider()).isEqualTo(LlmProvider.GROQ);
    assertThat(client.model()).hasValue("test-model");
    assertThat(client.chatClient()).isNotNull();
  }
}
