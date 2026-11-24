package tungnn.tutor.java.claude.config;

import com.anthropic.models.messages.OutputConfig;
import java.util.Objects;

public record ChatOptions(
        String model, long maxTokens, String system, OutputConfig.Effort effort, boolean cacheSystemPrompt) {
    public static final long DEFAULT_MAX_TOKENS = 16_000L;
    public static final long STREAM_MAX_TOKENS = 64_000L;

    public ChatOptions {
        Objects.requireNonNull(model, "model must not be null");
        Objects.requireNonNull(effort, "effort must not be null");
    }

    public static ChatOptions defaults() {
        return new ChatOptions(ClaudeModels.OPUS, DEFAULT_MAX_TOKENS, null, OutputConfig.Effort.HIGH, false);
    }

    public static ChatOptions of(String model) {
        return defaults().withModel(model);
    }

    public ChatOptions withModel(String newModel) {
        return new ChatOptions(newModel, maxTokens, system, effort, cacheSystemPrompt);
    }

    public ChatOptions withMaxTokens(long newMaxTokens) {
        return new ChatOptions(model, newMaxTokens, system, effort, cacheSystemPrompt);
    }

    public ChatOptions withSystem(String newSystem, boolean cache) {
        return new ChatOptions(model, maxTokens, newSystem, effort, cache);
    }

    public ChatOptions withEffort(OutputConfig.Effort newEffort) {
        return new ChatOptions(model, maxTokens, system, newEffort, cacheSystemPrompt);
    }
}
