package tungnn.tutor.java.ai.factory;

import org.springframework.ai.bedrock.converse.BedrockChatOptions;
import org.springframework.ai.bedrock.converse.BedrockProxyChatModel;
import org.springframework.ai.chat.model.ChatModel;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import tungnn.tutor.java.ai.config.LlmClientProperties;

public final class BedrockConverseChatModelFactory implements ChatModelFactory {
    private static Region resolveRegion(LlmClientProperties properties) {
        var configured = properties.extra(LlmClientProperties.EXTRA_REGION);
        if (configured != null) {
            return Region.of(configured);
        }
        try {
            return new DefaultAwsRegionProviderChain().getRegion();
        } catch (SdkClientException e) {
            throw new IllegalArgumentException(
                    "BEDROCK_CONVERSE: AWS region could not be resolved (set AWS_REGION)", e);
        }
    }

    @Override
    public ChatModel create(LlmClientProperties properties) {
        var options = BedrockChatOptions.builder()
                .model(properties.model())
                .temperature(properties.temperature())
                .maxTokens(properties.maxTokens())
                .build();
        var builder = BedrockProxyChatModel.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .timeout(properties.timeout())
                .options(options);
        builder.region(resolveRegion(properties));
        return builder.build();
    }
}
