package tungnn.tutor.java.ai.factory;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import tungnn.tutor.java.ai.config.LlmClientProperties;
import tungnn.tutor.java.ai.util.HttpUtils;

public final class OllamaChatModelFactory implements ChatModelFactory {
  @Override
  public ChatModel create(LlmClientProperties properties) {
    var api =
        OllamaApi.builder()
            .baseUrl(properties.baseUrl().orElse(null))
            .restClientBuilder(HttpUtils.restClientBuilder(properties.timeout()))
            .build();
    var options =
        OllamaChatOptions.builder()
            .model(properties.model())
            .temperature(properties.temperature())
            .numPredict(properties.maxTokens())
            .build();
    return OllamaChatModel.builder()
        .ollamaApi(api)
        .options(options)
        .retryTemplate(HttpUtils.retryTemplate(properties.maxRetries()))
        .build();
  }
}
