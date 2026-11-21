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
    var httpOptions =
        HttpOptions.builder().timeout(Math.toIntExact(properties.timeout().toMillis()));
    if (properties.baseUrl() != null) {
      httpOptions.baseUrl(properties.baseUrl());
    }
    var client = Client.builder().httpOptions(httpOptions.build());
    var project = properties.extra(LlmClientProperties.EXTRA_PROJECT);
    if (project != null) {
      client.vertexAI(true).project(project);
      var location = properties.extra(LlmClientProperties.EXTRA_LOCATION);
      if (location != null) {
        client.location(location);
      }
    } else {
      client.apiKey(properties.apiKey());
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
