package tungnn.tutor.java.infra.document.converter;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFSDT;
import org.apache.poi.xwpf.usermodel.XWPFTable;

public final class DocxMarkdownConverterUtils {

  private static final Pattern MANUAL_BULLET =
      Pattern.compile("^\\s*[\\u2022\\u25E6\\u25AA\\u25CF\\u00B7]\\s+");

  private DocxMarkdownConverterUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static void convertDocxToMarkdown(Path docxPath, Path mdPath) throws IOException {
    var markdown = toMarkdown(docxPath);
    Files.writeString(mdPath, markdown, StandardCharsets.UTF_8);
  }

  public static String toMarkdown(Path docxPath) throws IOException {
    try (var inputStream = Files.newInputStream(docxPath);
        var document = new XWPFDocument(inputStream)) {
      var sb = new StringBuilder();
      var lists = new ListCounters();

      for (var element : document.getBodyElements()) {
        if (element instanceof XWPFParagraph paragraph) {
          var listItem = listItemToMarkdown(document, paragraph, lists);
          var mdText = listItem != null ? listItem : paragraphToMarkdown(paragraph);
          if (mdText.isBlank()) {
            continue;
          }
          if (listItem == null) {
            lists.reset();
          }
          // Thêm newline cho mỗi item/paragraph (tạo dòng trống phân tách rõ ràng)
          sb.append(mdText).append("\n\n");
        } else if (element instanceof XWPFTable table) {
          var mdTable = tableToMarkdown(table);
          if (!mdTable.isBlank()) {
            sb.append(mdTable).append("\n\n");
          }
          lists.reset();
        }
      }
      return sb.toString().stripTrailing() + "\n";
    }
  }

  private static String listItemToMarkdown(
      XWPFDocument document, XWPFParagraph paragraph, ListCounters lists) {
    var styleName = paragraph.getStyle() != null ? paragraph.getStyle().toLowerCase() : "";
    if (styleName.contains("heading")) {
      return null;
    }
    var numId = paragraph.getNumID();
    if (numId != null) {
      var level =
          paragraph.getNumIlvl() != null ? Math.clamp(paragraph.getNumIlvl().intValue(), 0, 8) : 0;
      var indent = "  ".repeat(level);
      var content = processRunsToMarkdown(paragraph).strip();
      var fmt = paragraph.getNumFmt();
      if (fmt == null || "bullet".equalsIgnoreCase(fmt) || "none".equalsIgnoreCase(fmt)) {
        return indent + "- " + content;
      }
      var n = lists.next(numId, level, startOf(document, numId, level));
      return indent + n + ". " + content;
    }
    var content = processRunsToMarkdown(paragraph);
    var m = MANUAL_BULLET.matcher(content);
    if (m.find()) {
      return "- " + content.substring(m.end()).strip();
    }
    return null;
  }

  private static int startOf(XWPFDocument document, BigInteger numId, int level) {
    try {
      var numbering = document.getNumbering();
      var abstractNum = numbering.getAbstractNum(numbering.getAbstractNumID(numId));
      for (var lvl : abstractNum.getCTAbstractNum().getLvlArray()) {
        if (lvl.getIlvl().intValue() == level && lvl.isSetStart()) {
          return lvl.getStart().getVal().intValue();
        }
      }
    } catch (RuntimeException ignored) {
    }
    return 1;
  }

  private static String paragraphToMarkdown(XWPFParagraph paragraph) {
    var text = paragraph.getText();
    if (text == null || text.isBlank()) {
      return "";
    }
    var styleName = paragraph.getStyle() != null ? paragraph.getStyle().toLowerCase() : "";
    if (styleName.contains("heading")) {
      var level = 1;
      try {
        var levelStr = styleName.replaceAll("[^0-9]", "");
        if (!levelStr.isEmpty()) {
          level = Math.clamp(Integer.parseInt(levelStr), 1, 6);
        }
      } catch (NumberFormatException ignored) {
        level = 1;
      }
      return "#".repeat(level) + " " + text.strip();
    }
    var inlineMarkdown = processRunsToMarkdown(paragraph);
    return inlineMarkdown.isEmpty() ? text.strip() : inlineMarkdown;
  }

  /**
   * Gom text của paragraph thành markdown inline.
   *
   * <p>Dùng {@link XWPFRun#text()} (không phải {@code getText(0)}): một run có thể chứa nhiều
   * {@code <w:t>} xen kẽ {@code <w:tab/>}/{@code <w:br/>} (ví dụ "Tiết 1&lt;tab&gt;&lt;tab&gt;Thanh
   * khí phận nhiệt"), {@code getText(0)} chỉ trả về phần text đầu tiên nên mất phần còn lại của
   * dòng. Đồng thời duyệt {@link XWPFParagraph#getIRuns()} để không bỏ sót text nằm trong content
   * control (SDT).
   */
  private static String processRunsToMarkdown(XWPFParagraph paragraph) {
    var segments = new ArrayList<Segment>();
    for (var element : paragraph.getIRuns()) {
      if (element instanceof XWPFRun run) {
        var text = run.text();
        if (text == null || text.isEmpty()) {
          continue;
        }
        var fontFamily = run.getFontFamily();
        var isCode =
            fontFamily != null
                && (fontFamily.equalsIgnoreCase("Courier New")
                    || fontFamily.toLowerCase().contains("code"));
        addSegment(
            segments,
            new Segment(text, !isCode && run.isBold(), !isCode && run.isItalic(), isCode));
      } else if (element instanceof XWPFSDT sdt && sdt.getContent() != null) {
        var text = sdt.getContent().getText();
        if (text != null && !text.isEmpty()) {
          addSegment(segments, new Segment(text, false, false, false));
        }
      }
    }
    if (segments.isEmpty()) {
      var text = paragraph.getText();
      return text == null ? "" : text;
    }
    var sb = new StringBuilder();
    for (var segment : segments) {
      sb.append(renderSegment(segment));
    }
    // Tab/xuống dòng ở đầu hoặc cuối paragraph không mang nghĩa (và tab đầu dòng sẽ thành code
    // block).
    return sb.toString().strip();
  }

  /** Gộp segment liền nhau có cùng định dạng để không sinh "**a****b**". */
  private static void addSegment(List<Segment> segments, Segment next) {
    if (!segments.isEmpty()) {
      var last = segments.getLast();
      if (last.sameStyle(next)) {
        segments.set(
            segments.size() - 1,
            new Segment(last.text() + next.text(), last.bold(), last.italic(), last.code()));
        return;
      }
    }
    segments.add(next);
  }

  /** Dấu định dạng ôm sát phần chữ, khoảng trắng đầu/cuối để ngoài dấu (tránh "**text **"). */
  private static String renderSegment(Segment segment) {
    var text = segment.text();
    var core = text.strip();
    if (core.isEmpty()) {
      return text;
    }
    var lead = text.substring(0, text.indexOf(core.charAt(0)));
    var trail = text.substring(lead.length() + core.length());
    String marked;
    if (segment.code()) {
      marked = "`" + core + "`";
    } else if (segment.bold() && segment.italic()) {
      marked = "***" + core + "***";
    } else if (segment.bold()) {
      marked = "**" + core + "**";
    } else if (segment.italic()) {
      marked = "*" + core + "*";
    } else {
      marked = core;
    }
    return lead + marked + trail;
  }

  private static String tableToMarkdown(XWPFTable table) {
    var rows = table.getRows();
    if (rows == null || rows.isEmpty()) {
      return "";
    }
    var sb = new StringBuilder();
    var parsedRows = new ArrayList<List<String>>();
    var maxCols = 0;
    for (var row : rows) {
      var cells = row.getTableCells();
      var rowCells = new ArrayList<String>();
      for (var cell : cells) {
        var cellText = cell.getText() != null ? cell.getText().strip().replace("\n", " ") : "";
        rowCells.add(cellText);
      }
      parsedRows.add(rowCells);
      maxCols = Math.max(maxCols, rowCells.size());
    }
    if (maxCols == 0) {
      return "";
    }
    var header = parsedRows.getFirst();
    sb.append("| ");
    for (int c = 0; c < maxCols; c++) {
      sb.append(c < header.size() ? header.get(c) : "").append(" | ");
    }
    sb.setLength(sb.length() - 1);
    sb.append("\n");
    sb.append("|").append(" --- |".repeat(maxCols));
    for (int r = 1; r < parsedRows.size(); r++) {
      sb.append("\n| ");
      var rowData = parsedRows.get(r);
      for (int c = 0; c < maxCols; c++) {
        sb.append(c < rowData.size() ? rowData.get(c) : "").append(" | ");
      }
      sb.setLength(sb.length() - 1);
    }
    return sb.toString();
  }

  /** Đoạn text liên tiếp có cùng định dạng inline. */
  private record Segment(String text, boolean bold, boolean italic, boolean code) {
    boolean sameStyle(Segment other) {
      return bold == other.bold && italic == other.italic && code == other.code;
    }
  }

  private static final class ListCounters {
    private final Map<BigInteger, int[]> counters = new HashMap<>();

    int next(BigInteger numId, int level, int start) {
      var c = counters.computeIfAbsent(numId, k -> new int[9]);
      c[level] = c[level] == 0 ? start : c[level] + 1;
      for (int i = level + 1; i < c.length; i++) {
        c[i] = 0;
      }
      return c[level];
    }

    void reset() {
      counters.clear();
    }
  }
}
