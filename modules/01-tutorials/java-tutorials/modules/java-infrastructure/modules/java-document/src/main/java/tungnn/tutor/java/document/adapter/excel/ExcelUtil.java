package tungnn.tutor.java.document.adapter.excel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.function.Consumer;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.*;
import org.apache.poi.xssf.usermodel.*;

public class ExcelUtil {

  private ExcelUtil() {}

  // ==========================================
  // 1. WORKBOOK & SHEET MANAGEMENT
  // ==========================================

  public static Workbook createNewWorkbook() {
    return new XSSFWorkbook();
  }

  public static Workbook readWorkbook(Path path) throws IOException {
    try (var is = Files.newInputStream(path)) {
      return WorkbookFactory.create(is);
    }
  }

  public static Workbook readWorkbook(InputStream inputStream) throws IOException {
    return WorkbookFactory.create(inputStream);
  }

  public static void writeWorkbook(Workbook workbook, Path targetPath) throws IOException {
    try (var os = Files.newOutputStream(targetPath)) {
      workbook.write(os);
    }
  }

  public static Sheet createSheet(Workbook workbook, String sheetName) {
    return workbook.createSheet(sheetName);
  }

  public static Sheet cloneSheet(Workbook workbook, int sheetIndex, String newName) {
    Sheet clonedSheet = workbook.cloneSheet(sheetIndex);
    if (newName != null && !newName.isBlank()) {
      int clonedIndex = workbook.getSheetIndex(clonedSheet);
      workbook.setSheetName(clonedIndex, newName);
    }
    return clonedSheet;
  }

  public static Sheet cloneSheet(Workbook workbook, String templateName, String sheetName) {
    int sheetIndex = workbook.getSheetIndex(templateName);
    if (sheetIndex == -1) {
      throw new IllegalArgumentException("Source sheet '" + templateName + "' does not exist.");
    }
    return cloneSheet(workbook, sheetIndex, sheetName);
  }

  public static void setSheetAsSelected(Workbook workbook, int index) {
    workbook.setSelectedTab(index);
  }

  public static void setZoomMagnification(Sheet sheet, int scale) {
    sheet.setZoom(scale); // 100 means 100%
  }

  // ==========================================
  // 2. TEXT EXTRACTION
  // ==========================================

  public static String extractAllText(Workbook workbook) {
    throw new UnsupportedOperationException("Use TextExtractorUtil#extractext() instead.");
  }

  // ==========================================
  // 3. CELL & ROW OPERATIONS & DATA HANDLING
  // ==========================================
  public static Cell createCell(Row row, int colIndex, Object value) {
    return createCell(row, colIndex, value, null);
  }

  public static Cell createCell(Row row, int colIndex, Object value, CellStyle style) {
    var cell = row.createCell(colIndex);
    if (style != null) {
      cell.setCellStyle(style);
    }

    switch (value) {
      case String s -> cell.setCellValue(s);
      case Number n -> cell.setCellValue(n.doubleValue());
      case Boolean b -> cell.setCellValue(b);
      case LocalDate ld -> cell.setCellValue(ld);
      case null -> throw new IllegalArgumentException("Cell value cannot be null");
      default ->
          throw new IllegalArgumentException(
              "Unsupported data type: " + value.getClass().getName());
    }

    return cell;
  }

  public static void shiftRows(Sheet sheet, int startRow, int endRow, int n) {
    sheet.shiftRows(startRow, endRow, n);
  }

  public static void setRowHidden(Sheet sheet, int rowIndex, boolean hidden) {
    var row = sheet.getRow(rowIndex);
    if (row != null) {
      row.setZeroHeight(hidden);
    }
  }

  public static void iterateOverRowsAndCells(Sheet sheet, Consumer<Cell> cellProcessor) {
    for (var row : sheet) {
      for (var cell : row) {
        cellProcessor.accept(cell);
      }
    }
  }

  public static Object getCellContent(Cell cell) {
    if (cell == null) return null;
    return switch (cell.getCellType()) {
      case STRING -> cell.getStringCellValue();
      case NUMERIC ->
          DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : cell.getNumericCellValue();
      case BOOLEAN -> cell.getBooleanCellValue();
      case FORMULA -> cell.getCellFormula();
      default -> "";
    };
  }

  public static void createNamedRange(Workbook workbook, String name, String formula) {
    var namedRange = workbook.createName();
    namedRange.setNameName(name);
    namedRange.setRefersToFormula(formula); // e.g. "Sheet1!$A$1:$C$10"
  }

  public static void applyAutoFilter(Sheet sheet, String rangeStr) {
    sheet.setAutoFilter(CellRangeAddress.valueOf(rangeStr));
  }

  public static void createListValidation(
      Sheet sheet, String[] explicitValues, int firstRow, int lastRow, int col) {
    var validationHelper = sheet.getDataValidationHelper();
    var addressList = new CellRangeAddressList(firstRow, lastRow, col, col);
    var constraint = validationHelper.createExplicitListConstraint(explicitValues);
    var validation = validationHelper.createValidation(constraint, addressList);
    validation.setShowErrorBox(true);
    sheet.addValidationData(validation);
  }

  // ==========================================
  // 4. STYLING & FORMATTING
  // ==========================================

  public static CellStyle createCustomStyle(
      Workbook workbook, Font font, HorizontalAlignment align, IndexedColors background) {
    var style = workbook.createCellStyle();
    if (font != null) style.setFont(font);
    if (align != null) style.setAlignment(align);
    if (background != null) {
      style.setFillForegroundColor(background.getIndex());
      style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }
    return style;
  }

  public static Font createFont(
      Workbook workbook, String fontName, short size, boolean isBold, IndexedColors color) {
    var font = workbook.createFont();
    font.setFontName(fontName);
    font.setFontHeightInPoints(size);
    font.setBold(isBold);
    if (color != null) {
      font.setColor(color.getIndex());
    }
    return font;
  }

  public static void setCustomRgbColor(
      Workbook workbook, CellStyle style, java.awt.Color javaColor) {
    if (style instanceof XSSFCellStyle xssfStyle && workbook instanceof XSSFWorkbook) {
      var xssfColor = new XSSFColor(javaColor, new DefaultIndexedColorMap());
      xssfStyle.setFillForegroundColor(xssfColor);
      xssfStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    } else {
      throw new UnsupportedOperationException(
          "Custom RGB color feature only supports XSSF format (.xlsx)");
    }
  }

  public static void drawBordersToArea(Sheet sheet, CellRangeAddress range) {
    var pt = new PropertyTemplate();
    pt.drawBorders(range, BorderStyle.THIN, BorderExtent.ALL);
    pt.applyBorders(sheet);
  }

  public static void setUserDefinedDataFormat(
      Workbook workbook, CellStyle style, String formatStr) {
    var format = workbook.createDataFormat();
    style.setDataFormat(format.getFormat(formatStr)); // e.g. "#,##0.00"
  }

  public static void enableNewlinesInCell(CellStyle style) {
    style.setWrapText(true);
  }

  // ==========================================
  // 5. LAYOUT & ORGANIZATION
  // ==========================================

  public static void adjustColumnWidthToFit(Sheet sheet, int colIndex) {
    sheet.autoSizeColumn(colIndex);
  }

  public static void mergeCells(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
    sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
  }

  public static void createFreezePane(Sheet sheet, int colSplit, int rowSplit) {
    sheet.createFreezePane(colSplit, rowSplit);
  }

  public static void setOutliningGroup(Sheet sheet, int start, int end, boolean isRow) {
    if (isRow) {
      sheet.groupRow(start, end);
    } else {
      sheet.groupColumn(start, end);
    }
  }

  // ==========================================
  // 6. GRAPHICS, SHAPES & OBJECTS
  // ==========================================

  public static void insertImage(
      Workbook workbook, Sheet sheet, byte[] imageBytes, int pictureType, int row, int col) {
    var pictureIdx = workbook.addPicture(imageBytes, pictureType);
    var drawing = sheet.createDrawingPatriarch();
    var anchor = workbook.getCreationHelper().createClientAnchor();
    anchor.setCol1(col);
    anchor.setRow1(row);
    var pic = drawing.createPicture(anchor, pictureIdx);
    pic.resize();
  }

  public static void drawSimpleShape(
      Sheet sheet, int row1, int col1, int row2, int col2, int shapeType) {
    if (sheet instanceof XSSFSheet xssfSheet) {
      var drawing = xssfSheet.createDrawingPatriarch();
      var anchor = new XSSFClientAnchor(0, 0, 0, 0, col1, row1, col2, row2);
      var shape = drawing.createSimpleShape(anchor);
      shape.setShapeType(shapeType);
      shape.setFillColor(230, 240, 250);
      shape.setLineStyleColor(0, 0, 0);
    } else {
      throw new UnsupportedOperationException(
          "Advanced shape drawing is only optimized for XSSF (.xlsx)");
    }
  }

  public static void drawAdvancedGraphics2D(
      Sheet sheet, Consumer<java.awt.Graphics2D> customPainter) {
    if (sheet instanceof XSSFSheet xssfSheet) {
      var drawing = xssfSheet.createDrawingPatriarch();
      var anchor = new XSSFClientAnchor(0, 0, 0, 0, 1, 1, 10, 25);
      var group = drawing.createGroup(anchor);
    } else {
      throw new UnsupportedOperationException(
          "Graphics2D environment requires XSSF components (.xlsx)");
    }
  }

  // ==========================================
  // 7. ADVANCED INTERACTIVITY
  // ==========================================

  public static void setCellComment(
      Workbook workbook, Sheet sheet, Cell cell, String author, String commentText) {
    var drawing = sheet.createDrawingPatriarch();
    var anchor = workbook.getCreationHelper().createClientAnchor();
    anchor.setCol1(cell.getColumnIndex() + 1);
    anchor.setRow1(cell.getRowIndex());

    var comment = drawing.createCellComment(anchor);
    comment.setString(workbook.getCreationHelper().createRichTextString(commentText));
    comment.setAuthor(author);
    cell.setCellComment(comment);
  }

  public static void addHyperlinkToCell(
      Workbook workbook, Cell cell, String address, HyperlinkType type) {
    var helper = workbook.getCreationHelper();
    var link = helper.createHyperlink(type);
    link.setAddress(address);
    cell.setHyperlink(link);
  }

  public static void applyConditionalFormatting(Sheet sheet, String rangeStr) {
    var sheetCF = sheet.getSheetConditionalFormatting();
    var rule = sheetCF.createConditionalFormattingRule(ComparisonOperator.GT, "100");
    var fill = rule.createPatternFormatting();
    fill.setFillBackgroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
    fill.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

    CellRangeAddress[] regions = {CellRangeAddress.valueOf(rangeStr)};
    sheetCF.addConditionalFormatting(regions, rule);
  }

  public static XSSFPivotTable createPivotTable(
      Sheet dataSheet, Sheet pivotSheet, String dataRange, String targetStartCell) {

    if (dataSheet instanceof XSSFSheet xssfData && pivotSheet instanceof XSSFSheet xssfPivot) {
      var sourceArea = new AreaReference(dataRange, org.apache.poi.ss.SpreadsheetVersion.EXCEL2007);
      var targetPos = new CellReference(targetStartCell);

      return xssfPivot.createPivotTable(sourceArea, targetPos, xssfData);
    } else {
      throw new UnsupportedOperationException("Pivot Table must be run on XSSF format (.xlsx)");
    }
  }

  // ==========================================
  // 8. PRINTING & PAGE SETUP
  // ==========================================

  public static void configurePrintSetup(
      Workbook workbook, Sheet sheet, boolean fitToPage, String printArea) {
    var printSetup = sheet.getPrintSetup();
    if (fitToPage) {
      sheet.setAutobreaks(true);
      printSetup.setFitWidth((short) 1);
      printSetup.setFitHeight((short) 1);
    }
    if (printArea != null) {
      var sheetIndex = workbook.getSheetIndex(sheet);
      workbook.setPrintArea(sheetIndex, printArea); // e.g. "A1:F50"
    }
  }

  public static void setRepeatingRowsAndColumns(Sheet sheet, String colRange, String rowRange) {
    if (colRange != null) {
      sheet.setRepeatingColumns(CellRangeAddress.valueOf(colRange)); // e.g. "A:B"
    }
    if (rowRange != null) {
      sheet.setRepeatingRows(CellRangeAddress.valueOf(rowRange)); // e.g. "1:2"
    }
  }

  public static void setupHeadersAndFooters(
      Sheet sheet, String leftHeader, boolean enableXSSFVersionEnhancement) {
    var header = sheet.getHeader();
    header.setLeft(leftHeader);

    var footer = sheet.getFooter();
    footer.setRight("Trang " + HeaderFooter.page() + " / " + HeaderFooter.numPages());

    if (enableXSSFVersionEnhancement && sheet instanceof XSSFSheet xssfSheet) {
      xssfSheet.getOddHeader().setCenter("Odd Page Header");
      xssfSheet.getEvenHeader().setCenter("Even Page Header");
    }
  }
}
