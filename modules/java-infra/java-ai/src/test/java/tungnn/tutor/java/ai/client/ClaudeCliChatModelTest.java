package tungnn.tutor.java.ai.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import tungnn.tutor.java.ai.config.LlmClientProperties;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.util.ChatModelUtils;
import tungnn.tutor.java.claude.cli.ClaudeCliOptions;

class ClaudeCliChatModelTest {

    @Test
    void shouldRejectAssistantTurnsBecauseTheCliIsSingleTurn() {
        var model = new ClaudeCliChatModel(ClaudeCliOptions.defaults());
        var prompt = new Prompt(List.of(new UserMessage("hi"), new AssistantMessage("hello")));

        assertThatThrownBy(() -> model.call(prompt)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldBuildTheModelFromProviderPropertiesWithEffort() {
        var properties = LlmClientProperties.fromEnv(
                LlmProvider.CLAUDE_CLI,
                key -> switch (key) {
                    case "CLAUDE_CLI_MODEL" -> "opus";
                    case "CLAUDE_CLI_EFFORT" -> "low";
                    default -> null;
                });

        var model = (ClaudeCliChatModel) ChatModelUtils.create(properties);

        assertThat(model.options().model()).isEqualTo("opus");
        assertThat(model.options().effort()).isEqualTo("low");
    }
}
