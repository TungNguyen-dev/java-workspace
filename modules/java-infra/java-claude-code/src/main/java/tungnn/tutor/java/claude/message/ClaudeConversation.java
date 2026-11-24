package tungnn.tutor.java.claude.message;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageParam;
import java.util.Objects;
import tungnn.tutor.java.claude.config.ChatOptions;

public final class ClaudeConversation {
    private final AnthropicClient client;
    private final ChatOptions options;
    private final java.util.List<MessageParam> history = new java.util.ArrayList<>();

    public ClaudeConversation(AnthropicClient client, ChatOptions options) {
        this.client = Objects.requireNonNull(client, "client must not be null");
        this.options = Objects.requireNonNull(options, "options must not be null");
    }

    public String send(String prompt) {
        Objects.requireNonNull(prompt, "prompt must not be null");
        var builder = ClaudeMessageUtils.builder(options);
        history.forEach(builder::addMessage);
        builder.addUserMessage(prompt);
        Message reply = ClaudeMessageUtils.create(client, builder.build());
        history.add(MessageParam.ofUser(prompt));
        // The whole content is replayed, not just text, so thinking blocks stay valid for the next turn
        history.add(reply.toParam());
        return ClaudeMessageUtils.extractText(reply);
    }

    public int turns() {
        return history.size() / 2;
    }
}
