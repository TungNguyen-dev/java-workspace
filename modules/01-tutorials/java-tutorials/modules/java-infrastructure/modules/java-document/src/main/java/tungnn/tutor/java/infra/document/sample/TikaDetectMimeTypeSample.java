package tungnn.tutor.java.infra.document.sample;

import java.nio.file.Path;
import tungnn.tutor.java.infra.document.ingestion.TikaFormatDetectorUtil;

public class TikaDetectMimeTypeSample {

  static void main() {
    var storageDir = Path.of("storage");
    var path = storageDir.resolve("tika.md");
    var mediaType = TikaFormatDetectorUtil.detectMediaType(path);
    var mimeType = TikaFormatDetectorUtil.findExtensionByMediaType(mediaType);
    System.out.println(mimeType.orElse(null));
  }
}
