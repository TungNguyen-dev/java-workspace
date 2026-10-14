package tungnn.tutor.java.document.adapter.powerpoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.poi.xslf.usermodel.*;

public final class PowerPointUtil {

  private PowerPointUtil() {}

  public static XMLSlideShow readPresentation(InputStream inputStream) throws IOException {
    return new XMLSlideShow(inputStream);
  }

  public static void writePresentation(XMLSlideShow presentation, OutputStream outputStream)
      throws IOException {
    presentation.write(outputStream);
  }

  public static Map<String, List<XSLFTextRun>> collectTextRuns(
      XMLSlideShow presentation, Predicate<String> textPredicate) {

    var textToRuns = new LinkedHashMap<String, List<XSLFTextRun>>();

    presentation
        .getSlides()
        .forEach(
            slide ->
                slide.getShapes().forEach(shape -> collectShape(shape, textPredicate, textToRuns)));

    return textToRuns;
  }

  public static void applyTranslations(
      Map<String, List<XSLFTextRun>> textToRuns, Map<String, String> translations) {

    textToRuns.forEach(
        (source, runs) -> {
          var translated = translations.get(source);
          if (translated != null) {
            runs.forEach(run -> run.setText(translated));
          }
        });
  }

  public static String extractAllText(XMLSlideShow presentation) {
    throw new UnsupportedOperationException("Use TextExtractorUtil#extractext() instead.");
  }

  private static void collectShape(
      XSLFShape shape, Predicate<String> textPredicate, Map<String, List<XSLFTextRun>> textToRuns) {

    if (shape instanceof XSLFGroupShape groupShape) {
      groupShape.getShapes().forEach(child -> collectShape(child, textPredicate, textToRuns));
      return;
    }

    if (shape instanceof XSLFTable table) {
      collectTable(table, textPredicate, textToRuns);
      return;
    }

    if (shape instanceof XSLFTextShape textShape) {
      collectTextShape(textShape, textPredicate, textToRuns);
    }
  }

  private static void collectTable(
      XSLFTable table, Predicate<String> textPredicate, Map<String, List<XSLFTextRun>> textToRuns) {

    table
        .getRows()
        .forEach(
            row ->
                row.getCells().forEach(cell -> collectTableCell(cell, textPredicate, textToRuns)));
  }

  private static void collectTableCell(
      XSLFTableCell cell,
      Predicate<String> textPredicate,
      Map<String, List<XSLFTextRun>> textToRuns) {

    collectTextShape(cell, textPredicate, textToRuns);
  }

  private static void collectTextShape(
      XSLFTextShape textShape,
      Predicate<String> textPredicate,
      Map<String, List<XSLFTextRun>> textToRuns) {

    for (XSLFTextParagraph paragraph : textShape.getTextParagraphs()) {
      for (XSLFTextRun run : paragraph.getTextRuns()) {
        var text = normalize(run.getRawText());
        if (textPredicate.test(text)) {
          textToRuns.computeIfAbsent(text, ignored -> new ArrayList<>()).add(run);
        }
      }
    }
  }

  private static String normalize(String value) {
    return value == null ? "" : value.strip();
  }
}
