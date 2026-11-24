package tungnn.tutor.java.ai.util;

import org.springframework.ai.chat.model.ChatModel;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.ai.client.SpringAiLlmClient;
import tungnn.tutor.java.ai.config.LlmClientProperties;
import tungnn.tutor.java.ai.config.LlmProvider;

public final class LlmClientUtils {
  private LlmClientUtils() {
    throw new UnsupportedOperationException("cannot instantiate utility class");
  }

  public static LlmClient create(LlmClientProperties properties) {
    return of(ChatModelUtils.create(properties), properties.provider());
  }

  public static LlmClient fromEnv(LlmProvider provider) {
    return create(LlmClientProperties.fromEnv(provider));
  }

  public static LlmClient of(ChatModel chatModel, LlmProvider provider) {
    return new SpringAiLlmClient(chatModel, provider);
  }
}
