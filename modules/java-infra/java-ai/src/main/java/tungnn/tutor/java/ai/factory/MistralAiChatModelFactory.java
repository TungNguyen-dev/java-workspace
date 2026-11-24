package tungnn.tutor.java.ai.factory;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import tungnn.tutor.java.ai.config.LlmClientProperties;
import tungnn.tutor.java.ai.util.HttpUtils;

public final class MistralAiChatModelFactory implements ChatModelFactory {
  @Override
  public ChatModel create(LlmClientProperties properties) {
    var api =
        MistralAiApi.builder()
            .baseUrl(properties.baseUrl().orElse(null))
            .apiKey(properties.apiKey().orElse(null))
            .restClientBuilder(HttpUtils.restClientBuilder(properties.timeout()))
            .build();
    var options =
        MistralAiChatOptions.builder()
            .model(properties.model())
            .temperature(properties.temperature())
            .maxTokens(properties.maxTokens())
            .build();
    return MistralAiChatModel.builder()
        .mistralAiApi(api)
        .options(options)
        .retryTemplate(HttpUtils.retryTemplate(properties.maxRetries()))
        .build();
  }
}
