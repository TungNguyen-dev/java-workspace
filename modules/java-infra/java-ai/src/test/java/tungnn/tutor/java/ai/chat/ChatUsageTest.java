package tungnn.tutor.java.ai.chat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ChatUsageTest {

    @Test
    void shouldAddUpCallsCostAndTokens() {
        var usage = new ChatUsage();

        usage.record(new ChatReply("a", (long) 2 + 4000 + 662, (long) 4000, (long) 1000, (long) 300, 0.5));
        usage.record(new ChatReply("b", (long) 3 + 0 + 0, (long) 0, (long) 500, (long) 0, 0.25));
        usage.recordFailure();

        assertThat(usage.calls()).isEqualTo(3);
        assertThat(usage.costUsd()).isEqualTo(0.75);
        assertThat(usage.render("translate (claude-cli:sonnet@medium)"))
                .isEqualTo("translate (claude-cli:sonnet@medium): 3 calls, 1 failed, $0.75, in 4.7k (cache 4.0k),"
                        + " out 1.5k, thinking 300");
    }

    @Test
    void shouldIgnoreAMissingCost() {
        var usage = new ChatUsage();

        usage.record(new ChatReply("a", (long) 1 + 0 + 0, (long) 0, (long) 2, (long) 0, null));

        assertThat(usage.costUsd()).isZero();
        assertThat(usage.render("ocr")).isEqualTo("ocr: 1 calls, 0 failed, $n/a, in 1 (cache 0), out 2, thinking 0");
    }

    @Test
    void shouldShowUnknownCostAndMissingTokenData() {
        var usage = new ChatUsage();

        usage.recordUnreported();
        usage.recordUnreported();

        assertThat(usage.render("ocr")).isEqualTo("ocr: 2 calls, 0 failed, $n/a, tokens not reported by provider");

        usage.record(new ChatReply("a", 1000L, 0L, 200L, null, null));
        assertThat(usage.render("ocr"))
                .isEqualTo("ocr: 3 calls, 0 failed, $n/a, in 1.0k (cache 0), out 200, thinking 0,"
                        + " 2 calls without token data");
    }

    @Test
    void shouldCountTokensOfAFailedCall() {
        var usage = new ChatUsage();

        usage.recordFailure(new ChatReply(null, 800L, 0L, 40L, 0L, 0.01));

        assertThat(usage.render("edit"))
                .isEqualTo("edit: 1 calls, 1 failed, $0.01, in 800 (cache 0), out 40, thinking 0");
    }
}
