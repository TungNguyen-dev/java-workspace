package tungnn.tutor.java.starter.application.service;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import tungnn.tutor.java.starter.application.model.OcrResult;

public interface OcrService {

  OcrResult recognize(Path imageOrPdf, OcrResult.OcrOptions options);

  OcrResult recognize(BufferedImage image, OcrResult.OcrOptions options);

  OcrResult recognize(InputStream stream, OcrResult.OcrOptions options);

  List<OcrResult> recognizePdf(Path pdf, OcrResult.OcrOptions options);

  default OcrResult recognize(Path imageOrPdf) {
    return recognize(imageOrPdf, OcrResult.OcrOptions.defaults());
  }
}
