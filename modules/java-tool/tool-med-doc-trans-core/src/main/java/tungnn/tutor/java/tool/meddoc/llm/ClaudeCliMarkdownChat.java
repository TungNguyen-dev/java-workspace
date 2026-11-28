package tungnn.tutor.java.tool.meddoc.llm;

import java.util.Objects;
import java.util.function.UnaryOperator;
import tungnn.tutor.java.claude.cli.ClaudeCliFailure;
import tungnn.tutor.java.claude.cli.ClaudeCliOptions;
import tungnn.tutor.java.claude.cli.ClaudeCliUtils;
import tungnn.tutor.java.tool.meddoc.core.chat.MarkdownChat;

public final class ClaudeCliMarkdownChat implements MarkdownChat, MeteredChat {

    public static final int DEFAULT_CONCURRENCY = 2;
    public static final String CONCURRENCY_ENV = "DOCTRANS_CLAUDE_CLI_CONCURRENCY";

    private final ClaudeCliOptions options;

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

    public String label() {
        return "claude-cli:%s@%s".formatted(options.model(), options.effort());
    }

    @Override
    public String chat(String system, String user) {
        try {
            return chatWithUsage(system, user).text();
        } catch (MeteredChatException e) {
            throw e.original();
        }
    }

    @Override
    public ChatReply chatWithUsage(String system, String user) {
        try {
            return ChatReply.from(ClaudeCliUtils.ask(system, user, options));
        } catch (ClaudeCliFailure e) {
            throw new MeteredChatException(e, ChatReply.from(e.partialResult()));
        }
    }
}
