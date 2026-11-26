package tungnn.tutor.java.tool.meddoc.core.llm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.meddoc.core.service.Step;

class ChatSpecUtilsTest {

    @Test
    void shouldFallBackToDeepseekWhenNothingIsConfigured() {
        var specs = ChatSpecUtils.resolve(Map.of(), key -> null);

        assertThat(specs.keySet()).containsExactlyInAnyOrder(Step.OCR, Step.TRANSLATE, Step.EDIT, Step.REFINE_TARGET);
        assertThat(specs.values()).allMatch(spec -> spec.equals(new ChatSpec("deepseek", null)));
    }

    @Test
    void shouldKeepTheLegacyProviderVariableAsTheDefault() {
        var specs = ChatSpecUtils.resolve(Map.of(), Map.of("DOCTRANS_TRANSLATOR_PROVIDER", "anthropic")::get);

        assertThat(specs.get(Step.OCR)).isEqualTo(new ChatSpec("anthropic", null));
    }

    @Test
    void shouldApplyOptionsBeforeStepVariablesBeforeTheDefault() {
        var env = Map.of(
                "DOCTRANS_LLM_DEFAULT", "ollama:qwen3:8b",
                "DOCTRANS_LLM_TRANSLATE", "claude-cli:sonnet",
                "DOCTRANS_LLM_REFINE_TARGET", "openai");

        var specs = ChatSpecUtils.resolve(Map.of("translate", "claude-cli:opus", "ocr", "deepseek"), env::get);

        assertThat(specs.get(Step.TRANSLATE)).isEqualTo(new ChatSpec("claude-cli", "opus"));
        assertThat(specs.get(Step.OCR)).isEqualTo(new ChatSpec("deepseek", null));
        assertThat(specs.get(Step.REFINE_TARGET)).isEqualTo(new ChatSpec("openai", null));
        assertThat(specs.get(Step.EDIT)).isEqualTo(new ChatSpec("ollama", "qwen3:8b"));
    }

    @Test
    void shouldRejectStepsWithoutAnLlm() {
        assertThatThrownBy(() -> ChatSpecUtils.resolve(Map.of("refine-source", "deepseek"), key -> null))
                .hasMessageContaining("does not use an LLM");
        assertThatThrownBy(() -> ChatSpecUtils.resolve(Map.of("dich", "deepseek"), key -> null))
                .hasMessageContaining("Unknown step");
    }
}
