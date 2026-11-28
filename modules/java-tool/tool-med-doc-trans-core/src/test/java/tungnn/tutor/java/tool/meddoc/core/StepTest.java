package tungnn.tutor.java.tool.meddoc.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class StepTest {

    @Test
    void shouldParseNamesIgnoringCaseAndSpaces() {
        assertThat(Step.parse(" FIX-OCR, translate ,Export"))
                .containsExactlyInAnyOrder(Step.FIX_OCR, Step.TRANSLATE, Step.EXPORT);
    }

    @Test
    void shouldRunPolishTargetBetweenTranslateAndFormatTarget() {
        assertThat(Step.parse("polish-target")).containsExactly(Step.POLISH_TARGET);
        assertThat(Step.all()).doesNotContain(Step.LOAD, Step.SCAN_SOURCE, Step.SCAN_TARGET);
        assertThat(Step.POLISH_TARGET.ordinal()).isEqualTo(Step.TRANSLATE.ordinal() + 1);
        assertThat(Step.FORMAT_TARGET.ordinal()).isEqualTo(Step.POLISH_TARGET.ordinal() + 1);
    }

    @Test
    void shouldAlwaysRunTheGlossaryAndNotAllowSelectingIt() {
        assertThat(Step.all()).doesNotContain(Step.GLOSSARY);
        assertThatThrownBy(() -> Step.parse("glossary")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectAnUnknownStepAndListTheValidOnes() {
        assertThatThrownBy(() -> Step.parse("fix-ocr,dich"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("'dich'")
                .hasMessageContaining("structure-source");
    }

    @Test
    void shouldRejectAnEmptyList() {
        assertThatThrownBy(() -> Step.parse(" , ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Step.parse(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
