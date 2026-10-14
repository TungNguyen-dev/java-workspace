package tungnn.tutor.java.document.adapter.pdf;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import org.apache.fontbox.ttf.CmapLookup;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.util.Matrix;
import tungnn.tutor.java.document.adapter.pdf.stripper.PositionAwareStripper;
import tungnn.tutor.java.document.adapter.pdf.stripper.WordAwareStripper;

public class PdfDocumentUtil {

  private static final float MIN_WORD_GAP_PT = 1.5f;
  private static final float LINE_OVERLAP_RATIO = 0.5f;

  public static PDDocument readDocument(InputStream inputStream) throws IOException {
    return Loader.loadPDF(new RandomAccessReadBuffer(inputStream));
  }

  public static List<PositionAwareStripper.TextChunk> extractTextChunk(
      PDDocument document, PositionAwareStripper stripper) throws IOException {

    List<PositionAwareStripper.TextChunk> tokens = new ArrayList<>();

    stripper.setSortByPosition(true);
    int totalPages = document.getNumberOfPages();
    for (int i = 1; i <= totalPages; i++) {
      stripper.setStartPage(i);
      stripper.setEndPage(i);
      stripper.getText(document);
      tokens.addAll(stripper.getChunks());
    }

    return tokens;
  }

  public static List<WordAwareStripper.WordChunk> extractWordChunk(
      PDDocument document, WordAwareStripper stripper) throws IOException {

    List<WordAwareStripper.WordChunk> tokens = new ArrayList<>();

    stripper.setSortByPosition(true);
    int totalPages = document.getNumberOfPages();
    for (int i = 1; i <= totalPages; i++) {
      stripper.setStartPage(i);
      stripper.setEndPage(i);
      stripper.getText(document);
      tokens.addAll(stripper.getChunks());
    }

    return tokens;
  }

  public static void writeDocument(Path path, List<WordAwareStripper.WordChunk> chunks)
      throws IOException {

    if (chunks == null || chunks.isEmpty()) {
      return;
    }

    Map<Integer, List<WordAwareStripper.WordChunk>> byPage = new TreeMap<>();
    for (WordAwareStripper.WordChunk chunk : chunks) {
      byPage.computeIfAbsent(chunk.pageNo(), k -> new ArrayList<>()).add(chunk);
    }

    try (PDDocument doc = new PDDocument()) {

      PDType0Font font;
      try (InputStream fontStream =
          PDFont.class.getResourceAsStream(
              "/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf")) {
        font = PDType0Font.load(doc, fontStream, false);
      }

      final float margin = 20f;

      for (List<WordAwareStripper.WordChunk> pageInput : byPage.values()) {

        // --- Lọc MỘT LẦN: bỏ word không vẽ được, giữ kèm safeText -------------
        List<RenderChunk> sanitized = new ArrayList<>(pageInput.size());
        for (WordAwareStripper.WordChunk chunk : pageInput) {
          String safeText = filterSupportedChars(font, chunk.text());
          if (safeText != null) {
            sanitized.add(new RenderChunk(chunk, safeText));
          }
        }
        if (sanitized.isEmpty()) {
          continue;
        }

        List<RenderChunk> pageChunks = resolveOverlaps(font, sanitized);

        // Page-box lấy theo width đã chuẩn hóa -> khớp glyph thực vẽ
        float maxX = 0f;
        float maxY = 0f;
        for (RenderChunk rc : pageChunks) {
          WordAwareStripper.WordChunk c = rc.chunk();
          maxX = Math.max(maxX, c.x() + c.width());
          maxY = Math.max(maxY, c.y() + c.height());
        }
        final float pageWidth = maxX + margin;
        final float pageHeight = maxY + margin;

        PDPage page = new PDPage(new PDRectangle(pageWidth, pageHeight));
        doc.addPage(page);

        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
          for (RenderChunk rc : pageChunks) {
            WordAwareStripper.WordChunk c = rc.chunk();
            cs.beginText();
            cs.setFont(font, c.fontSizePt());
            cs.setTextMatrix(new Matrix(1, 0, 0, 1, c.x(), pageHeight - c.y()));
            cs.showText(rc.safeText()); // dùng lại chính text đã lọc
            cs.endText();
          }
        }
      }

      doc.save(path.toFile());
    }
  }

  // TODO:
  public static void writeDocument(
      Path path, List<WordAwareStripper.WordChunk> chunks, Map<Integer, float[]> pageSizes) {}

  public static void mergeSideBySide(PDDocument docA, PDDocument docB, Path output)
      throws IOException {

    int pageCount = Math.min(docA.getNumberOfPages(), docB.getNumberOfPages());

    try (PDDocument result = new PDDocument()) {
      LayerUtility layerUtil = new LayerUtility(result);

      for (int i = 0; i < pageCount; i++) {
        PDPage pageA = docA.getPage(i);
        PDPage pageB = docB.getPage(i);

        float widthA = pageA.getMediaBox().getWidth();
        float heightA = pageA.getMediaBox().getHeight();
        float widthB = pageB.getMediaBox().getWidth();
        float heightB = pageB.getMediaBox().getHeight();

        float combinedWidth = widthA + widthB;
        float combinedHeight = Math.max(heightA, heightB);

        PDPage newPage = new PDPage(new PDRectangle(combinedWidth, combinedHeight));
        result.addPage(newPage);

        PDFormXObject formA = layerUtil.importPageAsForm(docA, i);
        PDFormXObject formB = layerUtil.importPageAsForm(docB, i);

        try (PDPageContentStream cs = new PDPageContentStream(result, newPage)) {
          // docA bên trái (x = 0)
          cs.saveGraphicsState();
          cs.transform(Matrix.getTranslateInstance(0, 0));
          cs.drawForm(formA);
          cs.restoreGraphicsState();

          // docB bên phải (x = widthA)
          cs.saveGraphicsState();
          cs.transform(Matrix.getTranslateInstance(widthA, 0));
          cs.drawForm(formB);
          cs.restoreGraphicsState();
        }
      }

      result.save(output.toFile());
    }
  }

  public static BufferedImage convertToSingleImage(PDDocument document, int dpi)
      throws IOException {
    var renderer = new PDFRenderer(document);
    var pages = new ArrayList<BufferedImage>();

    // 1) Render từng trang
    for (int i = 0; i < document.getNumberOfPages(); i++) {
      pages.add(renderer.renderImageWithDPI(i, dpi, ImageType.RGB));
    }
    if (pages.isEmpty()) {
      throw new IllegalStateException("PDF has no pages: nothing to render");
    }

    // 2) Tính kích thước canvas: rộng = trang rộng nhất, cao = tổng chiều cao
    var totalWidth = pages.stream().mapToInt(BufferedImage::getWidth).max().orElse(0);
    var totalHeight = pages.stream().mapToInt(BufferedImage::getHeight).sum();

    // 3) Vẽ lần lượt các trang xuống canvas
    var combined = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
    var g = combined.createGraphics();
    try {
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, totalWidth, totalHeight); // nền trắng cho trang hẹp hơn
      var y = 0;
      for (var page : pages) {
        g.drawImage(page, 0, y, null);
        y += page.getHeight();
        page.flush(); // giải phóng sớm
      }
    } finally {
      g.dispose();
    }

    return combined;
  }

  private static String filterSupportedChars(PDType0Font font, String text) {
    CmapLookup cmap = font.getCmapLookup();
    if (cmap == null) return text;

    StringBuilder sb = new StringBuilder(text.length());
    boolean anySupported = false;
    int offset = 0;
    while (offset < text.length()) {
      int cp = text.codePointAt(offset);
      if (cmap.getGlyphId(cp) > 0) {
        sb.appendCodePoint(cp);
        anySupported = true;
      } else {
        sb.append('?');
      }
      offset += Character.charCount(cp);
    }
    return anySupported ? sb.toString() : null;
  }

  private static List<RenderChunk> resolveOverlaps(
      PDType0Font renderFont, List<RenderChunk> chunks) {

    List<List<RenderChunk>> lines = groupIntoLines(chunks);

    List<RenderChunk> result = new ArrayList<>(chunks.size());
    for (List<RenderChunk> line : lines) {
      line.sort(Comparator.comparingDouble(rc -> rc.chunk().x()));

      float cursorX = Float.NEGATIVE_INFINITY;
      for (RenderChunk rc : line) {
        float width =
            renderedWidth(renderFont, rc.safeText(), rc.chunk().fontSizePt(), rc.chunk().width());
        float x = Math.max(rc.chunk().x(), cursorX);
        cursorX = x + width + MIN_WORD_GAP_PT;
        result.add(withGeometry(rc, x, width)); // chuẩn hóa width trên MỌI chunk
      }
    }
    return result;
  }

  /** Gom dòng top-to-bottom theo tỉ lệ chồng lấn dọc (band cập nhật tăng dần). */
  private static List<List<RenderChunk>> groupIntoLines(List<RenderChunk> chunks) {
    List<RenderChunk> sorted = new ArrayList<>(chunks);
    sorted.sort(
        Comparator.comparingDouble((RenderChunk rc) -> rc.chunk().y())
            .thenComparingDouble(rc -> rc.chunk().x()));

    List<List<RenderChunk>> lines = new ArrayList<>();
    List<float[]> bands = new ArrayList<>(); // song song: [top, bottom]

    for (RenderChunk rc : sorted) {
      float top = rc.chunk().y();
      float bottom = top + rc.chunk().height();
      int match = -1;
      for (int i = 0; i < bands.size(); i++) {
        if (overlapRatio(top, bottom, bands.get(i)) >= LINE_OVERLAP_RATIO) {
          match = i;
          break;
        }
      }
      if (match < 0) {
        List<RenderChunk> line = new ArrayList<>();
        line.add(rc);
        lines.add(line);
        bands.add(new float[] {top, bottom});
      } else {
        lines.get(match).add(rc);
        float[] b = bands.get(match);
        b[0] = Math.min(b[0], top);
        b[1] = Math.max(b[1], bottom);
      }
    }
    return lines;
  }

  private static float overlapRatio(float top, float bottom, float[] band) {
    float overlap = Math.min(bottom, band[1]) - Math.max(top, band[0]);
    float minHeight = Math.min(bottom - top, band[1] - band[0]);
    return minHeight <= 0f ? 0f : overlap / minHeight;
  }

  /** Đo bằng ĐÚNG font render trên ĐÚNG text đã lọc -> không bao giờ gặp missing glyph. */
  private static float renderedWidth(
      PDType0Font renderFont, String safeText, float fontSizePt, float fallbackWidth) {
    if (safeText.isEmpty()) {
      return fallbackWidth;
    }
    try {
      return renderFont.getStringWidth(safeText) / 1000f * fontSizePt;
    } catch (IOException | IllegalArgumentException e) {
      return fallbackWidth;
    }
  }

  private static RenderChunk withGeometry(RenderChunk rc, float x, float width) {
    WordAwareStripper.WordChunk c = rc.chunk();
    if (Float.compare(x, c.x()) == 0 && Float.compare(width, c.width()) == 0) {
      return rc;
    }
    WordAwareStripper.WordChunk updated =
        new WordAwareStripper.WordChunk(
            c.text(), c.pageNo(), x, c.y(), c.fontSizePt(), c.font(), width, c.height());
    return new RenderChunk(updated, rc.safeText());
  }

  /** Cặp chunk gốc + text đã được lọc theo font render (dùng chung cho đo & vẽ). */
  private record RenderChunk(WordAwareStripper.WordChunk chunk, String safeText) {}
}
