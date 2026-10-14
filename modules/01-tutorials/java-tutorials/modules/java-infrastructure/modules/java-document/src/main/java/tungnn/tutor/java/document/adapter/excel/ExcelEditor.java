package tungnn.tutor.java.document.adapter.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

public final class ExcelEditor {

  private ExcelEditor() {}

  public static Row cloneRow(Sheet sheet, int rowIndex, int templateRowIndex) {
    var sourceRow = sheet.getRow(templateRowIndex);
    if (sourceRow == null) {
      throw new IllegalArgumentException("Template row " + templateRowIndex + " does not exist");
    }

    var targetRow = sheet.getRow(rowIndex);
    if (targetRow != null) {
      sheet.removeRow(targetRow);
    }
    targetRow = sheet.createRow(rowIndex);
    targetRow.setHeight(sourceRow.getHeight());

    short last = sourceRow.getLastCellNum();
    if (last >= 0) {
      for (var i = 0; i < last; i++) {
        var sourceCell = sourceRow.getCell(i);
        if (sourceCell != null) {
          var targetCell = targetRow.createCell(i);
          copyCell(sourceCell, targetCell, true);
        }
      }
    }

    copyMergedRegions(sheet, sourceRow.getRowNum(), targetRow.getRowNum());
    return targetRow;
  }

  public static void cloneRows(Sheet sheet, int srcRowStart, int srcRowEnd, int dstRowStart) {
    if (srcRowStart > srcRowEnd) {
      return;
    }

    int rowOffset = dstRowStart - srcRowStart;
    for (int currentSrc = srcRowStart; currentSrc <= srcRowEnd; currentSrc++) {
      int currentDst = currentSrc + rowOffset;
      cloneRow(sheet, currentDst, currentSrc);
    }
  }

  public static void cloneRows(
      Sheet srcSheet, int srcFrom, int srcTo, Sheet dstSheet, int dstFrom) {

    if (srcSheet == null || dstSheet == null) {
      throw new IllegalArgumentException("Source sheet and destination sheet must not be null");
    }
    if (srcFrom > srcTo) {
      throw new IllegalArgumentException("Source 'from' row cannot be greater than 'to' row");
    }

    Row lastClonedRow = null;
    int rowCount = srcTo - srcFrom + 1;

    if (srcSheet == dstSheet && dstFrom > srcFrom) {
      dstSheet.shiftRows(dstFrom, dstSheet.getLastRowNum(), rowCount);
    }

    for (int i = 0; i < rowCount; i++) {
      int srcRowNum = srcFrom + i;
      int dstRowNum = dstFrom + i;

      Row srcRow = srcSheet.getRow(srcRowNum);
      Row dstRow = dstSheet.getRow(dstRowNum);

      if (dstRow != null) {
        dstSheet.removeRow(dstRow);
      }
      dstRow = dstSheet.createRow(dstRowNum);
      lastClonedRow = dstRow;

      if (srcRow == null) {
        continue;
      }

      dstRow.setHeight(srcRow.getHeight());

      short lastCellNum = srcRow.getLastCellNum();
      if (lastCellNum >= 0) {
        for (int j = 0; j < lastCellNum; j++) {
          Cell srcCell = srcRow.getCell(j);
          if (srcCell != null) {
            Cell dstCell = dstRow.createCell(j);
            copyCell(srcCell, dstCell, true);
          }
        }
      }
    }

    for (CellRangeAddress mergedRegion : srcSheet.getMergedRegions()) {
      if (mergedRegion.getFirstRow() >= srcFrom && mergedRegion.getLastRow() <= srcTo) {
        int rowOffset = dstFrom - srcFrom;
        CellRangeAddress newRegion =
            new CellRangeAddress(
                mergedRegion.getFirstRow() + rowOffset,
                mergedRegion.getLastRow() + rowOffset,
                mergedRegion.getFirstColumn(),
                mergedRegion.getLastColumn());
        dstSheet.addMergedRegion(newRegion);
      }
    }
  }

  public static Row insertRow(Sheet sheet, int templateRowIndex) {
    int lastRow = sheet.getLastRowNum();
    int targetRowIndex = templateRowIndex + 1;

    if (targetRowIndex <= lastRow) {
      ExcelUtil.shiftRows(sheet, targetRowIndex, lastRow, 1);
    }

    var sourceRow = sheet.getRow(templateRowIndex);
    var targetRow = sheet.getRow(targetRowIndex);
    if (targetRow != null) {
      sheet.removeRow(targetRow);
    }
    targetRow = sheet.createRow(targetRowIndex);
    targetRow.setHeight(sourceRow.getHeight());

    short last = sourceRow.getLastCellNum();
    if (last >= 0) {
      for (var i = 0; i < last; i++) {
        var sourceCell = sourceRow.getCell(i);
        if (sourceCell != null) {
          var targetCell = targetRow.createCell(i);
          copyCell(sourceCell, targetCell, false);
        }
      }
    }

    copyMergedRegions(sheet, sourceRow.getRowNum(), targetRow.getRowNum());
    return targetRow;
  }

  public static Cell getOrCreateCell(Sheet sheet, int rowNum, int colNum) {
    Row row = sheet.getRow(rowNum);
    if (row == null) {
      row = sheet.createRow(rowNum);
    }

    Cell cell = row.getCell(colNum);
    if (cell == null) {
      cell = row.createCell(colNum);
    }

    return cell;
  }

  public static void copyCell(Cell sourceCell, Cell targetCell, boolean copyValue) {
    targetCell.setCellStyle(sourceCell.getCellStyle());

    if (sourceCell.getHyperlink() != null) {
      ExcelUtil.addHyperlinkToCell(
          targetCell.getSheet().getWorkbook(),
          targetCell,
          sourceCell.getHyperlink().getAddress(),
          sourceCell.getHyperlink().getType());
    }

    if (sourceCell.getCellComment() != null) {
      var srcComment = sourceCell.getCellComment();
      ExcelUtil.setCellComment(
          targetCell.getSheet().getWorkbook(),
          targetCell.getSheet(),
          targetCell,
          srcComment.getAuthor(),
          srcComment.getString().getString());
    }

    if (!copyValue) {
      targetCell.setBlank();
      return;
    }

    switch (sourceCell.getCellType()) {
      case BOOLEAN -> targetCell.setCellValue(sourceCell.getBooleanCellValue());
      case ERROR -> targetCell.setCellErrorValue(sourceCell.getErrorCellValue());
      case FORMULA -> targetCell.setCellFormula(sourceCell.getCellFormula());
      case NUMERIC -> targetCell.setCellValue(sourceCell.getNumericCellValue());
      case STRING -> targetCell.setCellValue(sourceCell.getRichStringCellValue());
      case BLANK -> targetCell.setBlank();
      default -> {}
    }
  }

  public static void copyMergedRegions(Sheet sheet, int sourceRowNum, int targetRowNum) {
    int numRegions = sheet.getNumMergedRegions();
    for (int i = 0; i < numRegions; i++) {
      CellRangeAddress region = sheet.getMergedRegion(i);

      if (region.getFirstRow() == sourceRowNum) {
        int rowSpan = region.getLastRow() - region.getFirstRow();
        ExcelUtil.mergeCells(
            sheet,
            targetRowNum,
            targetRowNum + rowSpan,
            region.getFirstColumn(),
            region.getLastColumn());
      }
    }
  }

  public static void clearRowContent(Row row) {
    if (row == null) return;
    short firstCell = row.getFirstCellNum();
    short lastCell = row.getLastCellNum();
    if (firstCell < 0) return;

    for (int i = firstCell; i < lastCell; i++) {
      Cell cell = row.getCell(i);
      if (cell != null) {
        clearCellContent(cell);
      }
    }
  }

  public static void clearCellContent(Cell cell) {
    if (cell == null) return;
    if (cell.getCellType() == CellType.FORMULA) {
      cell.setCellFormula(null);
    }
    cell.setBlank();
    if (cell.getHyperlink() != null) {
      cell.removeHyperlink();
    }
  }

  public static void emptyCell(Cell cell) {
    if (cell == null) return;
    clearCellContent(cell);
    if (cell.getCellComment() != null) {
      cell.removeCellComment();
    }
    cell.setCellStyle(null);
  }
}
