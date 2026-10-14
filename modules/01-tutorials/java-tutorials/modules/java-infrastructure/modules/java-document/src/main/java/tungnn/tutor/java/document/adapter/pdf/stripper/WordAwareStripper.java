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

public class WordAwareStripper extends PDFTextStripper {

  private final List<WordChunk> wordChunks = new ArrayList<>();

  public WordAwareStripper() {
    super();
    setSortByPosition(true);
  }

  @Override
  protected void startPage(PDPage page) throws IOException {
    super.startPage(page);
    wordChunks.clear();
  }

  @Override
  protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
    if (textPositions == null || textPositions.isEmpty() || text == null || text.isBlank()) {
      return;
    }

    TextPosition first = textPositions.getFirst();
    TextPosition last = textPositions.getLast();

    float x = first.getX();
    float y = first.getY();
    float width = last.getX() + last.getWidth() - first.getX();
    PDFont font = first.getFont();

    float fontSizePt = 0f;
    float height = 0f;
    for (TextPosition tp : textPositions) {
      fontSizePt = Math.max(fontSizePt, tp.getXScale());
      height = Math.max(height, tp.getHeight());
    }

    wordChunks.add(new WordChunk(text, getCurrentPageNo(), x, y, fontSizePt, font, width, height));
  }

  public List<WordChunk> getChunks() {
    return Collections.unmodifiableList(wordChunks);
  }

  public record WordChunk(
      String text,
      int pageNo,
      float x,
      float y,
      float fontSizePt,
      PDFont font,
      float width,
      float height) {

    public WordChunk {
      Objects.requireNonNull(text, "text cannot be null");
      Objects.requireNonNull(font, "font cannot be null");
      if (text.isBlank()) throw new IllegalArgumentException("text cannot be blank");
      if (pageNo < 1) throw new IllegalArgumentException("pageNo must be >= 1");
    }
  }
}
