package tungnn.tutor.java.tool.meddoc.llm;

import tungnn.tutor.java.claude.cli.ClaudeCliResult;

public record ChatReply(
        String text,
        Long inputTokens,
        Long cacheReadTokens,
        Long outputTokens,
        Long thinkingTokens,
        Double costUsd) {

    public static ChatReply withoutUsage(String text) {
        return new ChatReply(text, null, null, null, null, null);
    }

    public static ChatReply from(ClaudeCliResult result) {
        return new ChatReply(
                result.text(),
                result.inputTokens() + result.cacheReadTokens() + result.cacheCreationTokens(),
                result.cacheReadTokens(),
                result.outputTokens(),
                result.thinkingTokens(),
                result.costUsd());
    }

    public boolean hasTokens() {
        return inputTokens != null || outputTokens != null;
    }
}
