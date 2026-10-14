package tungnn.tutor.java.document.adapter.pdf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.*;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.text.PDFTextStripper;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtil;
import tungnn.tutor.java.document.adapter.pdf.stripper.PositionAwareStripper;

public class PdfDocumentUtilBak {

  static void main() throws IOException {
    var pdDocument = readDocument(ResourceUtil.getResourceAsStream("sample-pdf-letter-size.pdf"));
    //    var stripper = new PDFTextStripper();
    var stripper = new PositionAwareStripper();
    // Load font mới — KHÔNG dùng chunk.font() vì đó là font subset
    InputStream fontStream =
        PDFont.class.getResourceAsStream(
            "/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf");
    PDType0Font newFont = PDType0Font.load(pdDocument, fontStream, false);
    var outputPath = Path.of("storage/output.pdf");
    PdfDocumentUtilBak.changeFontForDocument(pdDocument, fontStream, outputPath.toString());
    var pdDocument2 = readDocument(Files.newInputStream(outputPath));
    var outputPath2 = Path.of("storage/output2.pdf");
    PdfDocumentUtilBak.demoTranslateDocument2(pdDocument, outputPath2.toString());
  }

  public static PDDocument readDocument(InputStream inputStream) throws IOException {
    return Loader.loadPDF(new RandomAccessReadBuffer(inputStream));
  }

  public static List<String> extractText(PDDocument document, PDFTextStripper stripper)
      throws IOException {
    List<String> tokens = new ArrayList<>();
    stripper.setSortByPosition(true);

    int totalPages = document.getNumberOfPages();
    for (int i = 1; i <= totalPages; i++) {
      stripper.setStartPage(i);
      stripper.setEndPage(i);
      tokens.addAll(stripper.getText(document).lines().toList());
    }
    return tokens;
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

  /**
   * Thay toàn bộ font trong document bằng newFont, giữ nguyên graphics/images. newFontStream sẽ
   * được đóng bởi PDType0Font.load() trước khi method return.
   */
  public static void changeFontForDocument(
      PDDocument document, InputStream newFontStream, String outputPath) throws IOException {

    // 1. Load font mới vào cùng document — bắt buộc để tránh cross-document resource error
    PDType0Font newFont = PDType0Font.load(document, newFontStream, false);
    // PDType0Font.load() tự đóng stream trước khi return

    int totalPages = document.getNumberOfPages();

    for (int i = 0; i < totalPages; i++) {
      PDPage page = document.getPage(i);

      // Đảm bảo page có resources
      PDResources resources = page.getResources();
      if (resources == null) {
        resources = new PDResources();
        page.setResources(resources);
      }

      // 2. Thêm font mới vào resources của trang, lấy tên resource tự động (F1, F2, ...)
      //    Nếu font đã tồn tại trong resources, trả về key hiện có
      COSName newFontName = resources.add(newFont);

      // 3. Parse content stream thành token list
      PDFStreamParser parser = new PDFStreamParser(page);
      List<Object> tokens = parser.parse();

      PDFont currentOldFont = null; // track font cũ để decode bytes đúng encoding

      for (int j = 0; j < tokens.size(); j++) {
        Object token = tokens.get(j);

        // Detect Tf operator: pattern là [COSName(fontName), COSNumber(fontSize), Operator("Tf")]
        if (token instanceof COSName oldFontName
            && j + 2 < tokens.size()
            && tokens.get(j + 1) instanceof COSNumber
            && tokens.get(j + 2) instanceof Operator tfOp
            && OperatorName.SET_FONT_AND_SIZE.equals(tfOp.getName())) {

          // Lấy font cũ trước khi thay — cần để decode bytes của COSString sau này
          try {
            currentOldFont = resources.getFont(oldFontName);
          } catch (Exception e) {
            currentOldFont = null;
          }

          // Thay font name cũ → font name mới; giữ nguyên COSNumber (font size)
          tokens.set(j, newFontName);
          // j+1 (COSNumber) và j+2 (Operator Tf) không thay đổi
        }

        // Thay COSString cho Tj / ' / "
        if (token instanceof COSString original
            && j + 1 < tokens.size()
            && tokens.get(j + 1) instanceof Operator op) {
          String opName = op.getName();
          if (OperatorName.SHOW_TEXT.equals(opName)
              || OperatorName.SHOW_TEXT_LINE.equals(opName)
              || OperatorName.SHOW_TEXT_LINE_AND_SPACE.equals(opName)) {
            tokens.set(j, decodeString(original, currentOldFont, newFont));
          }
        }

        // Thay COSString bên trong array cho TJ
        if (token instanceof COSArray array
            && j + 1 < tokens.size()
            && tokens.get(j + 1) instanceof Operator op
            && OperatorName.SHOW_TEXT_ADJUSTED.equals(op.getName())) {
          for (int k = 0; k < array.size(); k++) {
            if (array.get(k) instanceof COSString elem) {
              array.set(k, decodeString(elem, currentOldFont, newFont));
            }
          }
        }
      }

      // 4. Ghi lại token list — graphics/images vẫn còn nguyên vì không bị đụng đến
      PDStream newContent = new PDStream(document);
      try (OutputStream os = newContent.createOutputStream(COSName.FLATE_DECODE)) {
        new ContentStreamWriter(os).writeTokens(tokens);
      }
      page.setContents(newContent);
    }

    // 5. Lưu ra file tên khác (không ghi đè file gốc)
    document.save(outputPath);
  }

  /**
   * Decode bytes từ oldFont encoding → Unicode, lọc PUA garbage, rồi re-encode theo newFont
   * encoding. Trả về COSString gốc nếu decode/encode thất bại.
   */
  private static COSString decodeString(COSString original, PDFont oldFont, PDFont newFont) {

    if (oldFont == null) return original;

    try {
      // Decode font-encoded bytes → Unicode qua oldFont
      ByteArrayInputStream in = new ByteArrayInputStream(original.getBytes());
      StringBuilder unicode = new StringBuilder();
      while (in.available() > 0) {
        int code = oldFont.readCode(in);
        String ch = oldFont.toUnicode(code);
        // Lọc Private Use Area characters (garbage khi font thiếu ToUnicode CMap)
        if (ch != null
            && ch.codePoints().noneMatch(cp -> Character.getType(cp) == Character.PRIVATE_USE)) {
          unicode.append(ch);
        }
      }

      if (unicode.isEmpty()) return original;

      // Re-encode Unicode → bytes theo newFont
      byte[] newBytes = newFont.encode(unicode.toString());
      return new COSString(newBytes); // dùng byte[] constructor, KHÔNG dùng String constructor
    } catch (Exception e) {
      return original; // fallback: giữ nguyên nếu ký tự không có trong newFont
    }
  }

  public static void demoTranslateDocument(PDDocument document, String outputPath)
      throws IOException {
    for (PDPage page : document.getPages()) {
      PDResources resources = page.getResources();
      PDFStreamParser parser = new PDFStreamParser(page);
      List<Object> tokens = parser.parse();

      PDFont currentFont = null;

      for (int i = 0; i < tokens.size(); i++) {
        Object token = tokens.get(i);

        if (token instanceof Operator op && OperatorName.SET_FONT_AND_SIZE.equals(op.getName())) {
          if (i >= 2 && tokens.get(i - 2) instanceof COSName fontName) {
            currentFont = resources.getFont(fontName);
          }
          continue;
        }

        if (!(token instanceof COSString cosString) || currentFont == null) {
          continue;
        }

        // Decode font-encoded bytes → Unicode
        byte[] rawBytes = cosString.getBytes();
        StringBuilder unicodeText = new StringBuilder();
        ByteArrayInputStream in = new ByteArrayInputStream(rawBytes);
        while (in.available() > 0) {
          int code = currentFont.readCode(in);
          String unicode = currentFont.toUnicode(code);
          if (unicode != null) {
            unicodeText.append(unicode);
          }
        }

        // Modify text
        String modifiedText = modifyText(unicodeText.toString());

        // Re-encode Unicode → font-encoded bytes, dùng byte[] constructor
        byte[] encodedBytes = currentFont.encode(modifiedText);
        tokens.set(i, new COSString(encodedBytes));
      }

      PDStream newContent = new PDStream(document);
      try (OutputStream os = newContent.createOutputStream(COSName.FLATE_DECODE)) {
        ContentStreamWriter writer = new ContentStreamWriter(os);
        writer.writeTokens(tokens);
      }
      page.setContents(newContent);
    }

    document.save(outputPath);
  }

  private static String modifyText(String text) {
    System.out.println(text);
    return text.toUpperCase();
  }

  public static void demoTranslateDocument2(PDDocument document, String outputPath)
      throws IOException {
    for (PDPage page : document.getPages()) {
      PDResources resources = page.getResources();
      PDFStreamParser parser = new PDFStreamParser(page);
      List<Object> tokens = parser.parse();

      PDFont currentFont = null;
      float currentHorizontalScaling = 100f; // default Tz value
      List<Object> newTokens = new ArrayList<>();

      for (int i = 0; i < tokens.size(); i++) {
        Object token = tokens.get(i);

        // Track Tz operator để biết horizontal scaling hiện tại
        if (token instanceof Operator op
            && OperatorName.SET_TEXT_HORIZONTAL_SCALING.equals(op.getName())) {
          if (i >= 1 && tokens.get(i - 1) instanceof COSNumber scalingNum) {
            currentHorizontalScaling = scalingNum.floatValue();
          }
          newTokens.add(token);
          continue;
        }

        // Track Tf operator để biết font hiện tại
        if (token instanceof Operator op && OperatorName.SET_FONT_AND_SIZE.equals(op.getName())) {
          if (i >= 2 && tokens.get(i - 2) instanceof COSName fontName) {
            currentFont = resources.getFont(fontName);
          }
          newTokens.add(token);
          continue;
        }

        if (!(token instanceof COSString cosString) || currentFont == null) {
          newTokens.add(token);
          continue;
        }

        // Decode font-encoded bytes → Unicode
        byte[] rawBytes = cosString.getBytes();
        StringBuilder unicodeText = new StringBuilder();
        try (ByteArrayInputStream in = new ByteArrayInputStream(rawBytes)) {
          while (in.available() > 0) {
            int code = currentFont.readCode(in);
            String unicode = currentFont.toUnicode(code);
            if (unicode != null) {
              unicodeText.append(unicode);
            }
          }
        }

        String originalText = unicodeText.toString();
        String modifiedText = modifyText(originalText);

        // Tính width để điều chỉnh horizontal scaling
        float originalWidth = currentFont.getStringWidth(originalText);
        float modifiedWidth = currentFont.getStringWidth(modifiedText);

        boolean needsScaling =
            originalWidth > 0
                && modifiedWidth > 0
                && Float.compare(originalWidth, modifiedWidth) != 0;

        if (needsScaling) {
          // Chèn Tz adjusted trước string
          float adjustedScaling = currentHorizontalScaling * originalWidth / modifiedWidth;
          newTokens.add(new COSFloat(adjustedScaling));
          newTokens.add(Operator.getOperator(OperatorName.SET_TEXT_HORIZONTAL_SCALING));
        }

        // Re-encode và thêm modified string
        byte[] encodedBytes = currentFont.encode(modifiedText);
        newTokens.add(new COSString(encodedBytes));

        if (needsScaling) {
          // Restore Tz về giá trị gốc sau string
          newTokens.add(new COSFloat(currentHorizontalScaling));
          newTokens.add(Operator.getOperator(OperatorName.SET_TEXT_HORIZONTAL_SCALING));
        }
      }

      PDStream newContent = new PDStream(document);
      try (OutputStream os = newContent.createOutputStream(COSName.FLATE_DECODE)) {
        ContentStreamWriter writer = new ContentStreamWriter(os);
        writer.writeTokens(newTokens);
      }
      page.setContents(newContent);
    }

    document.save(outputPath);
  }
}
