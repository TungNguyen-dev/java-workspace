package tungnn.tutor.java.tool.meddoc.llm;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import tungnn.tutor.java.ai.config.LlmProvider;

public record ChatSpec(String backend, String model, String effort) {
    public static final String CLAUDE_CLI = "claude-cli";
    public static final String ANTHROPIC = "anthropic";
    public static final List<String> EFFORTS = List.of("low", "medium", "high", "xhigh", "max");

    public ChatSpec {
        Objects.requireNonNull(backend, "backend must not be null");
        model = model == null || model.isBlank() ? null : model.strip();
        effort = effort == null || effort.isBlank() ? null : effort.strip().toLowerCase(Locale.ROOT);
    }

    public ChatSpec(String backend, String model) {
        this(backend, model, null);
    }

    public static ChatSpec parse(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("LLM spec must not be blank");
        }
        var separator = text.indexOf(':');
        var backend =
                (separator < 0 ? text : text.substring(0, separator)).strip().toLowerCase(Locale.ROOT);
        var model = separator < 0 ? null : text.substring(separator + 1);
        // Only claude-cli takes an effort, written after the last '@': claude-cli@low or claude-cli:opus@low
        var withoutModel = separator < 0 && backend.startsWith(CLAUDE_CLI + "@");
        if (!CLAUDE_CLI.equals(backend) && !withoutModel) {
            LlmProvider.from(backend);
            return new ChatSpec(backend, model, null);
        }
        var effortSource = withoutModel ? backend : model;
        var at = effortSource == null ? -1 : effortSource.lastIndexOf('@');
        if (at < 0) {
            return new ChatSpec(backend, model, null);
        }
        var effort = effortSource.substring(at + 1).strip().toLowerCase(Locale.ROOT);
        if (!EFFORTS.contains(effort)) {
            throw new IllegalArgumentException(
                    "Unknown effort '" + effort + "' in LLM spec '" + text + "', expected one of " + EFFORTS);
        }
        return withoutModel
                ? new ChatSpec(CLAUDE_CLI, null, effort)
                : new ChatSpec(backend, model.substring(0, at), effort);
    }

    public boolean isClaudeCli() {
        return CLAUDE_CLI.equals(backend);
    }

    public boolean isClaude() {
        return isClaudeCli() || ANTHROPIC.equals(backend);
    }

    public LlmProvider provider() {
        return LlmProvider.from(backend);
    }

    @Override
    public String toString() {
        var text = model == null ? backend : backend + ":" + model;
        return effort == null ? text : text + "@" + effort;
    }
}
