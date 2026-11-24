package tungnn.tutor.java.ai.factory;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import tungnn.tutor.java.ai.config.LlmClientProperties;
import tungnn.tutor.java.ai.util.HttpUtils;

public final class DeepSeekChatModelFactory implements ChatModelFactory {
    @Override
    public ChatModel create(LlmClientProperties properties) {
        var api = DeepSeekApi.builder()
                .baseUrl(properties.baseUrl().orElse(null))
                .apiKey(properties.apiKey().orElse(null))
                .restClientBuilder(HttpUtils.restClientBuilder(properties.timeout()))
                .build();
        var options = DeepSeekChatOptions.builder()
                .model(properties.model())
                .temperature(properties.temperature())
                .maxTokens(properties.maxTokens())
                .build();
        return DeepSeekChatModel.builder()
                .deepSeekApi(api)
                .options(options)
                .retryTemplate(HttpUtils.retryTemplate(properties.maxRetries()))
                .build();
    }
}
