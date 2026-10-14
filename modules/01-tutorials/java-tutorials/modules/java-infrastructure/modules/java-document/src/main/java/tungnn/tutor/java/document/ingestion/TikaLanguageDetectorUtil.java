package tungnn.tutor.java.document.ingestion;

import java.io.IOException;
import org.apache.tika.language.detect.LanguageDetector;

public final class TikaLanguageDetectorUtil {

  private static final LanguageDetector LANGUAGE_DETECTOR;
  private static final String UNKNOWN_LANGUAGE = "unknown";

  static {
    try {
      LANGUAGE_DETECTOR = LanguageDetector.getDefaultLanguageDetector().loadModels();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private TikaLanguageDetectorUtil() {}

  public static String detectLanguage(String text) {
    if (text == null || text.isBlank()) {
      return UNKNOWN_LANGUAGE;
    }
    return LANGUAGE_DETECTOR.detect(text).getLanguage();
  }
}
