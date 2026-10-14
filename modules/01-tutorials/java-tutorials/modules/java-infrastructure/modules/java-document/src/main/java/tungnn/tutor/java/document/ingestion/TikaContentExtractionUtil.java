package tungnn.tutor.java.document.ingestion;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

public final class TikaContentExtractionUtil {

  private static final Tika TIKA = new Tika();

  private TikaContentExtractionUtil() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static String extractContent(Path path) {
    Objects.requireNonNull(path, "Path must not be null");

    try {
      return TIKA.parseToString(path);
    } catch (IOException | TikaException e) {
      throw new RuntimeException("Failed to extract content from path: " + path, e);
    }
  }

  public static String extractContent(InputStream inputStream) {
    Objects.requireNonNull(inputStream, "InputStream must not be null");

    try {
      return TIKA.parseToString(inputStream);
    } catch (IOException | TikaException e) {
      throw new RuntimeException("Failed to extract content from InputStream", e);
    }
  }
}
