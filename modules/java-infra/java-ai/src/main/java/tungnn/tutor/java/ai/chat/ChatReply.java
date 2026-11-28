package tungnn.tutor.java.ai.chat;

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

    public boolean hasTokens() {
        return inputTokens != null || outputTokens != null;
    }
}
