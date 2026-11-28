package tungnn.tutor.java.ai.util;

import java.util.function.UnaryOperator;
import org.springframework.ai.chat.model.ChatModel;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.ai.client.SpringAiLlmClient;
import tungnn.tutor.java.ai.config.LlmClientProperties;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.config.LlmSpec;

public final class LlmClientUtils {
    private LlmClientUtils() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }

    public static LlmClient create(LlmClientProperties properties) {
        return of(ChatModelUtils.create(properties), properties.provider());
    }

    public static LlmClient create(LlmProvider provider, String modelOverride, UnaryOperator<String> env) {
        return create(new LlmSpec(provider, modelOverride, null), env);
    }

    public static LlmClient create(LlmSpec spec, UnaryOperator<String> env) {
        var provider = spec.provider();
        UnaryOperator<String> overlay = key -> {
            if (spec.model() != null && provider.modelEnv().filter(key::equals).isPresent()) {
                return spec.model();
            }
            if (spec.effort() != null
                    && key.equals(provider.extraEnv().get(LlmClientProperties.EXTRA_EFFORT))) {
                return spec.effort();
            }
            return env.apply(key);
        };
        return create(LlmClientProperties.fromEnv(provider, overlay));
    }

    public static LlmClient fromEnv(LlmProvider provider) {
        return create(LlmClientProperties.fromEnv(provider));
    }

    public static LlmClient of(ChatModel chatModel, LlmProvider provider) {
        return new SpringAiLlmClient(chatModel, provider);
    }
}
