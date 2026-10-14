package tungnn.tutor.java.document.ingestion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.apache.tika.mime.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("TikaUtils Unit Tests")
class TikaFormatDetectorUtilTest {

  @Test
  @DisplayName("Utility class instantiation should throw UnsupportedOperationException")
  void testPrivateConstructor() throws NoSuchMethodException {
    Constructor<TikaFormatDetectorUtil> constructor =
        TikaFormatDetectorUtil.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    InvocationTargetException exception =
        assertThrows(InvocationTargetException.class, constructor::newInstance);

    assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
    assertEquals("Utility class cannot be instantiated", exception.getCause().getMessage());
  }

  @Nested
  @DisplayName("detectMediaType Tests")
  class DetectMediaTypeTests {

    @Test
    @DisplayName("Should detect text/plain MediaType successfully")
    void detectMediaType_ValidTextFile_ReturnsTextPlain(@TempDir Path tempDir) throws IOException {
      Path sampleFile = tempDir.resolve("sample.txt");
      Files.writeString(sampleFile, "Hello World Tika Test");

      MediaType mediaType = TikaFormatDetectorUtil.detectMediaType(sampleFile);

      assertEquals(MediaType.TEXT_PLAIN, mediaType);
    }

    @Test
    @DisplayName("Should throw NullPointerException when path is null")
    void detectMediaType_NullPath_ThrowsNullPointerException() {
      NullPointerException exception =
          assertThrows(
              NullPointerException.class, () -> TikaFormatDetectorUtil.detectMediaType(null));

      assertEquals("Path must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw RuntimeException when path does not exist")
    void detectMediaType_NonExistentFile_ThrowsRuntimeException(@TempDir Path tempDir) {
      Path nonExistentPath = tempDir.resolve("non-existent-file.pdf");

      RuntimeException exception =
          assertThrows(
              RuntimeException.class,
              () -> TikaFormatDetectorUtil.detectMediaType(nonExistentPath));

      assertTrue(exception.getMessage().contains("Failed to detect MIME type for path"));
    }
  }

  @Nested
  @DisplayName("findExtensionByMediaType Tests")
  class FindExtensionByMediaTypeTests {

    @ParameterizedTest(name = "MediaType \"{0}\" should resolve to extension \"{1}\"")
    @CsvSource({
      "application/pdf, pdf",
      "image/jpeg, jpg",
      "image/png, png",
      "text/plain, txt",
      "application/json, json"
    })
    void findExtensionByMediaType_ValidMediaType_ReturnsExpectedExtension(
        String mimeType, String expectedExt) {
      MediaType mediaType = MediaType.parse(mimeType);

      Optional<String> extension = TikaFormatDetectorUtil.findExtensionByMediaType(mediaType);

      assertTrue(extension.isPresent());
      assertEquals(expectedExt, extension.get());
    }

    @ParameterizedTest(name = "MIME string \"{0}\" should resolve to extension \"{1}\"")
    @CsvSource({
      "application/pdf, pdf",
      "image/jpeg, jpg",
      "image/png, png",
      "text/plain, txt",
      "application/json, json"
    })
    void findExtensionByMediaType_ValidStringMimeType_ReturnsExpectedExtension(
        String mimeType, String expectedExt) {
      Optional<String> extension = TikaFormatDetectorUtil.findExtensionByMimeType(mimeType);

      assertTrue(extension.isPresent());
      assertEquals(expectedExt, extension.get());
    }

    @Test
    @DisplayName("Should return Optional.empty() when MediaType input is null")
    void findExtensionByMediaType_NullMediaType_ReturnsEmptyOptional() {
      Optional<String> result = TikaFormatDetectorUtil.findExtensionByMediaType(null);

      assertTrue(result.isEmpty());
    }

    @ParameterizedTest(name = "Input string \"{0}\" should return Optional.empty()")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void findExtensionByMediaType_NullOrBlankStringInput_ReturnsEmptyOptional(String invalidInput) {
      Optional<String> result = TikaFormatDetectorUtil.findExtensionByMimeType(invalidInput);

      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return Optional.empty() for unknown or invalid MIME type")
    void findExtensionByMediaType_UnknownMimeType_ReturnsEmptyOptional() {
      Optional<String> result =
          TikaFormatDetectorUtil.findExtensionByMimeType("invalid/mime-type-x");

      assertTrue(result.isEmpty());
    }
  }
}
