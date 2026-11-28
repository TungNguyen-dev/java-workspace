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

        assertThat(request.steps()).containsExactlyInAnyOrder(Step.all().toArray(new Step[0]));
        assertThat(request.targetLanguage()).isEqualTo(LanguageCode.VI);
    }

    @Test
    void shouldReadTheSelectedSteps() {
        var request = MedDocTranslationApp.parse(new String[] {"book.md", "--steps", "fix-ocr,translate", "--lang", "en"}).request();

        assertThat(request.steps()).containsExactlyInAnyOrder(Step.FIX_OCR, Step.TRANSLATE);
        assertThat(request.targetLanguage()).isEqualTo(LanguageCode.EN);
    }

    @Test
    void shouldDropTheRefineStepsWithNoRefine() {
        var request = MedDocTranslationApp.parse(new String[] {"book.md", "--no-refine"}).request();

        assertThat(request.steps()).containsExactlyInAnyOrder(
                Step.FIX_OCR, Step.GLOSSARY, Step.TRANSLATE, Step.CHECK_DOSES, Step.EXPORT);
    }

    @Test
    void shouldParseThePolishTargetStep() {
        var request = MedDocTranslationApp.parse(new String[] {"book.md", "--steps", "translate,polish-target"}).request();

        assertThat(request.steps()).containsExactlyInAnyOrder(Step.TRANSLATE, Step.POLISH_TARGET);
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
                        MedDocTranslationApp.parse(new String[] {"book.md", "--steps", "structure-source", "--no-refine"}))
                .hasMessageContaining("no step");
    }

    @Test
    void shouldCollectTheLlmOptions() {
        var args = MedDocTranslationApp.parse(new String[] {
            "book.md", "--llm", "default=deepseek", "--llm", "translate=claude-cli:opus", "--llm", "polish-target=ollama:qwen3:8b"
        });

        assertThat(args.llm())
                .containsEntry("default", "deepseek")
                .containsEntry("translate", "claude-cli:opus")
                .containsEntry("polish-target", "ollama:qwen3:8b");
    }

    @Test
    void shouldRejectBadLlmOptions() {
        assertThatThrownBy(() -> MedDocTranslationApp.parse(new String[] {"book.md", "--llm", "foo=deepseek"}))
                .hasMessageContaining("Unknown step");
        assertThatThrownBy(() -> MedDocTranslationApp.parse(new String[] {"book.md", "--llm", "translate"}))
                .hasMessageContaining("<step>=<spec>");
        assertThatThrownBy(() -> MedDocTranslationApp.parse(new String[] {"book.md", "--llm", "export=deepseek"}))
                .hasMessageContaining("does not use an LLM");
        assertThatThrownBy(() -> MedDocTranslationApp.parse(new String[] {"book.md", "--llm", "fix-ocr=nope"}))
                .hasMessageContaining("Unknown LLM provider");
    }
}
