package tungnn.tutor.java.claude.config;

import java.time.Duration;

public record ClaudeClientOptions(String apiKey, String baseUrl, Duration timeout, Integer maxRetries) {
    public static ClaudeClientOptions of(String apiKey) {
        return new ClaudeClientOptions(apiKey, null, null, null);
    }
}
