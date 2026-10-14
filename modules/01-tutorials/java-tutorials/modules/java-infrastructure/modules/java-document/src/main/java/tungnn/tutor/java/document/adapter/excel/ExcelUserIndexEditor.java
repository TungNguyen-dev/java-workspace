package tungnn.tutor.java.document.adapter.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;

public final class ExcelUserIndexEditor {

  private ExcelUserIndexEditor() {}

  // ==========================================
  // THAO TÁC THEO SỐ (1-BASED INDEX)
  // ==========================================

  public static Row cloneRow(Sheet sheet, int rowNum, int templateRowNum) {
    return ExcelEditor.cloneRow(sheet, rowNum - 1, templateRowNum - 1);
  }

  public static void cloneRows(Sheet sheet, int srcRowStart, int srcRowEnd, int dstRowStart) {
    ExcelEditor.cloneRows(sheet, srcRowStart - 1, srcRowEnd - 1, dstRowStart - 1);
  }

  public static void cloneRows(
      Sheet srcSheet, int srcFrom, int srcTo, Sheet dstSheet, int dstFrom) {
    ExcelEditor.cloneRows(srcSheet, srcFrom - 1, srcTo - 1, dstSheet, dstFrom - 1);
  }

  public static Row insertRow(Sheet sheet, int templateRowNum) {
    return ExcelEditor.insertRow(sheet, templateRowNum - 1);
  }

  public static Cell getOrCreateCell(Sheet sheet, int rowNum, int colNum) {
    return ExcelEditor.getOrCreateCell(sheet, rowNum - 1, colNum - 1);
  }

  // =========================================================================
  // CELL REFERENCE METHODS (Get/Set by "A1", "B5")
  // =========================================================================

  public static Cell getOrCreateCellByAddress(Sheet sheet, String address) {
    var ref = new CellReference(address);
    return ExcelEditor.getOrCreateCell(sheet, ref.getRow(), ref.getCol());
  }

  public static void setCellValueByAddress(Sheet sheet, String address, Object value) {
    var cell = getOrCreateCellByAddress(sheet, address);
    cell.setCellValue(value.toString());
  }

  public static Object getCellValueByAddress(Sheet sheet, String address) {
    CellReference ref = new CellReference(address);
    Row row = sheet.getRow(ref.getRow());
    if (row == null) return null;
    Cell cell = row.getCell(ref.getCol());
    return ExcelUtil.getCellContent(cell);
  }
}
