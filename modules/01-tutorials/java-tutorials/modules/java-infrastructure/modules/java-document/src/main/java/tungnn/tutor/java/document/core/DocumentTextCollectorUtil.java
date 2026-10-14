package tungnn.tutor.java.document.core;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTxbxContent;

public final class DocumentTextCollectorUtil {

  private DocumentTextCollectorUtil() {}

  // ---------- Word ----------
  public static List<XWPFRun> collectTextRuns(XWPFDocument document) {
    var runs = new ArrayList<XWPFRun>();
    for (XWPFHeader header : document.getHeaderList()) {
      collectTextRunsInWord(header.getBodyElements(), runs);
    }
    collectTextRunsInWord(document.getBodyElements(), runs);
    for (XWPFFooter footer : document.getFooterList()) {
      collectTextRunsInWord(footer.getBodyElements(), runs);
    }
    collectTextRunsInBox(document, runs);
    return runs;
  }

  private static void collectTextRunsInBox(XWPFDocument document, List<XWPFRun> runs) {
    try (XmlCursor cursor = document.getDocument().newCursor()) {
      while (cursor.hasNextToken()) {
        if (cursor.isStart() && cursor.getObject() instanceof CTTxbxContent txbx) {
          for (CTP ctp : txbx.getPList()) {
            runs.addAll(new XWPFParagraph(ctp, document).getRuns());
          }
          for (CTTbl tbl : txbx.getTblList()) {
            var table = new XWPFTable(tbl, document);
            for (XWPFTableRow row : table.getRows()) {
              for (XWPFTableCell cell : row.getTableCells()) {
                collectTextRunsInWord(cell.getBodyElements(), runs);
              }
            }
          }
        }
        cursor.toNextToken();
      }
    }
  }

  private static void collectTextRunsInWord(List<IBodyElement> elements, List<XWPFRun> runs) {
    for (IBodyElement element : elements) {
      switch (element) {
        case XWPFParagraph paragraph -> runs.addAll(paragraph.getRuns());
        case XWPFTable table -> {
          for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
              collectTextRunsInWord(cell.getBodyElements(), runs);
            }
          }
        }
        default -> {
          /* ignore */
        }
      }
    }
  }

  // ---------- Excel ----------
  public static List<RichTextString> collectRichTexts(Workbook document) {
    var texts = new ArrayList<RichTextString>();
    for (Sheet sheet : document) {
      for (Row row : sheet) {
        for (Cell cell : row) {
          if (cell.getCellType() == CellType.STRING) {
            texts.add(cell.getRichStringCellValue());
          }
          Comment comment = cell.getCellComment();
          if (comment != null) {
            texts.add(comment.getString());
          }
        }
      }
    }
    return texts;
  }

  public static List<XSSFTextRun> collectTextRunsInShape(Workbook document) {
    var runs = new ArrayList<XSSFTextRun>();

    for (Sheet sheet : document) {
      if (!(sheet instanceof XSSFSheet xssfSheet)) {
        continue;
      }

      XSSFDrawing drawing = xssfSheet.getDrawingPatriarch();
      if (drawing != null) {
        collectTextRunsInShape(drawing.getShapes(), runs);
      }
    }

    return runs;
  }

  public static List<HeaderFooter> collectHeaders(Workbook document) {
    var headers = new ArrayList<HeaderFooter>();

    for (Sheet sheet : document) {
      var header = sheet.getHeader();

      if (hasText(header)) {
        headers.add(header);
      }
    }

    return headers;
  }

  public static List<HeaderFooter> collectFooters(Workbook document) {
    var footers = new ArrayList<HeaderFooter>();

    for (Sheet sheet : document) {
      var footer = sheet.getFooter();

      if (hasText(footer)) {
        footers.add(footer);
      }
    }

    return footers;
  }

  private static void collectTextRunsInShape(List<XSSFShape> shapes, List<XSSFTextRun> runs) {
    for (XSSFShape shape : shapes) {
      switch (shape) {
        case XSSFShapeGroup group -> collectTextRunsInShapeGroup(group, runs);
        case XSSFSimpleShape simpleShape -> collectTextRunsInSimpleShape(simpleShape, runs);
        default -> {
          /* ignore image, chart, object,... */
        }
      }
    }
  }

  private static void collectTextRunsInShapeGroup(XSSFShapeGroup group, List<XSSFTextRun> runs) {
    var children = new ArrayList<XSSFShape>();
    group.iterator().forEachRemaining(children::add);
    collectTextRunsInShape(children, runs);
  }

  private static void collectTextRunsInSimpleShape(XSSFSimpleShape shape, List<XSSFTextRun> runs) {
    shape.getTextParagraphs().forEach(paragraph -> runs.addAll(paragraph.getTextRuns()));
  }

  private static boolean hasText(String text) {
    return text != null && !text.isBlank();
  }

  private static boolean hasText(HeaderFooter headerFooter) {
    return hasText(headerFooter.getLeft())
        || hasText(headerFooter.getCenter())
        || hasText(headerFooter.getRight());
  }

  // ---------- PowerPoint ----------
  public static List<XSLFTextRun> collectTextRuns(XMLSlideShow document) {
    var runs = new ArrayList<XSLFTextRun>();

    collectTextRunsInSlides(document, runs);
    collectTextRunsInMasters(document, runs);

    return runs;
  }

  private static void collectTextRunsInSlides(XMLSlideShow document, List<XSLFTextRun> runs) {
    for (XSLFSlide slide : document.getSlides()) {
      collectTextRunsInSheet(slide, runs);

      var notes = slide.getNotes();
      if (notes != null) {
        collectTextRunsInSheet(notes, runs);
      }
    }
  }

  private static void collectTextRunsInMasters(XMLSlideShow document, List<XSLFTextRun> runs) {
    for (XSLFSlideMaster master : document.getSlideMasters()) {
      collectTextRunsInSheet(master, runs);

      for (XSLFSlideLayout layout : master.getSlideLayouts()) {
        collectTextRunsInSheet(layout, runs);
      }
    }
  }

  private static void collectTextRunsInSheet(XSLFSheet sheet, List<XSLFTextRun> runs) {
    collectTextRunsInSlideShow(sheet.getShapes(), runs);
  }

  private static void collectTextRunsInSlideShow(List<XSLFShape> shapes, List<XSLFTextRun> runs) {
    for (XSLFShape shape : shapes) {
      switch (shape) {
        case XSLFGroupShape group -> collectTextRunsInSlideShow(group.getShapes(), runs);
        case XSLFTable table -> collectTextRunsInTable(table, runs);
        case XSLFTextShape textShape -> collectTextRunsInTextShape(textShape, runs);
        default -> {
          /* ignore image, chart, media,... */
        }
      }
    }
  }

  private static void collectTextRunsInTable(XSLFTable table, List<XSLFTextRun> runs) {
    for (XSLFTableRow row : table.getRows()) {
      for (XSLFTableCell cell : row.getCells()) {
        collectTextRunsInTextShape(cell, runs);
      }
    }
  }

  private static void collectTextRunsInTextShape(XSLFTextShape textShape, List<XSLFTextRun> runs) {
    textShape.getTextParagraphs().forEach(paragraph -> runs.addAll(paragraph.getTextRuns()));
  }
}
