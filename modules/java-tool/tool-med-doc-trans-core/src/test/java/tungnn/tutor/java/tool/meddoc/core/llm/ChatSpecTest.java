package tungnn.tutor.java.tool.meddoc.core.llm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ChatSpecTest {

    @Test
    void shouldParseBackendAndOptionalModel() {
        assertThat(ChatSpec.parse("claude-cli")).isEqualTo(new ChatSpec("claude-cli", null));
        assertThat(ChatSpec.parse("Claude-CLI:opus")).isEqualTo(new ChatSpec("claude-cli", "opus"));
        assertThat(ChatSpec.parse("deepseek:deepseek-chat")).isEqualTo(new ChatSpec("deepseek", "deepseek-chat"));
        assertThat(ChatSpec.parse("deepseek:")).isEqualTo(new ChatSpec("deepseek", null));
    }

    @Test
    void shouldKeepTheColonsOfTheModel() {
        var spec = ChatSpec.parse("ollama:qwen3:8b");

        assertThat(spec.backend()).isEqualTo("ollama");
        assertThat(spec.model()).isEqualTo("qwen3:8b");
        assertThat(spec).hasToString("ollama:qwen3:8b");
        assertThat(spec.isClaudeCli()).isFalse();
    }

    @Test
    void shouldTellWhichBackendsAreClaude() {
        assertThat(ChatSpec.parse("claude-cli").isClaude()).isTrue();
        assertThat(ChatSpec.parse("claude-cli:opus").isClaude()).isTrue();
        assertThat(ChatSpec.parse("anthropic:claude-sonnet-5-5").isClaude()).isTrue();
        assertThat(ChatSpec.parse("deepseek").isClaude()).isFalse();
        assertThat(ChatSpec.parse("ollama:qwen3:8b").isClaude()).isFalse();
    }

    @Test
    void shouldParseTheEffortOfClaudeCli() {
        assertThat(ChatSpec.parse("claude-cli:opus@low")).isEqualTo(new ChatSpec("claude-cli", "opus", "low"));
        assertThat(ChatSpec.parse("claude-cli@HIGH")).isEqualTo(new ChatSpec("claude-cli", null, "high"));
        assertThat(ChatSpec.parse("claude-cli:sonnet@medium")).hasToString("claude-cli:sonnet@medium");
        assertThat(ChatSpec.parse("claude-cli:opus").effort()).isNull();
    }

    @Test
    void shouldKeepTheAtSignOfOtherBackends() {
        var spec = ChatSpec.parse("ollama:model@latest");

        assertThat(spec.model()).isEqualTo("model@latest");
        assertThat(spec.effort()).isNull();
    }

    @Test
    void shouldRejectAnUnknownEffort() {
        assertThatThrownBy(() -> ChatSpec.parse("claude-cli:opus@bogus")).hasMessageContaining("Unknown effort");
        assertThatThrownBy(() -> ChatSpec.parse("claude-cli@bogus")).hasMessageContaining("Unknown effort");
    }

    @Test
    void shouldRejectBlankAndUnknownBackends() {
        assertThatThrownBy(() -> ChatSpec.parse(" ")).hasMessageContaining("must not be blank");
        assertThatThrownBy(() -> ChatSpec.parse("nope:model")).hasMessageContaining("Unknown LLM provider");
    }
}
