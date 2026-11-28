package tungnn.tutor.java.tool.meddoc.llm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.meddoc.core.Step;
import tungnn.tutor.java.tool.meddoc.core.chat.MarkdownChat;

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

        assertThat(((MeteringMarkdownChat) chat.chat()).delegate()).isInstanceOf(ClaudeCliMarkdownChat.class);
        assertThat(chat.maxConcurrency()).isEqualTo(3);
        assertThatThrownBy(() -> chats.forStep(Step.OCR)).hasMessageContaining("No LLM configured");
    }

    @Test
    void shouldCountUsagePerStepEvenWhenTheFactoryReturnsOneSharedChat() {
        MarkdownChat shared = (system, user) -> "ok";
        var chats = StepChats.of(step -> StepChat.of(shared));

        chats.forStep(Step.OCR).chat().chat("s", "u");
        chats.forStep(Step.TRANSLATE).chat().chat("s", "u");
        chats.forStep(Step.TRANSLATE).chat().chat("s", "u");

        assertThat(chats.usageLine(Step.OCR)).contains("1 calls").contains("tokens not reported by provider");
        assertThat(chats.usageLine(Step.TRANSLATE)).contains("2 calls");
        assertThat(chats.usageLine(Step.EDIT)).isNull();
        assertThat(chats.usageTotal()).contains("3 calls");
    }

    @Test
    void shouldRecordTokensOfAMeteredChatAndFailures() {
        class Metered implements MarkdownChat, MeteredChat {
            @Override
            public String chat(String system, String user) {
                return chatWithUsage(system, user).text();
            }

            @Override
            public ChatReply chatWithUsage(String system, String user) {
                if ("boom".equals(user)) {
                    throw new IllegalStateException("boom");
                }
                return new ChatReply("ok", 1500L, 0L, 500L, null, null);
            }
        }
        var chats = StepChats.of(step -> new StepChat(new Metered(), null, false, "deepseek:deepseek-chat"));
        var chat = chats.forStep(Step.EDIT).chat();

        chat.chat("s", "u");
        assertThatThrownBy(() -> chat.chat("s", "boom")).isInstanceOf(IllegalStateException.class);

        assertThat(chats.usageLine(Step.EDIT))
                .isEqualTo("edit (deepseek:deepseek-chat): 2 calls, 1 failed, $n/a, in 1.5k (cache 0),"
                        + " out 500, thinking 0");
    }

    @Test
    void shouldKeepTheTokensOfAFailedMeteredCall() {
        class Failing implements MarkdownChat, MeteredChat {
            @Override
            public String chat(String system, String user) {
                return chatWithUsage(system, user).text();
            }

            @Override
            public ChatReply chatWithUsage(String system, String user) {
                throw new MeteredChatException(
                        new IllegalStateException("truncated"), new ChatReply(null, 900L, 0L, 300L, 0L, 0.02));
            }
        }
        var chats = StepChats.of(step -> new StepChat(new Failing(), null, false, "claude-cli:sonnet@medium"));
        var chat = chats.forStep(Step.TRANSLATE).chat();

        assertThatThrownBy(() -> chat.chat("s", "u"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("truncated");

        assertThat(chats.usageLine(Step.TRANSLATE))
                .isEqualTo("translate (claude-cli:sonnet@medium): 1 calls, 1 failed, $0.02, in 900 (cache 0),"
                        + " out 300, thinking 0");
    }
}
