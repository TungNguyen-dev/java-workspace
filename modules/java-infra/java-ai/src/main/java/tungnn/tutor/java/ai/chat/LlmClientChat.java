package tungnn.tutor.java.ai.chat;

import java.util.ArrayList;
import java.util.Objects;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.ai.util.ChatUsageUtils;

public final class LlmClientChat implements TextChat, MeteredChat {

    private final LlmClient client;

    public LlmClientChat(LlmClient client) {
        this.client = Objects.requireNonNull(client, "client must not be null");
    }

    public String label() {
        return client.provider().cliName() + ":" + client.model().orElse("default");
    }

    @Override
    public String chat(String system, String user) {
        return chatWithUsage(system, user).text();
    }

    @Override
    public ChatReply chatWithUsage(String system, String user) {
        Objects.requireNonNull(user, "user message must not be null");
        var messages = new ArrayList<Message>();
        if (system != null && !system.isBlank()) {
            messages.add(new SystemMessage(system));
        }
        messages.add(new UserMessage(user));
        return ChatUsageUtils.replyOf(client.chat(new Prompt(messages)), client.provider());
    }
}
