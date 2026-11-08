package tungnn.tutor.java.infra.document.converter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;

/** Utility class for converting Word (.docx) documents to Markdown format. */
public final class DocxMarkdownConverterUtils {

  private DocxMarkdownConverterUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  // ==========================================================================
  // Public API: Docx to Markdown
  // ==========================================================================

  /**
   * Converts a Word .docx file into a Markdown file.
   *
   * @param docxPath path to the input Word document
   * @param mdPath path to the output Markdown file
   * @throws IOException if reading or writing fails
   */
  public static void convertDocxToMarkdown(Path docxPath, Path mdPath) throws IOException {
    var markdown = toMarkdown(docxPath);
    Files.writeString(mdPath, markdown, StandardCharsets.UTF_8);
  }

  /**
   * Parses a Word .docx file and returns its content as a Markdown string.
   *
   * @param docxPath path to the input Word document
   * @return Markdown formatted string
   * @throws IOException if reading fails
   */
  public static String toMarkdown(Path docxPath) throws IOException {
    try (var inputStream = Files.newInputStream(docxPath);
        var document = new XWPFDocument(inputStream)) {

      var sb = new StringBuilder();
      for (var element : document.getBodyElements()) {
        if (element instanceof XWPFParagraph paragraph) {
          var mdText = paragraphToMarkdown(paragraph);
          if (!mdText.isBlank()) {
            sb.append(mdText).append("\n\n");
          }
        } else if (element instanceof XWPFTable table) {
          var mdTable = tableToMarkdown(table);
          if (!mdTable.isBlank()) {
            sb.append(mdTable).append("\n\n");
          }
        }
      }

      return sb.toString().stripTrailing() + "\n";
    }
  }

  // ==========================================================================
  // Internal Helpers
  // ==========================================================================

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

    var numID = paragraph.getNumID() != null ? paragraph.getNumID().toString() : null;
    if (numID != null || paragraph.getNumFmt() != null) {
      return "- " + processRunsToMarkdown(paragraph);
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

    sb.append("|").repeat(" --- |", maxCols);

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
}
