package tungnn.tutor.java.tool.meddoc.core.llm;

import java.util.Objects;
import java.util.function.UnaryOperator;
import tungnn.tutor.java.claude.cli.ClaudeCliOptions;
import tungnn.tutor.java.claude.cli.ClaudeCliUtils;
import tungnn.tutor.java.claude.support.ClaudeException;

public final class ClaudeCliMarkdownChat implements MarkdownChat {

    public static final int DEFAULT_CONCURRENCY = 2;
    public static final String CONCURRENCY_ENV = "DOCTRANS_CLAUDE_CLI_CONCURRENCY";

    private final ClaudeCliOptions options;
    private final ClaudeCliUsage usage = new ClaudeCliUsage();

    public ClaudeCliMarkdownChat(ClaudeCliOptions options) {
        this.options = Objects.requireNonNull(options, "options must not be null");
    }

    public static int concurrency(UnaryOperator<String> env) {
        var raw = env.apply(CONCURRENCY_ENV);
        if (raw == null || raw.isBlank()) {
            return DEFAULT_CONCURRENCY;
        }
        try {
            return Math.max(1, Integer.parseInt(raw.strip()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for " + CONCURRENCY_ENV + ": '" + raw + "'", e);
        }
    }

    public ClaudeCliUsage usage() {
        return usage;
    }

    public String usageLine(String stepName) {
        return usage.render("%s (claude-cli:%s@%s)".formatted(stepName, options.model(), options.effort()));
    }

    @Override
    public String chat(String system, String user) {
        try {
            var result = ClaudeCliUtils.ask(system, user, options);
            usage.record(result);
            return result.text();
        } catch (ClaudeException e) {
            usage.recordFailure();
            throw e;
        }
    }
}
