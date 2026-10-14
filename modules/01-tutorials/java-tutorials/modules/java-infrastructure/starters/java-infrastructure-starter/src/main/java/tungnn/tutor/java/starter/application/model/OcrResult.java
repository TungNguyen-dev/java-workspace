package tungnn.tutor.java.starter.application.model;

import java.util.List;

public record OcrResult(String text, float confidence, List<OcrBlock> blocks) {

  public boolean isEmpty() {
    return text == null || text.isBlank();
  }

  public record OcrBlock(String text, float confidence, int x, int y, int width, int height) {}

  public record OcrOptions(String languages, int dpi, int pageSegMode, boolean withBlocks) {
    public static OcrOptions defaults() {
      return new OcrOptions("eng", 300, 3, false);
    }
  }
}
