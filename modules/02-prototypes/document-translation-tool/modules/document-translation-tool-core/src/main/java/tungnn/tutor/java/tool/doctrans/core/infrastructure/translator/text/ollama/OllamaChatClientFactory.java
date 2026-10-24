package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.ollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;

/**
 * Builds a {@link ChatClient} that talks to Ollama through its OpenAI-compatible endpoint (/v1).
 */
public final class OllamaChatClientFactory {

  private static final int MAX_RETRIES = 0;

  private OllamaChatClientFactory() {
    throw new UnsupportedOperationException("cannot instantiate factory class");
  }

  public static ChatClient create(OllamaProperties properties) {
    var options =
        OpenAiChatOptions.builder()
            .baseUrl(properties.baseUrl())
            .apiKey(properties.token())
            .model(properties.model())
            .temperature(properties.temperature())
            .timeout(properties.timeout())
            .maxRetries(MAX_RETRIES)
            .build();

    var chatModel = OpenAiChatModel.builder().options(options).build();
    return ChatClient.create(chatModel);
  }
}
