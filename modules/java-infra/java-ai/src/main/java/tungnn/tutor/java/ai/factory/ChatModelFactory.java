package tungnn.tutor.java.ai.factory;

import org.springframework.ai.chat.model.ChatModel;
import tungnn.tutor.java.ai.config.LlmClientProperties;

@FunctionalInterface
public interface ChatModelFactory {
  ChatModel create(LlmClientProperties properties);
}
