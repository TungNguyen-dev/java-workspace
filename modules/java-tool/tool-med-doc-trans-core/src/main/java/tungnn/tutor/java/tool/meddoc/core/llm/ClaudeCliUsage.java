package tungnn.tutor.java.tool.meddoc.core.llm;

import java.util.Locale;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;
import tungnn.tutor.java.claude.cli.ClaudeCliResult;

public final class ClaudeCliUsage {

    private final LongAdder calls = new LongAdder();
    private final LongAdder failures = new LongAdder();
    private final DoubleAdder costUsd = new DoubleAdder();
    private final LongAdder inputTokens = new LongAdder();
    private final LongAdder cacheReadTokens = new LongAdder();
    private final LongAdder outputTokens = new LongAdder();
    private final LongAdder thinkingTokens = new LongAdder();

    public void record(ClaudeCliResult result) {
        calls.increment();
        if (result.costUsd() != null) {
            costUsd.add(result.costUsd());
        }
        inputTokens.add(result.inputTokens() + result.cacheReadTokens() + result.cacheCreationTokens());
        cacheReadTokens.add(result.cacheReadTokens());
        outputTokens.add(result.outputTokens());
        thinkingTokens.add(result.thinkingTokens());
    }

    public void recordFailure() {
        calls.increment();
        failures.increment();
    }

    public long calls() {
        return calls.sum();
    }

    public double costUsd() {
        return costUsd.sum();
    }

    public String render(String name) {
        return "%s: %d calls, %d failed, $%s, in %s (cache %s), out %s, thinking %s"
                .formatted(
                        name,
                        calls.sum(),
                        failures.sum(),
                        String.format(Locale.ROOT, "%.2f", costUsd.sum()),
                        compact(inputTokens.sum()),
                        compact(cacheReadTokens.sum()),
                        compact(outputTokens.sum()),
                        compact(thinkingTokens.sum()));
    }

    private static String compact(long value) {
        return value < 1000 ? Long.toString(value) : String.format(Locale.ROOT, "%.1fk", value / 1000.0);
    }
}
