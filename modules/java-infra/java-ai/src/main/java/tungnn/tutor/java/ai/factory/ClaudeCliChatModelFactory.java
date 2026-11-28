package tungnn.tutor.java.ai.factory;

import java.time.Duration;
import org.springframework.ai.chat.model.ChatModel;
import tungnn.tutor.java.ai.client.ClaudeCliChatModel;
import tungnn.tutor.java.ai.config.LlmClientProperties;
import tungnn.tutor.java.claude.cli.ClaudeCliOptions;

public final class ClaudeCliChatModelFactory implements ChatModelFactory {

    @Override
    public ChatModel create(LlmClientProperties properties) {
        var defaults = ClaudeCliOptions.defaults();
        var executable = orDefault(properties.extra(LlmClientProperties.EXTRA_EXECUTABLE), defaults.executable());
        var effort = orDefault(properties.extra(LlmClientProperties.EXTRA_EFFORT), defaults.effort());
        var options = new ClaudeCliOptions(executable, properties.model(), effort, timeout(properties, defaults), null);
        return new ClaudeCliChatModel(options);
    }

    private static Duration timeout(LlmClientProperties properties, ClaudeCliOptions defaults) {
        var raw = properties.extra(LlmClientProperties.EXTRA_TIMEOUT_SECONDS);
        if (raw == null) {
            return defaults.timeout();
        }
        try {
            return Duration.ofSeconds(Long.parseLong(raw));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid claude-cli timeout seconds: '" + raw + "'", e);
        }
    }

    private static String orDefault(String value, String fallback) {
        return value == null ? fallback : value;
    }
}
