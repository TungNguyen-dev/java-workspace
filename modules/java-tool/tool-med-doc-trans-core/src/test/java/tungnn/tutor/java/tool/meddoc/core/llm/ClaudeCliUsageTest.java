package tungnn.tutor.java.tool.meddoc.core.llm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tungnn.tutor.java.claude.cli.ClaudeCliResult;

class ClaudeCliUsageTest {

    @Test
    void shouldAddUpCallsCostAndTokens() {
        var usage = new ClaudeCliUsage();

        usage.record(new ClaudeCliResult("a", "end_turn", 0.5, 2, 1000, 300, 4000, 662));
        usage.record(new ClaudeCliResult("b", "end_turn", 0.25, 3, 500, 0, 0, 0));
        usage.recordFailure();

        assertThat(usage.calls()).isEqualTo(3);
        assertThat(usage.costUsd()).isEqualTo(0.75);
        assertThat(usage.render("translate (claude-cli:sonnet@medium)"))
                .isEqualTo("translate (claude-cli:sonnet@medium): 3 calls, 1 failed, $0.75, in 4.7k (cache 4.0k),"
                        + " out 1.5k, thinking 300");
    }

    @Test
    void shouldIgnoreAMissingCost() {
        var usage = new ClaudeCliUsage();

        usage.record(new ClaudeCliResult("a", null, null, 1, 2, 0, 0, 0));

        assertThat(usage.costUsd()).isZero();
        assertThat(usage.render("ocr")).isEqualTo("ocr: 1 calls, 0 failed, $0.00, in 1 (cache 0), out 2, thinking 0");
    }
}
