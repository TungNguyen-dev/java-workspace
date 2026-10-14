package tungnn.tutor.java.document.core;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTxbxContent;

public final class DocumentSemanticUnitCollector {

  private DocumentSemanticUnitCollector() {}

  // ============================================================
  // Word
  // ============================================================

  public static List<XWPFParagraph> collectParagraphs(XWPFDocument document) {
    var paragraphs = new ArrayList<XWPFParagraph>();

    for (var header : document.getHeaderList()) {
      collectParagraphs(header.getBodyElements(), paragraphs);
    }

    collectParagraphs(document.getBodyElements(), paragraphs);

    for (var footer : document.getFooterList()) {
      collectParagraphs(footer.getBodyElements(), paragraphs);
    }

    collectTextBoxParagraphs(document, paragraphs);

    return paragraphs;
  }

  public static List<XWPFTableCell> collectTableCells(XWPFDocument document) {
    var cells = new ArrayList<XWPFTableCell>();

    for (var header : document.getHeaderList()) {
      collectTableCells(header.getBodyElements(), cells);
    }

    collectTableCells(document.getBodyElements(), cells);

    for (var footer : document.getFooterList()) {
      collectTableCells(footer.getBodyElements(), cells);
    }

    collectTextBoxTableCells(document, cells);

    return cells;
  }

  private static void collectParagraphs(
      List<IBodyElement> elements, List<XWPFParagraph> paragraphs) {

    for (var element : elements) {
      switch (element) {
        case XWPFParagraph paragraph -> paragraphs.add(paragraph);

        case XWPFTable table -> {
          for (var row : table.getRows()) {
            for (var cell : row.getTableCells()) {
              collectParagraphs(cell.getBodyElements(), paragraphs);
            }
          }
        }

        default -> {}
      }
    }
  }

  private static void collectTableCells(List<IBodyElement> elements, List<XWPFTableCell> cells) {

    for (var element : elements) {
      if (element instanceof XWPFTable table) {
        for (var row : table.getRows()) {
          cells.addAll(row.getTableCells());

          for (var cell : row.getTableCells()) {
            collectTableCells(cell.getBodyElements(), cells);
          }
        }
      }
    }
  }

  private static void collectTextBoxParagraphs(
      XWPFDocument document, List<XWPFParagraph> paragraphs) {

    try (XmlCursor cursor = document.getDocument().newCursor()) {

      while (cursor.hasNextToken()) {

        if (cursor.isStart() && cursor.getObject() instanceof CTTxbxContent txbx) {

          txbx.getPList().forEach(ctp -> paragraphs.add(new XWPFParagraph(ctp, document)));
        }

        cursor.toNextToken();
      }
    }
  }

  private static void collectTextBoxTableCells(XWPFDocument document, List<XWPFTableCell> cells) {

    try (XmlCursor cursor = document.getDocument().newCursor()) {

      while (cursor.hasNextToken()) {

        if (cursor.isStart() && cursor.getObject() instanceof CTTxbxContent txbx) {

          for (CTTbl tbl : txbx.getTblList()) {
            var table = new XWPFTable(tbl, document);

            for (var row : table.getRows()) {
              cells.addAll(row.getTableCells());

              for (var cell : row.getTableCells()) {
                collectTableCells(cell.getBodyElements(), cells);
              }
            }
          }
        }

        cursor.toNextToken();
      }
    }
  }

  // ============================================================
  // Excel
  // ============================================================

  public static List<Cell> collectCells(Workbook workbook) {
    var cells = new ArrayList<Cell>();

    for (Sheet sheet : workbook) {
      for (Row row : sheet) {
        for (Cell cell : row) {

          if (cell.getCellType() == CellType.STRING) {
            cells.add(cell);
          }
        }
      }
    }

    return cells;
  }

  public static List<Comment> collectComments(Workbook workbook) {
    var comments = new ArrayList<Comment>();

    for (Sheet sheet : workbook) {
      for (Row row : sheet) {
        for (Cell cell : row) {

          var comment = cell.getCellComment();

          if (comment != null) {
            comments.add(comment);
          }
        }
      }
    }

    return comments;
  }

  public static List<Header> collectHeaders(Workbook workbook) {
    var headers = new ArrayList<Header>();

    for (Sheet sheet : workbook) {
      if (hasText(sheet.getHeader())) {
        headers.add(sheet.getHeader());
      }
    }

    return headers;
  }

  public static List<Footer> collectFooters(Workbook workbook) {
    var footers = new ArrayList<Footer>();

    for (Sheet sheet : workbook) {
      if (hasText(sheet.getFooter())) {
        footers.add(sheet.getFooter());
      }
    }

    return footers;
  }

  public static List<XSSFSimpleShape> collectTextShapes(Workbook workbook) {

    if (!(workbook instanceof XSSFWorkbook)) {
      throw new UnsupportedOperationException("Only XSSFWorkbook (.xlsx) is supported.");
    }

    var shapes = new ArrayList<XSSFSimpleShape>();

    for (Sheet sheet : workbook) {

      var drawing = ((XSSFSheet) sheet).getDrawingPatriarch();

      if (drawing != null) {
        collectExcelTextShapes(drawing.getShapes(), shapes);
      }
    }

    return shapes;
  }

  private static void collectExcelTextShapes(
      List<XSSFShape> shapes, List<XSSFSimpleShape> collectors) {

    for (var shape : shapes) {

      switch (shape) {
        case XSSFShapeGroup group -> {
          var children = new ArrayList<XSSFShape>();
          group.iterator().forEachRemaining(children::add);

          collectExcelTextShapes(children, collectors);
        }

        case XSSFSimpleShape simpleShape -> {
          var text = simpleShape.getText();

          if (text != null && !text.isBlank()) {
            collectors.add(simpleShape);
          }
        }

        default -> {}
      }
    }
  }

  // ============================================================
  // PowerPoint
  // ============================================================

  public static List<XSLFTextShape> collectTextShapes(XMLSlideShow document) {

    var shapes = new ArrayList<XSLFTextShape>();

    for (var slide : document.getSlides()) {

      collectPowerPointTextShapes(slide.getShapes(), shapes);

      var notes = slide.getNotes();

      if (notes != null) {
        collectPowerPointTextShapes(notes.getShapes(), shapes);
      }
    }

    for (var master : document.getSlideMasters()) {

      collectPowerPointTextShapes(master.getShapes(), shapes);

      for (var layout : master.getSlideLayouts()) {
        collectPowerPointTextShapes(layout.getShapes(), shapes);
      }
    }

    return shapes;
  }

  public static List<XSLFTableCell> collectTableCells(XMLSlideShow document) {

    var cells = new ArrayList<XSLFTableCell>();

    for (var slide : document.getSlides()) {

      collectPowerPointTableCells(slide.getShapes(), cells);

      var notes = slide.getNotes();

      if (notes != null) {
        collectPowerPointTableCells(notes.getShapes(), cells);
      }
    }

    for (var master : document.getSlideMasters()) {

      collectPowerPointTableCells(master.getShapes(), cells);

      for (var layout : master.getSlideLayouts()) {
        collectPowerPointTableCells(layout.getShapes(), cells);
      }
    }

    return cells;
  }

  private static void collectPowerPointTextShapes(
      List<XSLFShape> shapes, List<XSLFTextShape> collectors) {

    for (var shape : shapes) {

      switch (shape) {
        case XSLFGroupShape group -> collectPowerPointTextShapes(group.getShapes(), collectors);

        case XSLFTextShape textShape -> {
          if (!textShape.getText().isBlank()) {
            collectors.add(textShape);
          }
        }

        default -> {}
      }
    }
  }

  private static void collectPowerPointTableCells(
      List<XSLFShape> shapes, List<XSLFTableCell> collectors) {

    for (var shape : shapes) {

      switch (shape) {
        case XSLFGroupShape group -> collectPowerPointTableCells(group.getShapes(), collectors);

        case XSLFTable table -> {
          for (var row : table.getRows()) {
            collectors.addAll(row.getCells());
          }
        }

        default -> {}
      }
    }
  }

  // ============================================================
  // Common
  // ============================================================

  private static boolean hasText(HeaderFooter headerFooter) {
    return hasText(headerFooter.getLeft())
        || hasText(headerFooter.getCenter())
        || hasText(headerFooter.getRight());
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
