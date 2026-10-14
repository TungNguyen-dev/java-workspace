package tungnn.tutor.java.document.ingestion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("TikaContentExtractionUtil Unit Tests")
class TikaContentExtractionUtilTest {

  @Test
  @DisplayName("Utility class instantiation should throw UnsupportedOperationException")
  void testPrivateConstructor() throws NoSuchMethodException {
    Constructor<TikaContentExtractionUtil> constructor =
        TikaContentExtractionUtil.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    InvocationTargetException exception =
        assertThrows(InvocationTargetException.class, constructor::newInstance);

    assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
    assertEquals("Utility class cannot be instantiated", exception.getCause().getMessage());
  }

  @Nested
  @DisplayName("extractContent(Path) Tests")
  class ExtractContentByPathTests {

    @Test
    @DisplayName("Should extract text content from PDF file successfully")
    void extractContent_PdfPath_ReturnsExtractedText() {
      Path pdfPath = Paths.get("src/test/resources/sample-pdf-letter-size.pdf");

      String content = TikaContentExtractionUtil.extractContent(pdfPath);

      assertNotNull(content);
      assertTrue(content.contains("Sample PDF Content"));
      assertTrue(content.contains("Chadwick Hilarity"));
      assertTrue(content.contains("Market Analysis"));
    }

    @Test
    @DisplayName("Should extract text content from PPTX file successfully")
    void extractContent_PptxPath_ReturnsExtractedText() {
      Path pptxPath = Paths.get("src/test/resources/SamplePPTX-All.pptx");

      String content = TikaContentExtractionUtil.extractContent(pptxPath);

      assertNotNull(content);
      assertTrue(content.contains("Fruit Data"));
      assertTrue(content.contains("Apples"));
      assertTrue(content.contains("Fruit Chart"));
    }

    @Test
    @DisplayName("Should throw NullPointerException when path is null")
    void extractContent_NullPath_ThrowsNullPointerException() {
      NullPointerException exception =
          assertThrows(
              NullPointerException.class,
              () -> TikaContentExtractionUtil.extractContent((Path) null));

      assertEquals("Path must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw RuntimeException when file does not exist")
    void extractContent_NonExistentPath_ThrowsRuntimeException(@TempDir Path tempDir) {
      Path nonExistentPath = tempDir.resolve("non-existent.pdf");

      RuntimeException exception =
          assertThrows(
              RuntimeException.class,
              () -> TikaContentExtractionUtil.extractContent(nonExistentPath));

      assertTrue(exception.getMessage().contains("Failed to extract content from path"));
    }
  }

  @Nested
  @DisplayName("extractContent(InputStream) Tests")
  class ExtractContentByInputStreamTests {

    @Test
    @DisplayName("Should extract text content from PDF InputStream successfully")
    void extractContent_PdfInputStream_ReturnsExtractedText() throws IOException {
      try (InputStream is =
          getClass().getClassLoader().getResourceAsStream("sample-pdf-letter-size.pdf")) {
        assertNotNull(is, "Test resource file sample-pdf-letter-size.pdf must exist");

        String content = TikaContentExtractionUtil.extractContent(is);

        assertNotNull(content);
        assertTrue(content.contains("Sample PDF Content"));
        assertTrue(content.contains("Objectives"));
      }
    }

    @Test
    @DisplayName("Should extract text content from PPTX InputStream successfully")
    void extractContent_PptxInputStream_ReturnsExtractedText() throws IOException {
      try (InputStream is =
          getClass().getClassLoader().getResourceAsStream("SamplePPTX-All.pptx")) {
        assertNotNull(is, "Test resource file SamplePPTX-All.pptx must exist");

        String content = TikaContentExtractionUtil.extractContent(is);

        assertNotNull(content);
        assertTrue(content.contains("Fruit Data"));
        assertTrue(content.contains("Fruit Chart"));
      }
    }

    @Test
    @DisplayName("Should throw NullPointerException when InputStream is null")
    void extractContent_NullInputStream_ThrowsNullPointerException() {
      NullPointerException exception =
          assertThrows(
              NullPointerException.class,
              () -> TikaContentExtractionUtil.extractContent((InputStream) null));

      assertEquals("InputStream must not be null", exception.getMessage());
    }
  }
}
