package tungnn.tutor.java.tool.meddoc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.Step;

class MedDocTranslationAppTest {

    @Test
    void shouldRunAllStepsByDefault() {
        var request = MedDocTranslationApp.parse(new String[] {"book.md"}).request();

        assertThat(request.steps()).containsExactlyInAnyOrder(Step.values());
        assertThat(request.targetLanguage()).isEqualTo(LanguageCode.VI);
    }

    @Test
    void shouldReadTheSelectedSteps() {
        var request = MedDocTranslationApp.parse(new String[] {"book.md", "--steps", "ocr,translate", "--lang", "en"}).request();

        assertThat(request.steps()).containsExactlyInAnyOrder(Step.OCR, Step.TRANSLATE);
        assertThat(request.targetLanguage()).isEqualTo(LanguageCode.EN);
    }

    @Test
    void shouldDropTheRefineStepsWithNoRefine() {
        var request = MedDocTranslationApp.parse(new String[] {"book.md", "--no-refine"}).request();

        assertThat(request.steps()).containsExactlyInAnyOrder(Step.OCR, Step.TRANSLATE, Step.EXPORT);
    }

    @Test
    void shouldParseTheEditStep() {
        var request = MedDocTranslationApp.parse(new String[] {"book.md", "--steps", "translate,edit"}).request();

        assertThat(request.steps()).containsExactlyInAnyOrder(Step.TRANSLATE, Step.EDIT);
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

    @Test
    void shouldCollectTheLlmOptions() {
        var args = MedDocTranslationApp.parse(new String[] {
            "book.md", "--llm", "default=deepseek", "--llm", "translate=claude-cli:opus", "--llm", "edit=ollama:qwen3:8b"
        });

        assertThat(args.llm())
                .containsEntry("default", "deepseek")
                .containsEntry("translate", "claude-cli:opus")
                .containsEntry("edit", "ollama:qwen3:8b");
    }

    @Test
    void shouldRejectBadLlmOptions() {
        assertThatThrownBy(() -> MedDocTranslationApp.parse(new String[] {"book.md", "--llm", "foo=deepseek"}))
                .hasMessageContaining("Unknown step");
        assertThatThrownBy(() -> MedDocTranslationApp.parse(new String[] {"book.md", "--llm", "translate"}))
                .hasMessageContaining("<step>=<spec>");
        assertThatThrownBy(() -> MedDocTranslationApp.parse(new String[] {"book.md", "--llm", "export=deepseek"}))
                .hasMessageContaining("does not use an LLM");
        assertThatThrownBy(() -> MedDocTranslationApp.parse(new String[] {"book.md", "--llm", "ocr=nope"}))
                .hasMessageContaining("Unknown LLM provider");
    }
}
