package tungnn.tutor.java.ai.config;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public record LlmSpec(LlmProvider provider, String model, String effort) {
    public static final List<String> EFFORTS = List.of("low", "medium", "high", "xhigh", "max");

    public LlmSpec {
        Objects.requireNonNull(provider, "provider must not be null");
        model = model == null || model.isBlank() ? null : model.strip();
        effort = effort == null || effort.isBlank() ? null : effort.strip().toLowerCase(Locale.ROOT);
        if (effort != null && provider != LlmProvider.CLAUDE_CLI) {
            throw new IllegalArgumentException("Only claude-cli takes an effort, got '" + effort + "' for " + provider);
        }
    }

    public LlmSpec(LlmProvider provider, String model) {
        this(provider, model, null);
    }

    public static LlmSpec parse(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("LLM spec must not be blank");
        }
        var separator = text.indexOf(':');
        var head = (separator < 0 ? text : text.substring(0, separator)).strip().toLowerCase(Locale.ROOT);
        var tail = separator < 0 ? null : text.substring(separator + 1);
        // Only claude-cli takes an effort, written after the last '@': claude-cli@low or claude-cli:opus@low
        var headEffort = separator < 0 && head.startsWith(LlmProvider.CLAUDE_CLI.cliName() + "@");
        var provider = LlmProvider.from(headEffort ? LlmProvider.CLAUDE_CLI.cliName() : head);
        if (provider != LlmProvider.CLAUDE_CLI) {
            return new LlmSpec(provider, tail, null);
        }
        var effortSource = headEffort ? head : tail;
        var at = effortSource == null ? -1 : effortSource.lastIndexOf('@');
        if (at < 0) {
            return new LlmSpec(provider, tail, null);
        }
        var effort = effortSource.substring(at + 1).strip().toLowerCase(Locale.ROOT);
        if (!EFFORTS.contains(effort)) {
            throw new IllegalArgumentException(
                    "Unknown effort '" + effort + "' in LLM spec '" + text + "', expected one of " + EFFORTS);
        }
        return new LlmSpec(provider, headEffort ? null : tail.substring(0, at), effort);
    }

    public boolean isClaudeCli() {
        return provider == LlmProvider.CLAUDE_CLI;
    }

    public boolean isClaude() {
        return provider.isClaude();
    }

    @Override
    public String toString() {
        var text = model == null ? provider.cliName() : provider.cliName() + ":" + model;
        return effort == null ? text : text + "@" + effort;
    }
}
