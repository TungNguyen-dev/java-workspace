package tungnn.tutor.java.ai.factory;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import tungnn.tutor.java.ai.config.LlmClientProperties;

public final class AnthropicChatModelFactory implements ChatModelFactory {
  static final int DEFAULT_MAX_TOKENS = 4096;

  @Override
  public ChatModel create(LlmClientProperties properties) {
    var maxTokens = properties.maxTokens() != null ? properties.maxTokens() : DEFAULT_MAX_TOKENS;
    var options =
        AnthropicChatOptions.builder()
            .baseUrl(properties.baseUrl())
            .apiKey(properties.apiKey())
            .model(properties.model())
            .temperature(properties.temperature())
            .maxTokens(maxTokens)
            .timeout(properties.timeout())
            .maxRetries(properties.maxRetries())
            .build();
    return AnthropicChatModel.builder().options(options).build();
  }
}
