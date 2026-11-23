package tungnn.tutor.java.ocr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public final class OcrTessUtil {

  private static final String TESSDATA_PROP = "tessdata.path";
  private static final String TESSDATA_ENV = "TESSDATA_PATH";

  private OcrTessUtil() {}

  static void main() {
    var tesseract = OcrTessUtil.createTesseract();
    var path =
        Path.of(
            "/Users/tungnn/SourceCode/tungnn/java/java-tutor/storage/app/ocr/01-todo/Screenshot 2026-04-27 at 14.46.45.png");
    var result = recognizeText(tesseract, "eng", path);
    System.out.println(result);
  }

  public static ITesseract createTesseract() {
    ITesseract tesseract = new Tesseract();

    var dataPath = getTessDataPath();
    tesseract.setDatapath(dataPath.toString());
    tesseract.setPageSegMode(3);
    tesseract.setOcrEngineMode(1);
    tesseract.setVariable("user_defined_dpi", "300");

    return tesseract;
  }

  private static Path getTessDataPath() {
    String path = System.getProperty(TESSDATA_PROP);
    if (path == null || path.isBlank()) {
      path = System.getenv(TESSDATA_ENV);
    }
    if (path == null || path.isBlank()) {
      throw new IllegalStateException(
          "tessdata path not configured. Set system property '"
              + TESSDATA_PROP
              + "' or environment variable '"
              + TESSDATA_ENV
              + "'.");
    }
    Path tessDataPath = Paths.get(path);
    if (!Files.isDirectory(tessDataPath)) {
      throw new IllegalStateException(
          "tessdata path does not exist or is not a directory: " + tessDataPath);
    }
    return tessDataPath;
  }

  public static String recognizeText(ITesseract tesseract, String language, Path imagePath) {
    tesseract.setLanguage(language);
    try {
      return tesseract.doOCR(imagePath.toFile());
    } catch (TesseractException e) {
      throw new IllegalStateException("OCR failed for image: " + imagePath, e);
    }
  }
}
