package tungnn.tutor.java.ai.util;

import java.util.EnumSet;
import java.util.Set;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import tungnn.tutor.java.ai.chat.ChatReply;
import tungnn.tutor.java.ai.config.LlmProvider;

public final class ChatUsageUtils {

    public static final String META_COST_USD = "costUsd";
    public static final String META_THINKING_TOKENS = "thinkingTokens";
    public static final String META_CACHE_READ_TOKENS = "cacheReadTokens";

    // These providers report cached tokens separately from the prompt tokens
    private static final Set<LlmProvider> CACHE_EXCLUDED_FROM_PROMPT =
            EnumSet.of(LlmProvider.ANTHROPIC, LlmProvider.BEDROCK_CONVERSE);

    private ChatUsageUtils() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }

    public static ChatReply replyOf(ChatResponse response, LlmProvider provider) {
        if (response == null) {
            return ChatReply.withoutUsage(null);
        }
        var generation = response.getResult();
        var text = generation == null || generation.getOutput() == null
                ? null
                : generation.getOutput().getText();
        var metadata = response.getMetadata();
        var usage = metadata == null ? null : metadata.getUsage();
        return withUsage(text, usage, provider, metadata);
    }

    private static ChatReply withUsage(String text, Usage usage, LlmProvider provider, ChatResponseMetadata metadata) {
        if (usage == null) {
            return ChatReply.withoutUsage(text);
        }
        var prompt = usage.getPromptTokens();
        var completion = usage.getCompletionTokens();
        if ((prompt == null || prompt == 0) && (completion == null || completion == 0)) {
            return ChatReply.withoutUsage(text);
        }
        var cacheRead = usage.getCacheReadInputTokens() != null
                ? orZero(usage.getCacheReadInputTokens())
                : (meta(metadata, META_CACHE_READ_TOKENS) instanceof Number n ? n.longValue() : 0L);
        var cacheWrite = orZero(usage.getCacheWriteInputTokens());
        var input = orZero(prompt);
        if (CACHE_EXCLUDED_FROM_PROMPT.contains(provider)) {
            input += cacheRead + cacheWrite;
        }
        return new ChatReply(
                text,
                Math.max(input, cacheRead),
                cacheRead,
                completion == null ? null : completion.longValue(),
                meta(metadata, META_THINKING_TOKENS) instanceof Number n ? Long.valueOf(n.longValue()) : null,
                meta(metadata, META_COST_USD) instanceof Number n ? Double.valueOf(n.doubleValue()) : null);
    }

    private static Object meta(ChatResponseMetadata metadata, String key) {
        return metadata == null ? null : metadata.get(key);
    }

    private static long orZero(Number value) {
        return value == null ? 0 : value.longValue();
    }
}
