package tungnn.tutor.java.tool.meddoc.llm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.config.LlmSpec;
import tungnn.tutor.java.tool.meddoc.core.Step;

class ChatSpecUtilsTest {

    @Test
    void shouldFallBackToDeepseekWhenNothingIsConfigured() {
        var specs = ChatSpecUtils.resolve(Map.of(), key -> null);

        assertThat(specs.keySet()).containsExactlyInAnyOrder(
                Step.FIX_OCR, Step.TRANSLATE, Step.POLISH_TARGET, Step.FORMAT_TARGET);
        assertThat(specs.values()).allMatch(spec -> spec.equals(new LlmSpec(LlmProvider.DEEPSEEK, null)));
    }

    @Test
    void shouldKeepTheLegacyProviderVariableAsTheDefault() {
        var specs = ChatSpecUtils.resolve(Map.of(), Map.of("DOCTRANS_TRANSLATOR_PROVIDER", "anthropic")::get);

        assertThat(specs.get(Step.FIX_OCR)).isEqualTo(new LlmSpec(LlmProvider.ANTHROPIC, null));
    }

    @Test
    void shouldApplyOptionsBeforeStepVariablesBeforeTheDefault() {
        var env = Map.of(
                "DOCTRANS_LLM_DEFAULT", "ollama:qwen3:8b",
                "DOCTRANS_LLM_TRANSLATE", "claude-cli:sonnet",
                "DOCTRANS_LLM_FORMAT_TARGET", "openai");

        var specs = ChatSpecUtils.resolve(Map.of("translate", "claude-cli:opus", "fix-ocr", "deepseek"), env::get);

        assertThat(specs.get(Step.TRANSLATE)).isEqualTo(new LlmSpec(LlmProvider.CLAUDE_CLI, "opus"));
        assertThat(specs.get(Step.FIX_OCR)).isEqualTo(new LlmSpec(LlmProvider.DEEPSEEK, null));
        assertThat(specs.get(Step.FORMAT_TARGET)).isEqualTo(new LlmSpec(LlmProvider.OPENAI, null));
        assertThat(specs.get(Step.POLISH_TARGET)).isEqualTo(new LlmSpec(LlmProvider.OLLAMA, "qwen3:8b"));
    }

    @Test
    void shouldRejectStepsWithoutAnLlm() {
        assertThatThrownBy(() -> ChatSpecUtils.resolve(Map.of("structure-source", "deepseek"), key -> null))
                .hasMessageContaining("does not use an LLM");
        assertThatThrownBy(() -> ChatSpecUtils.resolve(Map.of("dich", "deepseek"), key -> null))
                .hasMessageContaining("Unknown step");
    }
}
