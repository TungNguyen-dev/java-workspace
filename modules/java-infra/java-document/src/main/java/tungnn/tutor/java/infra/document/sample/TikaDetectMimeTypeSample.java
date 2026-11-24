package tungnn.tutor.java.infra.document.sample;

import java.nio.file.Path;
import tungnn.tutor.java.infra.document.ingestion.TikaFormatDetectorUtils;

public class TikaDetectMimeTypeSample {
    static void main() {
        var storageDir = Path.of("storage");
        var path = storageDir.resolve("tika.md");
        var mediaType = TikaFormatDetectorUtils.detectMediaType(path);
        var mimeType = TikaFormatDetectorUtils.findExtensionByMediaType(mediaType);
        System.out.println(mimeType.orElse(null));
    }
}
