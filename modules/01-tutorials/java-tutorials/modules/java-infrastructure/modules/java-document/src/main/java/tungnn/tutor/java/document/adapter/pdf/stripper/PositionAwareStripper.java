package tungnn.tutor.java.document.adapter.pdf.stripper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class PositionAwareStripper extends PDFTextStripper {

  private final List<TextChunk> chunks = new ArrayList<>();

  public PositionAwareStripper() {
    super();
  }

  @Override
  protected void startPage(PDPage page) throws IOException {
    super.startPage(page);
    chunks.clear();
  }

  @Override
  protected void writeString(String text, List<TextPosition> textPositions) {
    if (textPositions == null || textPositions.isEmpty()) {
      return;
    }

    for (TextPosition position : textPositions) {
      String unicode = position.getUnicode();
      if (unicode == null || unicode.isEmpty()) {
        // Glyph has no Unicode mapping; skip rather than throw NPE.
        continue;
      }

      chunks.add(
          new TextChunk(
              unicode,
              position.getX(), // screen-space x, origin upper-left
              position.getY(), // screen-space y, origin upper-left
              position.getFontSizeInPt(), // rendered pt size (Tf × Tm scaling)
              position.getFont(),
              position.getWidth(),
              position.getHeight() // heuristic max-height, see TextChunk Javadoc
              ));
    }
  }

  public List<TextChunk> getChunks() {
    return Collections.unmodifiableList(chunks);
  }

  public record TextChunk(
      String text, float x, float y, float fontSizePt, PDFont font, float width, float height) {

    public TextChunk {
      Objects.requireNonNull(text, "text cannot be null");
      Objects.requireNonNull(font, "font cannot be null");
      if (text.isEmpty()) throw new IllegalArgumentException("text cannot be empty");
    }
  }
}
