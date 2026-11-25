package tungnn.tutor.java.tool.doctrans.core.infra.translator.text;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;

class DummyTextTranslatorTest {

    private final DummyTextTranslator translator = new DummyTextTranslator();

    @Test
    void shouldTranslateTextSuccessfully() {
        // Given
        var textUnit = new TextTranslationRequest.TextUnit("unit-1", "Hello World");
        var request = new TextTranslationRequest.Single(textUnit, LanguageCode.EN, null, null);

        // When
        var result = translator.translate(request);

        // Then
        assertThat(result).isInstanceOf(TextTranslationResult.Success.class);
        var successResult = (TextTranslationResult.Success) result;

        assertThat(successResult.translatedText().id()).isEqualTo("unit-1");
        assertThat(successResult.translatedText().sourceContent()).isEqualTo("Hello World");
        assertThat(successResult.translatedText().translatedContent()).isEqualTo("[DUMMY - EN] Hello World");
        assertThat(successResult.translatedText().targetLanguage()).isEqualTo(LanguageCode.EN);
    }
}
