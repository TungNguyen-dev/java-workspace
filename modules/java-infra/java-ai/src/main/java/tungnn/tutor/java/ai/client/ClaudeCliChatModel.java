package tungnn.tutor.java.ai.client;

import java.util.Objects;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.DefaultUsage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import tungnn.tutor.java.ai.chat.ChatReply;
import tungnn.tutor.java.ai.chat.MeteredChatException;
import tungnn.tutor.java.ai.util.ChatUsageUtils;
import tungnn.tutor.java.claude.cli.ClaudeCliFailure;
import tungnn.tutor.java.claude.cli.ClaudeCliOptions;
import tungnn.tutor.java.claude.cli.ClaudeCliResult;
import tungnn.tutor.java.claude.cli.ClaudeCliUtils;

public final class ClaudeCliChatModel implements ChatModel {

    private final ClaudeCliOptions options;

    public ClaudeCliChatModel(ClaudeCliOptions options) {
        this.options = Objects.requireNonNull(options, "options must not be null");
    }

    public ClaudeCliOptions options() {
        return options;
    }

    @Override
    public ChatOptions getOptions() {
        return ChatOptions.builder().model(options.model()).build();
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        var system = new StringBuilder();
        var user = new StringBuilder();
        for (var message : prompt.getInstructions()) {
            switch (message) {
                case SystemMessage m -> append(system, m.getText());
                case UserMessage m -> append(user, m.getText());
                case AssistantMessage ignored ->
                    throw new UnsupportedOperationException("Claude CLI is single-turn: assistant messages are not supported");
                default ->
                    throw new UnsupportedOperationException(
                            "Claude CLI does not support message type " + message.getMessageType());
            }
        }
        try {
            return toResponse(ClaudeCliUtils.ask(system.isEmpty() ? null : system.toString(), user.toString(), options));
        } catch (ClaudeCliFailure e) {
            throw new MeteredChatException(e, replyOf(e.partialResult()));
        }
    }

    private static void append(StringBuilder target, String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        if (!target.isEmpty()) {
            target.append("\n\n");
        }
        target.append(text);
    }

    private static ChatReply replyOf(ClaudeCliResult result) {
        if (result == null) {
            return ChatReply.withoutUsage(null);
        }
        return new ChatReply(
                result.text(),
                result.inputTokens() + result.cacheReadTokens() + result.cacheCreationTokens(),
                result.cacheReadTokens(),
                result.outputTokens(),
                result.thinkingTokens(),
                result.costUsd());
    }

    private static ChatResponse toResponse(ClaudeCliResult result) {
        var usage = new DefaultUsage(
                (int) (result.inputTokens() + result.cacheReadTokens() + result.cacheCreationTokens()),
                (int) result.outputTokens());
        var metadata = ChatResponseMetadata.builder()
                .usage(usage)
                .keyValue(ChatUsageUtils.META_THINKING_TOKENS, result.thinkingTokens())
                .keyValue(ChatUsageUtils.META_CACHE_READ_TOKENS, result.cacheReadTokens());
        if (result.costUsd() != null) {
            metadata.keyValue(ChatUsageUtils.META_COST_USD, result.costUsd());
        }
        return ChatResponse.builder()
                .generations(java.util.List.of(new Generation(new AssistantMessage(result.text()))))
                .metadata(metadata.build())
                .build();
    }
}
