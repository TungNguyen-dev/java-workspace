package tungnn.tutor.java.claude.token;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCountTokensParams;
import java.util.Objects;
import tungnn.tutor.java.claude.config.ChatOptions;

public final class ClaudeTokenUtils {
    private ClaudeTokenUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static long countTokens(AnthropicClient client, String prompt, ChatOptions options) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(prompt, "prompt must not be null");
        Objects.requireNonNull(options, "options must not be null");
        var builder = MessageCountTokensParams.builder().model(options.model()).addUserMessage(prompt);
        if (options.system() != null) {
            builder.system(options.system());
        }
        return client.messages().countTokens(builder.build()).inputTokens();
    }
}
