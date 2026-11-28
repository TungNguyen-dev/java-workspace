package tungnn.tutor.java.tool.meddoc.llm;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.prompt.Prompt;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.tool.meddoc.core.chat.MarkdownChat;

public final class LlmClientMarkdownChat implements MarkdownChat, MeteredChat {

    private static final Set<LlmProvider> CACHE_EXCLUDED_FROM_PROMPT =
            EnumSet.of(LlmProvider.ANTHROPIC, LlmProvider.BEDROCK_CONVERSE);

    private final LlmClient client;

    public LlmClientMarkdownChat(LlmClient client) {
        this.client = Objects.requireNonNull(client, "client must not be null");
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
        var response = client.chat(new Prompt(messages));
        if (response == null) {
            return ChatReply.withoutUsage(null);
        }
        var generation = response.getResult();
        var text = generation == null || generation.getOutput() == null
                ? null
                : generation.getOutput().getText();
        var usage = response.getMetadata() == null ? null : response.getMetadata().getUsage();
        return withUsage(text, usage);
    }

    public static StepChat stepChat(LlmClient client, boolean claude) {
        var label = client.provider().name().toLowerCase(Locale.ROOT) + ":" + client.model().orElse("default");
        return new StepChat(new LlmClientMarkdownChat(client), null, claude, label);
    }

    private ChatReply withUsage(String text, Usage usage) {
        if (usage == null) {
            return ChatReply.withoutUsage(text);
        }
        var prompt = usage.getPromptTokens();
        var completion = usage.getCompletionTokens();
        if ((prompt == null || prompt == 0) && (completion == null || completion == 0)) {
            return ChatReply.withoutUsage(text);
        }
        var cacheRead = orZero(usage.getCacheReadInputTokens());
        var cacheWrite = orZero(usage.getCacheWriteInputTokens());
        var input = orZero(prompt);
        if (CACHE_EXCLUDED_FROM_PROMPT.contains(client.provider())) {
            input += cacheRead + cacheWrite;
        }
        return new ChatReply(
                text,
                Math.max(input, cacheRead),
                cacheRead,
                completion == null ? null : completion.longValue(),
                null,
                null);
    }

    private static long orZero(Number value) {
        return value == null ? 0 : value.longValue();
    }
}
