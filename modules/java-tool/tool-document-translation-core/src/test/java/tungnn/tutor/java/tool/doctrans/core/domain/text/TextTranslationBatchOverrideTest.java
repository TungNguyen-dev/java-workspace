package tungnn.tutor.java.tool.doctrans.core.domain.text;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class TextTranslationBatchOverrideTest {

    private static final TextTranslationBatchPolicy DEFAULTS = new TextTranslationBatchPolicy(20, 3000);

    @Test
    void noneShouldKeepDefaults() {
        assertThat(TextTranslationBatchOverride.NONE.applyTo(DEFAULTS)).isEqualTo(DEFAULTS);
    }

    @Test
    void partialOverrideShouldReplaceOnlyGivenField() {
        var policy = new TextTranslationBatchOverride(5, null).applyTo(DEFAULTS);

        assertThat(policy.maxUnits()).isEqualTo(5);
        assertThat(policy.maxChars()).isEqualTo(3000);
    }

    @Test
    void invalidOverrideShouldBeRejected() {
        assertThatThrownBy(() -> new TextTranslationBatchOverride(0, null).applyTo(DEFAULTS))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
