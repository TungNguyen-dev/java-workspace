package tungnn.tutor.java.ai.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class LlmSpecTest {

    @Test
    void shouldParseBackendAndOptionalModel() {
        assertThat(LlmSpec.parse("claude-cli")).isEqualTo(new LlmSpec(LlmProvider.CLAUDE_CLI, null));
        assertThat(LlmSpec.parse("Claude-CLI:opus")).isEqualTo(new LlmSpec(LlmProvider.CLAUDE_CLI, "opus"));
        assertThat(LlmSpec.parse("deepseek:deepseek-chat")).isEqualTo(new LlmSpec(LlmProvider.DEEPSEEK, "deepseek-chat"));
        assertThat(LlmSpec.parse("deepseek:")).isEqualTo(new LlmSpec(LlmProvider.DEEPSEEK, null));
    }

    @Test
    void shouldKeepTheColonsOfTheModel() {
        var spec = LlmSpec.parse("ollama:qwen3:8b");

        assertThat(spec.provider()).isEqualTo(LlmProvider.OLLAMA);
        assertThat(spec.model()).isEqualTo("qwen3:8b");
        assertThat(spec).hasToString("ollama:qwen3:8b");
        assertThat(spec.isClaudeCli()).isFalse();
    }

    @Test
    void shouldTellWhichBackendsAreClaude() {
        assertThat(LlmSpec.parse("claude-cli").isClaude()).isTrue();
        assertThat(LlmSpec.parse("claude-cli:opus").isClaude()).isTrue();
        assertThat(LlmSpec.parse("anthropic:claude-sonnet-5-5").isClaude()).isTrue();
        assertThat(LlmSpec.parse("deepseek").isClaude()).isFalse();
        assertThat(LlmSpec.parse("ollama:qwen3:8b").isClaude()).isFalse();
    }

    @Test
    void shouldParseTheEffortOfClaudeCli() {
        assertThat(LlmSpec.parse("claude-cli:opus@low")).isEqualTo(new LlmSpec(LlmProvider.CLAUDE_CLI, "opus", "low"));
        assertThat(LlmSpec.parse("claude-cli@HIGH")).isEqualTo(new LlmSpec(LlmProvider.CLAUDE_CLI, null, "high"));
        assertThat(LlmSpec.parse("claude-cli:sonnet@medium")).hasToString("claude-cli:sonnet@medium");
        assertThat(LlmSpec.parse("claude-cli:opus").effort()).isNull();
    }

    @Test
    void shouldKeepTheAtSignOfOtherBackends() {
        var spec = LlmSpec.parse("ollama:model@latest");

        assertThat(spec.model()).isEqualTo("model@latest");
        assertThat(spec.effort()).isNull();
    }

    @Test
    void shouldRejectAnUnknownEffort() {
        assertThatThrownBy(() -> LlmSpec.parse("claude-cli:opus@bogus")).hasMessageContaining("Unknown effort");
        assertThatThrownBy(() -> LlmSpec.parse("claude-cli@bogus")).hasMessageContaining("Unknown effort");
    }

    @Test
    void shouldRejectBlankAndUnknownBackends() {
        assertThatThrownBy(() -> LlmSpec.parse(" ")).hasMessageContaining("must not be blank");
        assertThatThrownBy(() -> LlmSpec.parse("nope:model")).hasMessageContaining("Unknown LLM provider");
    }
}
