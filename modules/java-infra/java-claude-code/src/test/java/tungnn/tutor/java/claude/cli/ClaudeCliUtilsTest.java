package tungnn.tutor.java.claude.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.claude.support.ClaudeException;

class ClaudeCliUtilsTest {

    @TempDir
    Path dir;

    @BeforeEach
    void requirePosix() {
        assumeTrue(!System.getProperty("os.name").toLowerCase().contains("win"));
    }

    private ClaudeCliOptions stub(String body, Duration timeout) throws IOException {
        var script = dir.resolve("claude-stub.sh");
        Files.writeString(script, "#!/bin/sh\n" + body + "\n");
        Files.setPosixFilePermissions(script, PosixFilePermissions.fromString("rwxr-xr-x"));
        return new ClaudeCliOptions(script.toString(), "opus", "high", timeout, dir);
    }

    @Test
    void shouldReturnTheResultAndPassPromptsThroughFileAndStdin() throws IOException {
        var options = stub("""
            cat > "%s/stdin.txt"
            for a in "$@"; do last="$prev"; prev="$a"; if [ "$last" = "--system-prompt-file" ]; then cp "$a" "%s/system.txt"; fi; done
            echo '{"type":"result","subtype":"success","is_error":false,"result":"xin chao","stop_reason":"end_turn","total_cost_usd":0.5,"usage":{"input_tokens":3,"output_tokens":4,"cache_read_input_tokens":5,"cache_creation_input_tokens":6,"output_tokens_details":{"thinking_tokens":7}}}'
            """.formatted(dir, dir), Duration.ofSeconds(20));

        var result = ClaudeCliUtils.ask("SYSTEM", "USER", options);

        assertThat(result.text()).isEqualTo("xin chao");
        assertThat(result.stopReason()).isEqualTo("end_turn");
        assertThat(result.costUsd()).isEqualTo(0.5);
        assertThat(result.inputTokens()).isEqualTo(3);
        assertThat(result.outputTokens()).isEqualTo(4);
        assertThat(result.cacheReadTokens()).isEqualTo(5);
        assertThat(result.cacheCreationTokens()).isEqualTo(6);
        assertThat(result.thinkingTokens()).isEqualTo(7);
        assertThat(Files.readString(dir.resolve("stdin.txt"))).isEqualTo("USER");
        assertThat(Files.readString(dir.resolve("system.txt"))).isEqualTo("SYSTEM");
    }

    @Test
    void shouldFailOnAnErrorResult() throws IOException {
        var options = stub(
                "cat >/dev/null; echo '{\"subtype\":\"success\",\"is_error\":true,\"result\":\"usage"
                        + " limit\",\"api_error_status\":429}'",
                Duration.ofSeconds(20));

        assertThatThrownBy(() -> ClaudeCliUtils.ask("s", "u", options))
                .isInstanceOf(ClaudeException.class)
                .hasMessageContaining("usage limit");
    }

    @Test
    void shouldFailOnTruncation() throws IOException {
        var options = stub(
                "cat >/dev/null; echo"
                        + " '{\"subtype\":\"success\",\"is_error\":false,\"result\":\"cut\",\"stop_reason\":\"max_tokens\"}'",
                Duration.ofSeconds(20));

        assertThatThrownBy(() -> ClaudeCliUtils.ask("s", "u", options))
                .isInstanceOf(ClaudeException.class)
                .hasMessageContaining("max_tokens");
    }

    @Test
    void shouldKeepTheUsageOfAFailedCall() {
        var json = "{\"subtype\":\"success\",\"is_error\":false,\"result\":\"cut\",\"stop_reason\":\"max_tokens\","
                + "\"total_cost_usd\":0.02,\"usage\":{\"input_tokens\":10,\"output_tokens\":300}}";

        assertThatThrownBy(() -> ClaudeCliUtils.parse(json))
                .isInstanceOfSatisfying(ClaudeCliFailure.class, e -> {
                    assertThat(e.partialResult().outputTokens()).isEqualTo(300);
                    assertThat(e.partialResult().costUsd()).isEqualTo(0.02);
                });
    }

    @Test
    void shouldFailOnANonZeroExit() throws IOException {
        var options = stub("cat >/dev/null; echo boom >&2; exit 3", Duration.ofSeconds(20));

        assertThatThrownBy(() -> ClaudeCliUtils.ask("s", "u", options))
                .isInstanceOf(ClaudeException.class)
                .hasMessageContaining("code 3")
                .hasMessageContaining("boom");
    }

    @Test
    void shouldExplainANonZeroExitWithAJsonResult() throws IOException {
        var options = stub(
                "cat >/dev/null; echo '{\"subtype\":\"error_during_execution\",\"result\":\"You have hit your usage"
                        + " limit\",\"api_error_status\":429}'; exit 1",
                Duration.ofSeconds(20));

        assertThatThrownBy(() -> ClaudeCliUtils.ask("s", "u", options))
                .isInstanceOf(ClaudeException.class)
                .hasMessageContaining("code 1")
                .hasMessageContaining("api_status=429")
                .hasMessageContaining("You have hit your usage limit");
    }

    @Test
    void shouldKillTheProcessOnTimeout() throws IOException {
        var options = stub("sleep 30", Duration.ofSeconds(1));

        assertThatThrownBy(() -> ClaudeCliUtils.ask("s", "u", options))
                .isInstanceOf(ClaudeException.class)
                .hasMessageContaining("timed out");
    }

    @Test
    void shouldExplainAMissingExecutable() {
        var options =
                new ClaudeCliOptions(dir.resolve("missing").toString(), "opus", "high", Duration.ofSeconds(5), dir);

        assertThatThrownBy(() -> ClaudeCliUtils.ask("s", "u", options))
                .isInstanceOf(ClaudeException.class)
                .hasMessageContaining(ClaudeCliOptions.PATH_ENV);
    }
}
