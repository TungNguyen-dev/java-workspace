package tungnn.tutor.java.document.adapter.word;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.poi.xwpf.usermodel.*;

public final class WordUtil {

  private WordUtil() {}

  public static XWPFDocument readDocument(InputStream inputStream) throws IOException {
    return new XWPFDocument(inputStream);
  }

  public static void writeDocument(XWPFDocument document, OutputStream outputStream)
      throws IOException {
    document.write(outputStream);
  }

  public static Map<String, List<XWPFRun>> collectTextRuns(
      XWPFDocument document, Predicate<String> textPredicate) {

    var textToRuns = new LinkedHashMap<String, List<XWPFRun>>();

    collectBodyElements(document.getBodyElements(), textPredicate, textToRuns);

    for (XWPFHeader header : document.getHeaderList()) {
      collectParagraphs(header.getParagraphs(), textPredicate, textToRuns);
      collectTables(header.getTables(), textPredicate, textToRuns);
    }

    for (XWPFFooter footer : document.getFooterList()) {
      collectParagraphs(footer.getParagraphs(), textPredicate, textToRuns);
      collectTables(footer.getTables(), textPredicate, textToRuns);
    }

    return textToRuns;
  }

  public static void applyTranslations(
      Map<String, List<XWPFRun>> textToRuns, Map<String, String> translations) {

    textToRuns.forEach(
        (source, runs) -> {
          var translated = translations.get(source);
          if (translated != null) {
            runs.forEach(run -> run.setText(translated, 0));
          }
        });
  }

  public static String extractAllText(XWPFDocument document) {
    throw new UnsupportedOperationException("Use TextExtractorUtil#extractext() instead.");
  }

  private static void collectBodyElements(
      List<IBodyElement> bodyElements,
      Predicate<String> textPredicate,
      Map<String, List<XWPFRun>> textToRuns) {

    for (IBodyElement element : bodyElements) {
      if (element instanceof XWPFParagraph paragraph) {
        collectParagraph(paragraph, textPredicate, textToRuns);
      } else if (element instanceof XWPFTable table) {
        collectTable(table, textPredicate, textToRuns);
      }
    }
  }

  private static void collectParagraphs(
      List<XWPFParagraph> paragraphs,
      Predicate<String> textPredicate,
      Map<String, List<XWPFRun>> textToRuns) {

    paragraphs.forEach(paragraph -> collectParagraph(paragraph, textPredicate, textToRuns));
  }

  private static void collectTables(
      List<XWPFTable> tables,
      Predicate<String> textPredicate,
      Map<String, List<XWPFRun>> textToRuns) {

    tables.forEach(table -> collectTable(table, textPredicate, textToRuns));
  }

  private static void collectTable(
      XWPFTable table, Predicate<String> textPredicate, Map<String, List<XWPFRun>> textToRuns) {

    table
        .getRows()
        .forEach(
            row ->
                row.getTableCells()
                    .forEach(cell -> collectTableCell(cell, textPredicate, textToRuns)));
  }

  private static void collectTableCell(
      XWPFTableCell cell, Predicate<String> textPredicate, Map<String, List<XWPFRun>> textToRuns) {

    collectParagraphs(cell.getParagraphs(), textPredicate, textToRuns);
    collectTables(cell.getTables(), textPredicate, textToRuns);
  }

  private static void collectParagraph(
      XWPFParagraph paragraph,
      Predicate<String> textPredicate,
      Map<String, List<XWPFRun>> textToRuns) {

    for (XWPFRun run : paragraph.getRuns()) {
      var text = normalize(run.getText(0));
      if (textPredicate.test(text)) {
        textToRuns.computeIfAbsent(text, ignored -> new ArrayList<>()).add(run);
      }
    }
  }

  private static String normalize(String value) {
    return value == null ? "" : value.strip();
  }
}
