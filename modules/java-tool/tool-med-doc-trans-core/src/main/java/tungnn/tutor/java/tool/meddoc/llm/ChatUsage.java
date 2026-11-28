package tungnn.tutor.java.tool.meddoc.llm;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

public final class ChatUsage {

    private final LongAdder calls = new LongAdder();
    private final LongAdder failures = new LongAdder();
    private final LongAdder unreportedCalls = new LongAdder();
    private final DoubleAdder costUsd = new DoubleAdder();
    private final AtomicBoolean costKnown = new AtomicBoolean();
    private final LongAdder inputTokens = new LongAdder();
    private final LongAdder cacheReadTokens = new LongAdder();
    private final LongAdder outputTokens = new LongAdder();
    private final LongAdder thinkingTokens = new LongAdder();

    public void record(ChatReply reply) {
        calls.increment();
        addTokensAndCost(reply);
    }

    public void recordFailure(ChatReply partial) {
        calls.increment();
        failures.increment();
        addTokensAndCost(partial);
    }

    private void addTokensAndCost(ChatReply reply) {
        if (reply.costUsd() != null) {
            costUsd.add(reply.costUsd());
            costKnown.set(true);
        }
        if (!reply.hasTokens()) {
            unreportedCalls.increment();
            return;
        }
        inputTokens.add(orZero(reply.inputTokens()));
        cacheReadTokens.add(orZero(reply.cacheReadTokens()));
        outputTokens.add(orZero(reply.outputTokens()));
        thinkingTokens.add(orZero(reply.thinkingTokens()));
    }

    public void recordUnreported() {
        calls.increment();
        unreportedCalls.increment();
    }

    public void recordFailure() {
        calls.increment();
        failures.increment();
    }

    public void add(ChatUsage other) {
        calls.add(other.calls.sum());
        failures.add(other.failures.sum());
        unreportedCalls.add(other.unreportedCalls.sum());
        costUsd.add(other.costUsd.sum());
        if (other.costKnown.get()) {
            costKnown.set(true);
        }
        inputTokens.add(other.inputTokens.sum());
        cacheReadTokens.add(other.cacheReadTokens.sum());
        outputTokens.add(other.outputTokens.sum());
        thinkingTokens.add(other.thinkingTokens.sum());
    }

    public long calls() {
        return calls.sum();
    }

    public long failures() {
        return failures.sum();
    }

    public long unreportedCalls() {
        return unreportedCalls.sum();
    }

    public double costUsd() {
        return costUsd.sum();
    }

    public long inputTokens() {
        return inputTokens.sum();
    }

    public long outputTokens() {
        return outputTokens.sum();
    }

    public String render(String name) {
        var cost = costKnown.get() ? "$" + String.format(Locale.ROOT, "%.2f", costUsd.sum()) : "$n/a";
        var reported = calls.sum() - failures.sum() - unreportedCalls.sum();
        if (reported <= 0 && unreportedCalls.sum() > 0 && inputTokens.sum() == 0 && outputTokens.sum() == 0) {
            return "%s: %d calls, %d failed, %s, tokens not reported by provider"
                    .formatted(name, calls.sum(), failures.sum(), cost);
        }
        var text = "%s: %d calls, %d failed, %s, in %s (cache %s), out %s, thinking %s"
                .formatted(
                        name,
                        calls.sum(),
                        failures.sum(),
                        cost,
                        compact(inputTokens.sum()),
                        compact(cacheReadTokens.sum()),
                        compact(outputTokens.sum()),
                        compact(thinkingTokens.sum()));
        return unreportedCalls.sum() > 0 ? text + ", " + unreportedCalls.sum() + " calls without token data" : text;
    }

    private static long orZero(Long value) {
        return value == null ? 0 : value;
    }

    private static String compact(long value) {
        return value < 1000 ? Long.toString(value) : String.format(Locale.ROOT, "%.1fk", value / 1000.0);
    }
}
