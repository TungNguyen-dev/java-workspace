package tungnn.tutor.java.ai.client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;
import tungnn.tutor.java.ai.config.LlmProvider;

public interface LlmClient {
  LlmProvider provider();

  String model();

  default String chat(String user) {
    return chat(null, user);
  }

  String chat(String system, String user);

  ChatResponse chat(Prompt prompt);

  <T> T chat(String system, String user, Class<T> type);

  Flux<String> stream(String system, String user);

  ChatClient chatClient();
}
