package tungnn.tutor.java.document.ingestion;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

public final class TikaMetaExtractionUtil {

  private TikaMetaExtractionUtil() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static Metadata extractMetadata(Path path) {
    Objects.requireNonNull(path, "Path must not be null");

    Parser parser = new AutoDetectParser();
    BodyContentHandler handler = new BodyContentHandler(-1);
    Metadata metadata = new Metadata();
    ParseContext context = new ParseContext();

    try (TikaInputStream tis = TikaInputStream.get(path, metadata)) {
      parser.parse(tis, handler, metadata, context);
      return metadata;
    } catch (Exception e) {
      throw new RuntimeException("Failed to extract metadata from path: " + path, e);
    }
  }

  public static Map<String, String> extractMetadataAsMap(Path path) {
    Metadata metadata = extractMetadata(path);
    return metadataToMap(metadata);
  }

  private static Map<String, String> metadataToMap(Metadata metadata) {
    return Arrays.stream(metadata.names())
        .collect(
            Collectors.toMap(name -> name, metadata::get, (existing, replacement) -> existing));
  }
}
