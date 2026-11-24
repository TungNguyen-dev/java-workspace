package tungnn.tutor.java.claude.tool;

import com.anthropic.client.AnthropicClient;
import com.anthropic.helpers.BetaToolRunner;
import com.anthropic.models.beta.messages.BetaMessage;
import com.anthropic.models.beta.messages.MessageCreateParams;
import tungnn.tutor.java.claude.config.ChatOptions;

import java.util.Objects;
import java.util.function.Supplier;

public final class ClaudeToolUtils {
    private ClaudeToolUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    @SafeVarargs
    public static String runTools(
            AnthropicClient client, String prompt, ChatOptions options, Class<? extends Supplier<String>>... tools) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(prompt, "prompt must not be null");
        Objects.requireNonNull(options, "options must not be null");
        var builder = MessageCreateParams.builder()
                .model(options.model())
                .maxTokens(options.maxTokens())
                .putAdditionalHeader("anthropic-beta", "structured-outputs-2025-11-13")
                .addUserMessage(prompt);
        for (var tool : tools) {
            builder.addTool(tool);
        }
        BetaToolRunner runner = client.beta().messages().toolRunner(builder.build());
        var text = new StringBuilder();
        for (BetaMessage message : runner) {
            text.setLength(0);
            message.content().stream()
                    .flatMap(block -> block.text().stream())
                    .forEach(block -> text.append(block.text()));
        }
        return text.toString();
    }
}
