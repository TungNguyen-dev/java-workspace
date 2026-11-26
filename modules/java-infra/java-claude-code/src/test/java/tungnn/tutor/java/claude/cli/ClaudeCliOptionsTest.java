package tungnn.tutor.java.claude.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ClaudeCliOptionsTest {

    @Test
    void shouldUseDefaultsForAnEmptyEnvironment() {
        var options = ClaudeCliOptions.fromEnv(Map.of());

        assertThat(options.executable()).isEqualTo("claude");
        assertThat(options.model()).isEqualTo("sonnet");
        assertThat(options.effort()).isEqualTo("medium");
        assertThat(options.timeout()).isEqualTo(Duration.ofMinutes(10));
        assertThat(options.workDir()).isDirectory();
    }

    @Test
    void shouldReadEveryVariable() {
        var options = ClaudeCliOptions.fromEnv(Map.of(
                "CLAUDE_CLI_PATH", "/opt/claude",
                "CLAUDE_CLI_MODEL", "haiku",
                "CLAUDE_CLI_EFFORT", "low",
                "CLAUDE_CLI_TIMEOUT_SECONDS", "30"));

        assertThat(options.executable()).isEqualTo("/opt/claude");
        assertThat(options.model()).isEqualTo("haiku");
        assertThat(options.effort()).isEqualTo("low");
        assertThat(options.timeout()).isEqualTo(Duration.ofSeconds(30));
    }

    @Test
    void shouldNameTheBadVariable() {
        assertThatThrownBy(() -> ClaudeCliOptions.fromEnv(Map.of("CLAUDE_CLI_TIMEOUT_SECONDS", "abc")))
                .hasMessageContaining("CLAUDE_CLI_TIMEOUT_SECONDS");
    }

    @Test
    void shouldOverrideTheModelOnlyWhenGiven() {
        var base = ClaudeCliOptions.fromEnv(Map.of());

        assertThat(base.withModel("sonnet").model()).isEqualTo("sonnet");
        assertThat(base.withModel(null)).isSameAs(base);
        assertThat(base.withModel(" ")).isSameAs(base);
    }

    @Test
    void shouldOverrideTheEffortOnlyWhenGiven() {
        var base = ClaudeCliOptions.fromEnv(Map.of());

        assertThat(base.withEffort(" low ").effort()).isEqualTo("low");
        assertThat(base.withEffort(null)).isSameAs(base);
        assertThat(base.withEffort(" ")).isSameAs(base);
    }
}
