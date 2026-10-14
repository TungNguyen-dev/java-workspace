package tungnn.tutor.java.document.ingestion;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

public final class TikaFormatDetectorUtil {

  private static final Tika TIKA = new Tika();
  private static final MimeTypes MIME_TYPES = MimeTypes.getDefaultMimeTypes();

  private TikaFormatDetectorUtil() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static MediaType detectMediaType(Path path) {
    Objects.requireNonNull(path, "Path must not be null");

    try {
      String mediaTypeStr = TIKA.detect(path);
      MediaType mediaType = MediaType.parse(mediaTypeStr);
      return mediaType != null ? mediaType : MediaType.OCTET_STREAM;
    } catch (IOException e) {
      throw new RuntimeException("Failed to detect MIME type for path: " + path, e);
    }
  }

  public static Optional<String> findExtensionByMediaType(MediaType mediaType) {
    if (mediaType == null) {
      return Optional.empty();
    }
    String mimeTypeStr = mediaType.toString();
    return findExtensionByMimeType(mimeTypeStr);
  }

  public static Optional<String> findExtensionByMimeType(String mimeTypeStr) {
    try {
      MimeType mimeType = MIME_TYPES.forName(mimeTypeStr);
      String ext = mimeType.getExtension();

      if (ext == null || ext.isEmpty()) {
        return Optional.empty();
      }

      return Optional.of(ext.startsWith(".") ? ext.substring(1) : ext);
    } catch (MimeTypeException e) {
      return Optional.empty();
    }
  }
}
