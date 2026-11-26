package tungnn.tutor.java.tool.doctrans.core.text;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class TextTranslationBatchOverrideTest {

    private static final TextTranslationRequest.BatchPolicy DEFAULTS = new TextTranslationRequest.BatchPolicy(20, 3000);

    @Test
    void noneShouldKeepDefaults() {
        assertThat(TextTranslationRequest.BatchOverride.NONE.applyTo(DEFAULTS)).isEqualTo(DEFAULTS);
    }

    @Test
    void partialOverrideShouldReplaceOnlyGivenField() {
        var policy = new TextTranslationRequest.BatchOverride(5, null).applyTo(DEFAULTS);

        assertThat(policy.maxUnits()).isEqualTo(5);
        assertThat(policy.maxChars()).isEqualTo(3000);
    }

    @Test
    void invalidOverrideShouldBeRejected() {
        assertThatThrownBy(() -> new TextTranslationRequest.BatchOverride(0, null).applyTo(DEFAULTS))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
