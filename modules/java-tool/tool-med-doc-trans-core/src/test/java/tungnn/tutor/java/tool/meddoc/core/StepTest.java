package tungnn.tutor.java.tool.meddoc.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class StepTest {

    @Test
    void shouldParseNamesIgnoringCaseAndSpaces() {
        assertThat(Step.parse(" OCR, translate ,Export"))
                .containsExactlyInAnyOrder(Step.OCR, Step.TRANSLATE, Step.EXPORT);
    }

    @Test
    void shouldRunEditBetweenTranslateAndRefineTarget() {
        assertThat(Step.parse("edit")).containsExactly(Step.EDIT);
        assertThat(Step.all()).containsExactly(Step.values());
        assertThat(Step.EDIT.ordinal()).isEqualTo(Step.TRANSLATE.ordinal() + 1);
        assertThat(Step.REFINE_TARGET.ordinal()).isEqualTo(Step.EDIT.ordinal() + 1);
    }

    @Test
    void shouldRejectAnUnknownStepAndListTheValidOnes() {
        assertThatThrownBy(() -> Step.parse("ocr,dich"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("'dich'")
                .hasMessageContaining("refine-source");
    }

    @Test
    void shouldRejectAnEmptyList() {
        assertThatThrownBy(() -> Step.parse(" , ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Step.parse(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
