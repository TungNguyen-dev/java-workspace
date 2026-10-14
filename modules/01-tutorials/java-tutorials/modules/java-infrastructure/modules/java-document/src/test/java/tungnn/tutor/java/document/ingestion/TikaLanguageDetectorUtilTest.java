package tungnn.tutor.java.document.ingestion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("TikaLanguageDetectorUtil Unit Tests")
class TikaLanguageDetectorUtilTest {

  @Test
  @DisplayName("Constructor should be private")
  void testPrivateConstructor() throws NoSuchMethodException {
    Constructor<TikaLanguageDetectorUtil> constructor =
        TikaLanguageDetectorUtil.class.getDeclaredConstructor();

    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Nested
  @DisplayName("detectLanguage Tests")
  class DetectLanguageTests {

    @ParameterizedTest(name = "Text \"{0}\" should be detected as \"{1}\"")
    @CsvSource({
      "'Hello world, this is a sample English sentence for testing language detection.', en",
      "'Bonjour tout le monde, ceci est un texte bài văn tiếng Pháp.', fr",
      "'Hola a todos, este es un texto de prueba en español.', es",
      "'Hallo zusammen, das ist ein deutscher Text für die Erkennung.', de"
    })
    void detectLanguage_ValidText_ReturnsExpectedLanguageCode(
        String inputText, String expectedLang) {

      String detectedLang = TikaLanguageDetectorUtil.detectLanguage(inputText);

      assertEquals(expectedLang, detectedLang);
    }

    @ParameterizedTest(name = "Input \"{0}\" should be handled gracefully without exception")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void detectLanguage_NullOrBlankInput_ReturnsResultWithoutException(String invalidInput) {
      String detectedLang = TikaLanguageDetectorUtil.detectLanguage(invalidInput);

      assertNotNull(detectedLang);
    }
  }
}
