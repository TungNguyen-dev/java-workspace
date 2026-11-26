package tungnn.tutor.java.claude.cli;

import com.anthropic.core.ObjectMappers;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.claude.support.ClaudeException;

public final class ClaudeCliUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClaudeCliUtils.class);
    private static final int MAX_DETAIL_CHARS = 500;

    private ClaudeCliUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ClaudeCliResult ask(String system, String user, ClaudeCliOptions options) {
        Objects.requireNonNull(user, "user must not be null");
        Objects.requireNonNull(options, "options must not be null");
        var systemFile = writeSystemPrompt(system == null ? "" : system);
        try {
            var output = run(systemFile.toString(), user, options);
            return parse(output);
        } finally {
            try {
                Files.deleteIfExists(systemFile);
            } catch (IOException e) {
                LOGGER.debug("Cannot delete {}: {}", systemFile, e.getMessage());
            }
        }
    }

    static ClaudeCliResult parse(String stdout) {
        JsonNode node;
        try {
            node = ObjectMappers.jsonMapper().readTree(stdout);
        } catch (IOException e) {
            throw new ClaudeException("Claude CLI returned unreadable output: " + abbreviate(stdout), e);
        }
        var text = node.path("result").asText("");
        var subtype = node.path("subtype").asText("");
        if (node.path("is_error").asBoolean(false) || (!subtype.isEmpty() && !"success".equals(subtype))) {
            throw new ClaudeException("Claude CLI failed (subtype=%s, api_status=%s): %s"
                    .formatted(subtype, node.path("api_error_status").asText("none"), abbreviate(text)));
        }
        var stopReason = node.path("stop_reason").asText(null);
        if ("max_tokens".equals(stopReason)) {
            throw new ClaudeException("Claude response was truncated at max_tokens");
        }
        if (text.isBlank()) {
            throw new ClaudeException("Claude CLI returned an empty result");
        }
        var usage = node.path("usage");
        var cost =
                node.hasNonNull("total_cost_usd") ? node.get("total_cost_usd").asDouble() : null;
        var result = new ClaudeCliResult(
                text,
                stopReason,
                cost,
                usage.path("input_tokens").asLong(0),
                usage.path("output_tokens").asLong(0),
                usage.path("output_tokens_details").path("thinking_tokens").asLong(0),
                usage.path("cache_read_input_tokens").asLong(0),
                usage.path("cache_creation_input_tokens").asLong(0));
        LOGGER.debug(
                "Claude CLI call done [cost={}, in={}, out={}]", cost, result.inputTokens(), result.outputTokens());
        return result;
    }

    private static String run(String systemFile, String user, ClaudeCliOptions options) {
        var command = new ArrayList<>(List.of(
                options.executable(),
                "-p",
                "--model",
                options.model(),
                "--system-prompt-file",
                systemFile,
                "--tools",
                "",
                "--strict-mcp-config",
                "--setting-sources",
                "",
                "--disable-slash-commands",
                "--no-session-persistence",
                "--output-format",
                "json"));
        if (options.effort() != null && !options.effort().isBlank()) {
            command.addAll(List.of("--effort", options.effort()));
        }
        Process process;
        try {
            process = new ProcessBuilder(command)
                    .directory(options.workDir().toFile())
                    .start();
        } catch (IOException e) {
            throw new ClaudeException(
                    "Cannot start the Claude CLI '%s' (set %s): %s"
                            .formatted(options.executable(), ClaudeCliOptions.PATH_ENV, e.getMessage()),
                    e);
        }
        var stdout = new ByteArrayOutputStream();
        var stderr = new ByteArrayOutputStream();
        var outReader = pump(process.getInputStream(), stdout);
        var errReader = pump(process.getErrorStream(), stderr);
        try {
            try (var stdin = process.getOutputStream()) {
                stdin.write(user.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                // the process may have exited early; its exit code and stderr explain why
                LOGGER.debug("Cannot write the prompt to the Claude CLI: {}", e.getMessage());
            }
            if (!process.waitFor(options.timeout().toMillis(), TimeUnit.MILLISECONDS)) {
                process.destroyForcibly();
                throw new ClaudeException("Claude CLI timed out after " + options.timeout());
            }
            outReader.join();
            errReader.join();
        } catch (InterruptedException e) {
            process.destroyForcibly();
            Thread.currentThread().interrupt();
            throw new ClaudeException("Interrupted while waiting for the Claude CLI", e);
        }
        var out = stdout.toString(StandardCharsets.UTF_8);
        if (process.exitValue() != 0) {
            throw new ClaudeException(
                    failureMessage(process.exitValue(), out, stderr.toString(StandardCharsets.UTF_8)));
        }
        return out;
    }

    private static String failureMessage(int exitCode, String stdout, String stderr) {
        try {
            var node = ObjectMappers.jsonMapper().readTree(stdout);
            var result = node.path("result").asText("");
            if (node.isObject() && !result.isBlank()) {
                return "Claude CLI exited with code %d (subtype=%s, api_status=%s): %s"
                        .formatted(
                                exitCode,
                                node.path("subtype").asText(""),
                                node.path("api_error_status").asText("none"),
                                abbreviate(result));
            }
        } catch (IOException e) {
            LOGGER.debug("The Claude CLI error output is not JSON: {}", e.getMessage());
        }
        return "Claude CLI exited with code %d: %s".formatted(exitCode, abbreviate(stdout.isBlank() ? stderr : stdout));
    }

    private static Thread pump(java.io.InputStream in, ByteArrayOutputStream sink) {
        var thread = Thread.ofVirtual().unstarted(() -> {
            try (in) {
                in.transferTo(sink);
            } catch (IOException e) {
                LOGGER.debug("Cannot read the Claude CLI output: {}", e.getMessage());
            }
        });
        thread.start();
        return thread;
    }

    private static java.nio.file.Path writeSystemPrompt(String system) {
        try {
            var file = Files.createTempFile("claude-system-", ".md");
            Files.writeString(file, system, StandardCharsets.UTF_8);
            return file;
        } catch (IOException e) {
            throw new ClaudeException("Cannot write the system prompt file: " + e.getMessage(), e);
        }
    }

    private static String abbreviate(String text) {
        var value = text == null ? "" : text.strip();
        return value.length() <= MAX_DETAIL_CHARS ? value : value.substring(0, MAX_DETAIL_CHARS) + "...";
    }
}
