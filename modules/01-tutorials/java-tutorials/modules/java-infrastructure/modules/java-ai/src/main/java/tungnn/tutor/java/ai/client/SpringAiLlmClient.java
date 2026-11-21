package tungnn.tutor.java.ai.client;

import java.util.Objects;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.util.StringUtils;

public record SpringAiLlmClient(LlmProvider provider, String model, ChatClient chatClient) implements LlmClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpringAiLlmClient.class);
  public SpringAiLlmClient(ChatModel chatModel, LlmProvider provider, String model) {
    Objects.requireNonNull(chatModel, "chatModel must not be null");
    this(Objects.requireNonNull(provider, "provider must not be null"), Objects.requireNonNull(model, "model must not be null"), ChatClient.create(chatModel));
  }

  @Override
  public String chat(String system, String user) {
    return timed("chat", () -> spec(system, user).call().content());
  }

  @Override
  public ChatResponse chat(Prompt prompt) {
    return timed("chat(prompt)", () -> chatClient.prompt(prompt).call().chatResponse());
  }

  @Override
  public <T> T chat(String system, String user, Class<T> type) {
    return timed("chat(entity)", () -> spec(system, user).call().entity(type));
  }

  @Override
  public Flux<String> stream(String system, String user) {
    return spec(system, user).stream().content();
  }

  @Override
  public String toString() {
    return "SpringAiLlmClient[provider=" + provider + ", model=" + model + "]";
  }

  private ChatClient.ChatClientRequestSpec spec(String system, String user) {
    Objects.requireNonNull(user, "user message must not be null");
    var spec = chatClient.prompt();
    if (StringUtils.isNotBlank(system)) {
      spec = spec.system(system);
    }
    return spec.user(user);
  }

  private <T> T timed(String operation, Supplier<T> call) {
    var start = System.nanoTime();
    try {
      return call.get();
    } finally {
      LOGGER.debug(
          "LLM {} [provider={}, model={}] took {} ms",
          operation,
          provider,
          model,
          (System.nanoTime() - start) / 1_000_000);
    }
  }
}
