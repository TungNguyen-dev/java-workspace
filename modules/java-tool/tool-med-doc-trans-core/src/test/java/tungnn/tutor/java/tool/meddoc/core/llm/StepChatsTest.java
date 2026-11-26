package tungnn.tutor.java.tool.meddoc.core.llm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.meddoc.core.service.Step;

class StepChatsTest {

    @Test
    void shouldCreateEachStepChatLazilyAndOnce() {
        var created = new AtomicInteger();
        var chats = StepChats.of(step -> {
            created.incrementAndGet();
            return StepChat.of((system, user) -> step.name());
        });

        assertThat(created).hasValue(0);
        var translate = chats.forStep(Step.TRANSLATE);
        assertThat(chats.forStep(Step.TRANSLATE)).isSameAs(translate);
        assertThat(created).hasValue(1);
        assertThat(chats.forStep(Step.OCR)).isNotSameAs(translate);
        assertThat(created).hasValue(2);
    }

    @Test
    void shouldKnowIfAStepUsesClaudeWithoutCreatingItsClient() {
        var created = new AtomicInteger();
        var specs = Map.of(Step.TRANSLATE, ChatSpec.parse("claude-cli"), Step.EDIT, ChatSpec.parse("deepseek"));
        var fromSpecs = StepChats.of(specs, key -> null);

        assertThat(fromSpecs.usesClaude(Step.TRANSLATE)).isTrue();
        assertThat(fromSpecs.usesClaude(Step.EDIT)).isFalse();
        assertThat(fromSpecs.usesClaude(Step.OCR)).isFalse();

        var fromFactory = StepChats.of(step -> {
            created.incrementAndGet();
            return step == Step.TRANSLATE ? StepChat.of((system, user) -> user, true) : null;
        });
        assertThat(fromFactory.usesClaude(Step.TRANSLATE)).isTrue();
        assertThat(fromFactory.usesClaude(Step.EDIT)).isFalse();
        assertThat(created).hasValue(2);
    }

    @Test
    void shouldBuildAClaudeCliChatFromASpecWithoutStartingAProcess() {
        var chats = StepChats.of(
                Map.of(Step.TRANSLATE, ChatSpec.parse("claude-cli:haiku")),
                Map.of("DOCTRANS_CLAUDE_CLI_CONCURRENCY", "3")::get);

        var chat = chats.forStep(Step.TRANSLATE);

        assertThat(chat.chat()).isInstanceOf(ClaudeCliMarkdownChat.class);
        assertThat(chat.maxConcurrency()).isEqualTo(3);
        assertThatThrownBy(() -> chats.forStep(Step.OCR)).hasMessageContaining("No LLM configured");
    }
}
