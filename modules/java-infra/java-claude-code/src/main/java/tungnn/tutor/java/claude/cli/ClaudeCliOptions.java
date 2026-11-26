package tungnn.tutor.java.claude.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import tungnn.tutor.java.claude.support.ClaudeException;

public record ClaudeCliOptions(String executable, String model, String effort, Duration timeout, Path workDir) {
    public static final String PATH_ENV = "CLAUDE_CLI_PATH";
    public static final String MODEL_ENV = "CLAUDE_CLI_MODEL";
    public static final String EFFORT_ENV = "CLAUDE_CLI_EFFORT";
    public static final String TIMEOUT_SECONDS_ENV = "CLAUDE_CLI_TIMEOUT_SECONDS";

    private static final String DEFAULT_EXECUTABLE = "claude";
    private static final String DEFAULT_MODEL = "sonnet";
    private static final String DEFAULT_EFFORT = "medium";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(10);

    public ClaudeCliOptions {
        Objects.requireNonNull(executable, "executable must not be null");
        Objects.requireNonNull(model, "model must not be null");
        Objects.requireNonNull(timeout, "timeout must not be null");
        if (timeout.isNegative() || timeout.isZero()) {
            throw new IllegalArgumentException("timeout must be positive");
        }
        if (workDir == null) {
            workDir = emptyWorkDir();
        }
    }

    public static ClaudeCliOptions defaults() {
        return new ClaudeCliOptions(DEFAULT_EXECUTABLE, DEFAULT_MODEL, DEFAULT_EFFORT, DEFAULT_TIMEOUT, null);
    }

    public static ClaudeCliOptions fromEnv() {
        return fromEnv(System.getenv());
    }

    public static ClaudeCliOptions fromEnv(Map<String, String> env) {
        return fromEnv(env::get);
    }

    public static ClaudeCliOptions fromEnv(UnaryOperator<String> env) {
        return new ClaudeCliOptions(
                value(env, PATH_ENV, DEFAULT_EXECUTABLE),
                value(env, MODEL_ENV, DEFAULT_MODEL),
                value(env, EFFORT_ENV, DEFAULT_EFFORT),
                Duration.ofSeconds(seconds(env)),
                null);
    }

    public ClaudeCliOptions withModel(String newModel) {
        if (newModel == null || newModel.isBlank()) {
            return this;
        }
        return new ClaudeCliOptions(executable, newModel.strip(), effort, timeout, workDir);
    }

    public ClaudeCliOptions withEffort(String newEffort) {
        if (newEffort == null || newEffort.isBlank()) {
            return this;
        }
        return new ClaudeCliOptions(executable, model, newEffort.strip(), timeout, workDir);
    }

    private static String value(UnaryOperator<String> env, String name, String defaultValue) {
        var raw = env.apply(name);
        return raw == null || raw.isBlank() ? defaultValue : raw.strip();
    }

    private static long seconds(UnaryOperator<String> env) {
        var raw = env.apply(TIMEOUT_SECONDS_ENV);
        if (raw == null || raw.isBlank()) {
            return DEFAULT_TIMEOUT.toSeconds();
        }
        try {
            return Long.parseLong(raw.strip());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for " + TIMEOUT_SECONDS_ENV + ": '" + raw + "'", e);
        }
    }

    // An empty directory keeps project CLAUDE.md files and local settings out of the call
    private static Path emptyWorkDir() {
        try {
            var dir = Files.createTempDirectory("claude-cli-");
            dir.toFile().deleteOnExit();
            return dir;
        } catch (java.io.IOException e) {
            throw new ClaudeException("Cannot create a working directory for the Claude CLI: " + e.getMessage(), e);
        }
    }
}
