package tungnn.tutor.java.ai.client;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.util.StringUtils;

public final class SpringAiLlmClient implements LlmClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpringAiLlmClient.class);

  private final LlmProvider provider;
  private final ChatModel chatModel;
  private final ChatClient chatClient;

  public SpringAiLlmClient(ChatModel chatModel, LlmProvider provider) {
    this.chatModel = Objects.requireNonNull(chatModel, "chatModel must not be null");
    this.provider = Objects.requireNonNull(provider, "provider must not be null");
    this.chatClient = ChatClient.create(chatModel);
  }

  @Override
  public LlmProvider provider() {
    return provider;
  }

  @Override
  public Optional<String> model() {
    return Optional.ofNullable(chatModel.getOptions()).map(ChatOptions::getModel);
  }

  @Override
  public ChatClient chatClient() {
    return chatClient;
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
    Objects.requireNonNull(user, "user message must not be null");
    return Flux.defer(() -> {
      var start = System.nanoTime();
      return spec(system, user).stream().content().doFinally(signal -> logElapsed("stream", start));
    });
  }

  @Override
  public String toString() {
    return "SpringAiLlmClient[provider=" + provider + ", model=" + model().orElse("unknown") + "]";
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
      logElapsed(operation, start);
    }
  }

  private void logElapsed(String operation, long startNanos) {
    LOGGER.debug(
        "LLM {} [provider={}, model={}] took {} ms",
        operation,
        provider,
        model().orElse("unknown"),
        (System.nanoTime() - startNanos) / 1_000_000);
  }
}
