package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.huggingface;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;

/**
 * Builds a {@link ChatClient} that talks to Hugging Face through its OpenAI-compatible endpoint.
 *
 * <p>Spring AI 2.0 runs the OpenAI module on the OpenAI Java SDK, so connection settings (base URL,
 * API key, timeout) live in {@link OpenAiChatOptions}. The model is built manually (no
 * auto-configuration) so it does not conflict with the Google GenAI starter's {@code
 * ChatClient.Builder}.
 */
public final class HuggingFaceChatClientFactory {

  private static final int MAX_RETRIES = 0; // retry/backoff is handled by the app layer (phase 2)

  private HuggingFaceChatClientFactory() {
    throw new UnsupportedOperationException("cannot instantiate factory class");
  }

  public static ChatClient create(HuggingFaceProperties properties) {
    var options =
        OpenAiChatOptions.builder()
            .baseUrl(properties.baseUrl()) // https://router.huggingface.co/v1
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
