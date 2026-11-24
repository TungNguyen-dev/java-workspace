package tungnn.tutor.java.claude.message;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.CacheControlEphemeral;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.OutputConfig;
import com.anthropic.models.messages.StopReason;
import com.anthropic.models.messages.TextBlockParam;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import tungnn.tutor.java.claude.config.ChatOptions;
import tungnn.tutor.java.claude.support.ClaudeException;

public final class ClaudeMessageUtils {
    private ClaudeMessageUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String ask(AnthropicClient client, String prompt) {
        return ask(client, prompt, ChatOptions.defaults());
    }

    public static String ask(AnthropicClient client, String prompt, ChatOptions options) {
        return extractText(create(client, buildParams(prompt, options)));
    }

    public static Message create(AnthropicClient client, MessageCreateParams params) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(params, "params must not be null");
        var message = client.messages().create(params);
        requireUsable(message);
        return message;
    }

    public static MessageCreateParams buildParams(String prompt, ChatOptions options) {
        Objects.requireNonNull(prompt, "prompt must not be null");
        return builder(options).addUserMessage(prompt).build();
    }

    public static MessageCreateParams.Builder builder(ChatOptions options) {
        Objects.requireNonNull(options, "options must not be null");
        // Effort is always set explicitly because the model default differs between Claude releases
        var builder = MessageCreateParams.builder()
                .model(options.model())
                .maxTokens(options.maxTokens())
                .outputConfig(OutputConfig.builder().effort(options.effort()).build());
        applySystem(builder, options);
        return builder;
    }

    public static String extractText(Message message) {
        Objects.requireNonNull(message, "message must not be null");
        return message.content().stream()
                .flatMap(block -> block.text().stream())
                .map(text -> text.text())
                .collect(Collectors.joining());
    }

    public static void requireUsable(Message message) {
        var reason = message.stopReason().orElse(null);
        if (reason == null) {
            return;
        }
        if (StopReason.REFUSAL.equals(reason)) {
            var detail = message.stopDetails()
                    .map(details -> details.category() + ": " + details.explanation())
                    .orElse("no details");
            throw new ClaudeException("Claude refused the request (" + detail + ")");
        }
        if (StopReason.MAX_TOKENS.equals(reason)) {
            throw new ClaudeException("Claude response was truncated at max_tokens");
        }
    }

    private static void applySystem(MessageCreateParams.Builder builder, ChatOptions options) {
        if (options.system() == null) {
            return;
        }
        if (!options.cacheSystemPrompt()) {
            builder.system(options.system());
            return;
        }
        builder.systemOfTextBlockParams(List.of(TextBlockParam.builder()
                .text(options.system())
                .cacheControl(CacheControlEphemeral.builder().build())
                .build()));
    }
}
