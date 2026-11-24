package tungnn.tutor.java.ai.factory;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import tungnn.tutor.java.ai.config.LlmClientProperties;

public final class OpenAiCompatibleChatModelFactory implements ChatModelFactory {
    @Override
    public ChatModel create(LlmClientProperties properties) {
        var options = OpenAiChatOptions.builder()
                .baseUrl(properties.baseUrl().orElse(null))
                .apiKey(properties.apiKey().orElse(null))
                .model(properties.model())
                .temperature(properties.temperature())
                .maxTokens(properties.maxTokens())
                .timeout(properties.timeout())
                .maxRetries(properties.maxRetries())
                .build();
        return OpenAiChatModel.builder().options(options).build();
    }
}
