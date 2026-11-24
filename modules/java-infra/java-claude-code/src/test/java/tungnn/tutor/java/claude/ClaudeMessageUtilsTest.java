package tungnn.tutor.java.claude;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.anthropic.core.ObjectMappers;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.OutputConfig;
import com.anthropic.models.messages.StopReason;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.claude.client.ClaudeClientFactory;
import tungnn.tutor.java.claude.config.ChatOptions;
import tungnn.tutor.java.claude.config.ClaudeModels;
import tungnn.tutor.java.claude.message.ClaudeMessageUtils;
import tungnn.tutor.java.claude.support.ClaudeException;

class ClaudeMessageUtilsTest {
    private static Message message(String text, StopReason reason) {
        var json = "{\"id\":\"msg_1\",\"type\":\"message\",\"role\":\"assistant\",\"model\":\"%s\","
                        .formatted(ClaudeModels.OPUS)
                + "\"content\":[{\"type\":\"text\",\"text\":\"%s\"}],".formatted(text)
                + "\"stop_reason\":\"%s\"}".formatted(reason.asString());
        try {
            return ObjectMappers.jsonMapper().readValue(json, Message.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    void buildParamsAppliesOptions() {
        var options = ChatOptions.of(ClaudeModels.HAIKU)
                .withMaxTokens(123)
                .withEffort(OutputConfig.Effort.LOW)
                .withSystem("be brief", true);
        var params = ClaudeMessageUtils.buildParams("hi", options);
        assertThat(params.model().asString()).isEqualTo(ClaudeModels.HAIKU);
        assertThat(params.maxTokens()).isEqualTo(123);
        assertThat(params.outputConfig().orElseThrow().effort().orElseThrow()).isEqualTo(OutputConfig.Effort.LOW);
        assertThat(params.system().orElseThrow().isTextBlockParams()).isTrue();
    }

    @Test
    void extractTextJoinsTextBlocks() {
        assertThat(ClaudeMessageUtils.extractText(message("pong", StopReason.END_TURN)))
                .isEqualTo("pong");
    }

    @Test
    void requireUsableRejectsTruncatedResponse() {
        assertThatThrownBy(() -> ClaudeMessageUtils.requireUsable(message("x", StopReason.MAX_TOKENS)))
                .isInstanceOf(ClaudeException.class);
    }

    @Test
    void factoryBuildsClientWithExplicitKey() {
        assertThat(ClaudeClientFactory.create("test-key")).isNotNull();
    }
}
