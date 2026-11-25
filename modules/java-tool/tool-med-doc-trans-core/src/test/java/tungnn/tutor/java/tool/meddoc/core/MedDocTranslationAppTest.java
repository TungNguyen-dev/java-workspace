package tungnn.tutor.java.tool.meddoc.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.service.Step;

class MedDocTranslationAppTest {

    @Test
    void shouldRunAllStepsByDefault() {
        var request = MedDocTranslationApp.parse(new String[] {"book.md"});

        assertThat(request.steps()).containsExactlyInAnyOrder(Step.values());
        assertThat(request.targetLanguage()).isEqualTo(LanguageCode.VI);
    }

    @Test
    void shouldReadTheSelectedSteps() {
        var request = MedDocTranslationApp.parse(new String[] {"book.md", "--steps", "ocr,translate", "--lang", "en"});

        assertThat(request.steps()).containsExactlyInAnyOrder(Step.OCR, Step.TRANSLATE);
        assertThat(request.targetLanguage()).isEqualTo(LanguageCode.EN);
    }

    @Test
    void shouldDropTheRefineStepsWithNoRefine() {
        var request = MedDocTranslationApp.parse(new String[] {"book.md", "--no-refine"});

        assertThat(request.steps()).containsExactlyInAnyOrder(Step.OCR, Step.TRANSLATE, Step.EXPORT);
    }

    @Test
    void shouldRejectBadArguments() {
        assertThatThrownBy(() -> MedDocTranslationApp.parse(new String[] {"book.md", "--steps", "dich"}))
                .hasMessageContaining("Unknown step");
        assertThatThrownBy(() -> MedDocTranslationApp.parse(new String[] {"book.md", "--out"}))
                .hasMessageContaining("needs a value");
        assertThatThrownBy(() -> MedDocTranslationApp.parse(new String[] {"book.md", "--lang", "xx"}))
                .hasMessageContaining("Unsupported language");
        assertThatThrownBy(() ->
                        MedDocTranslationApp.parse(new String[] {"book.md", "--steps", "refine-source", "--no-refine"}))
                .hasMessageContaining("no step");
    }
}
