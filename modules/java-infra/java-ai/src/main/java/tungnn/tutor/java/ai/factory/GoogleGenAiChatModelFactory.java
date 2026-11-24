package tungnn.tutor.java.ai.factory;

import com.google.genai.Client;
import com.google.genai.types.HttpOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import tungnn.tutor.java.ai.config.LlmClientProperties;
import tungnn.tutor.java.ai.util.HttpUtils;

public final class GoogleGenAiChatModelFactory implements ChatModelFactory {
  private static Client createClient(LlmClientProperties properties) {
    var timeoutMillis = (int) Math.min(properties.timeout().toMillis(), Integer.MAX_VALUE);
    var httpOptions = HttpOptions.builder().timeout(timeoutMillis);
    properties.baseUrl().ifPresent(httpOptions::baseUrl);
    var client = Client.builder().httpOptions(httpOptions.build());
    if (properties.vertexAi()) {
      client.vertexAI(true).project(properties.extra(LlmClientProperties.EXTRA_PROJECT));
      var location = properties.extra(LlmClientProperties.EXTRA_LOCATION);
      if (location != null) {
        client.location(location);
      }
    } else {
      client.apiKey(properties.apiKey().orElse(null));
    }
    return client.build();
  }

  @Override
  public ChatModel create(LlmClientProperties properties) {
    var options =
        GoogleGenAiChatOptions.builder()
            .model(properties.model())
            .temperature(properties.temperature())
            .maxOutputTokens(properties.maxTokens())
            .build();
    return GoogleGenAiChatModel.builder()
        .genAiClient(createClient(properties))
        .options(options)
        .retryTemplate(HttpUtils.retryTemplate(properties.maxRetries()))
        .build();
  }
}
