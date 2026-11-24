package tungnn.tutor.java.claude.client;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import java.util.Objects;
import tungnn.tutor.java.claude.config.ClaudeClientOptions;

public final class ClaudeClientFactory {
    private ClaudeClientFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static AnthropicClient create() {
        return AnthropicOkHttpClient.fromEnv();
    }

    public static AnthropicClient create(String apiKey) {
        Objects.requireNonNull(apiKey, "apiKey must not be null");
        return create(ClaudeClientOptions.of(apiKey));
    }

    public static AnthropicClient create(ClaudeClientOptions options) {
        Objects.requireNonNull(options, "options must not be null");
        var builder = AnthropicOkHttpClient.builder();
        if (options.apiKey() != null) {
            builder.apiKey(options.apiKey());
        }
        if (options.baseUrl() != null) {
            builder.baseUrl(options.baseUrl());
        }
        if (options.timeout() != null) {
            builder.timeout(options.timeout());
        }
        if (options.maxRetries() != null) {
            builder.maxRetries(options.maxRetries());
        }
        return builder.build();
    }
}
