package tungnn.tutor.java.claude.cli;

public record ClaudeCliResult(
        String text,
        String stopReason,
        Double costUsd,
        long inputTokens,
        long outputTokens,
        long thinkingTokens,
        long cacheReadTokens,
        long cacheCreationTokens) {}
