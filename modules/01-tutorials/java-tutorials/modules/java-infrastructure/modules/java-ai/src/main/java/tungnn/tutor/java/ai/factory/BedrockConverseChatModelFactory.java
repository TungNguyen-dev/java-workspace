package tungnn.tutor.java.ai.factory;

import org.springframework.ai.bedrock.converse.BedrockChatOptions;
import org.springframework.ai.bedrock.converse.BedrockProxyChatModel;
import org.springframework.ai.chat.model.ChatModel;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import tungnn.tutor.java.ai.config.LlmClientProperties;

public final class BedrockConverseChatModelFactory implements ChatModelFactory {
  @Override
  public ChatModel create(LlmClientProperties properties) {
    var options =
        BedrockChatOptions.builder()
            .model(properties.model())
            .temperature(properties.temperature())
            .maxTokens(properties.maxTokens())
            .build();
    var builder =
        BedrockProxyChatModel.builder()
            .credentialsProvider(DefaultCredentialsProvider.builder().build())
            .timeout(properties.timeout())
            .options(options);
    var region = properties.extra(LlmClientProperties.EXTRA_REGION);
    if (region != null) {
      builder.region(Region.of(region));
    }
    return builder.build();
  }
}
