package tungnn.tutor.java.claude.structured;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCreateParams;
import java.util.Objects;
import tungnn.tutor.java.claude.config.ChatOptions;
import tungnn.tutor.java.claude.support.ClaudeException;

public final class ClaudeStructuredUtils {
    private ClaudeStructuredUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static <T> T ask(AnthropicClient client, String prompt, Class<T> type, ChatOptions options) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(prompt, "prompt must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(options, "options must not be null");
        var params = MessageCreateParams.builder()
                .model(options.model())
                .maxTokens(options.maxTokens())
                .outputConfig(type)
                .addUserMessage(prompt)
                .build();
        return client.messages().create(params).content().stream()
                .flatMap(block -> block.text().stream())
                .map(typed -> typed.text())
                .findFirst()
                .orElseThrow(() -> new ClaudeException("Claude returned no structured output"));
    }
}
