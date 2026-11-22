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
      var previousWasListItem = false;
      BigInteger currentTopListId = null;

      for (var element : document.getBodyElements()) {
        if (element instanceof XWPFParagraph paragraph) {
          var listItem = listItemToMarkdown(document, paragraph, lists);
          var mdText = listItem != null ? listItem : paragraphToMarkdown(paragraph);
          if (mdText.isBlank()) {
            continue;
          }
          if (listItem != null) {
            var numId = paragraph.getNumID();
            var topLevel = paragraph.getNumIlvl() == null || paragraph.getNumIlvl().intValue() == 0;
            var newList = topLevel && numId != null && !numId.equals(currentTopListId);
            if (previousWasListItem && !newList) {
              sb.setLength(sb.length() - 1);
            }
            if (topLevel) {
              currentTopListId = numId;
            }
          } else {
            lists.reset();
            currentTopListId = null;
          }
          sb.append(mdText).append("\n");
          previousWasListItem = listItem != null;
        } else if (element instanceof XWPFTable table) {
          var mdTable = tableToMarkdown(table);
          if (!mdTable.isBlank()) {
            sb.append(mdTable).append("\n");
          }
          lists.reset();
          currentTopListId = null;
          previousWasListItem = false;
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

  private static String processRunsToMarkdown(XWPFParagraph paragraph) {
    var sb = new StringBuilder();
    var runs = paragraph.getRuns();
    if (runs == null || runs.isEmpty()) {
      return paragraph.getText();
    }
    for (var run : runs) {
      var runText = run.getText(0);
      if (runText == null || runText.isEmpty()) {
        continue;
      }
      var isBold = run.isBold();
      var isItalic = run.isItalic();
      var fontFamily = run.getFontFamily();
      var isCode =
          fontFamily != null
              && (fontFamily.equalsIgnoreCase("Courier New")
                  || fontFamily.toLowerCase().contains("code"));
      if (isCode) {
        runText = "`" + runText + "`";
      } else {
        if (isBold && isItalic) {
          runText = "***" + runText + "***";
        } else if (isBold) {
          runText = "**" + runText + "**";
        } else if (isItalic) {
          runText = "*" + runText + "*";
        }
      }
      sb.append(runText);
    }
    return sb.toString();
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
