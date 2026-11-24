package tungnn.tutor.java.claude.stream;

import com.anthropic.client.AnthropicClient;
import com.anthropic.core.http.StreamResponse;
import com.anthropic.models.messages.RawMessageStreamEvent;
import java.util.Objects;
import java.util.function.Consumer;
import tungnn.tutor.java.claude.config.ChatOptions;
import tungnn.tutor.java.claude.message.ClaudeMessageUtils;

public final class ClaudeStreamUtils {
    private ClaudeStreamUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String stream(AnthropicClient client, String prompt, ChatOptions options, Consumer<String> onText) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(onText, "onText must not be null");
        var params = ClaudeMessageUtils.buildParams(prompt, options);
        var full = new StringBuilder();
        try (StreamResponse<RawMessageStreamEvent> response = client.messages().createStreaming(params)) {
            response.stream()
                    .flatMap(event -> event.contentBlockDelta().stream())
                    .flatMap(delta -> delta.delta().text().stream())
                    .forEach(text -> {
                        full.append(text.text());
                        onText.accept(text.text());
                    });
        }
        return full.toString();
    }
}
