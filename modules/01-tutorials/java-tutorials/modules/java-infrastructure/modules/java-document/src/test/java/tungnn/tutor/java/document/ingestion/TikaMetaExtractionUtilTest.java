package tungnn.tutor.java.document.ingestion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.tika.metadata.Metadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("TikaMetaExtractionUtil Unit Tests")
public class TikaMetaExtractionUtilTest {

  @Test
  @DisplayName("Utility class instantiation should throw UnsupportedOperationException")
  void testPrivateConstructor() throws NoSuchMethodException {
    Constructor<TikaMetaExtractionUtil> constructor =
        TikaMetaExtractionUtil.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    InvocationTargetException exception =
        assertThrows(InvocationTargetException.class, constructor::newInstance);

    assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
    assertEquals("Utility class cannot be instantiated", exception.getCause().getMessage());
  }

  @Nested
  @DisplayName("extractMetadata(Path) Tests")
  class ExtractMetadataTests {

    @Test
    @DisplayName("Should extract metadata from PDF file successfully")
    void extractMetadata_PdfPath_ReturnsMetadata() {
      Path pdfPath = Paths.get("src/test/resources/sample-pdf-letter-size.pdf");

      Metadata metadata = TikaMetaExtractionUtil.extractMetadata(pdfPath);

      assertNotNull(metadata);
      assertTrue(metadata.get("Content-Type").contains("application/pdf"));
    }

    @Test
    @DisplayName("Should extract metadata from PPTX file successfully")
    void extractMetadata_PptxPath_ReturnsMetadata() {
      Path pptxPath = Paths.get("src/test/resources/SamplePPTX-All.pptx");

      Metadata metadata = TikaMetaExtractionUtil.extractMetadata(pptxPath);

      assertNotNull(metadata);
      assertNotNull(metadata.get("Content-Type"));
    }

    @Test
    @DisplayName("Should throw NullPointerException when path is null")
    void extractMetadata_NullPath_ThrowsNullPointerException() {
      NullPointerException exception =
          assertThrows(
              NullPointerException.class, () -> TikaMetaExtractionUtil.extractMetadata(null));

      assertEquals("Path must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw RuntimeException when file does not exist")
    void extractMetadata_NonExistentPath_ThrowsRuntimeException(@TempDir Path tempDir) {
      Path nonExistentPath = tempDir.resolve("non-existent.pdf");

      RuntimeException exception =
          assertThrows(
              RuntimeException.class,
              () -> TikaMetaExtractionUtil.extractMetadata(nonExistentPath));

      assertTrue(exception.getMessage().contains("Failed to extract metadata from path"));
    }
  }

  @Nested
  @DisplayName("extractMetadataAsMap(Path) Tests")
  class ExtractMetadataAsMapTests {

    @Test
    @DisplayName("Should extract metadata as Map from PDF file successfully")
    void extractMetadataAsMap_PdfPath_ReturnsMetadataMap() {
      Path pdfPath = Paths.get("src/test/resources/sample-pdf-letter-size.pdf");

      Map<String, String> metadataMap = TikaMetaExtractionUtil.extractMetadataAsMap(pdfPath);

      assertNotNull(metadataMap);
      assertFalse(metadataMap.isEmpty());
      assertTrue(metadataMap.containsKey("Content-Type"));
      assertTrue(metadataMap.get("Content-Type").contains("application/pdf"));
    }

    @Test
    @DisplayName("Should throw NullPointerException when path is null")
    void extractMetadataAsMap_NullPath_ThrowsNullPointerException() {
      NullPointerException exception =
          assertThrows(
              NullPointerException.class, () -> TikaMetaExtractionUtil.extractMetadataAsMap(null));

      assertEquals("Path must not be null", exception.getMessage());
    }
  }
}
