package tungnn.tutor.java.infra.document.adapter.office.excel;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

public final class ExcelFormulaErrorUtils {
  private static final List<String> ERROR_TOKENS =
      Arrays.stream(FormulaError.values()).map(FormulaError::getString).distinct().toList();

  private ExcelFormulaErrorUtils() {}

  public static List<FormulaErrorDetail> findErrors(Path excelFilePath) throws IOException {
    try (Workbook workbook = ExcelDocumentUtils.readWorkbook(excelFilePath)) {
      return findErrors(workbook);
    }
  }

  public static List<FormulaErrorDetail> findErrors(Workbook workbook) {
    List<FormulaErrorDetail> errors = new ArrayList<>();
    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
      errors.addAll(findErrors(workbook.getSheetAt(i)));
    }
    return errors;
  }

  public static List<FormulaErrorDetail> findErrors(Sheet sheet) {
    List<FormulaErrorDetail> errors = new ArrayList<>();
    String sheetName = sheet.getSheetName();
    ExcelDocumentUtils.iterateOverRowsAndCells(
        sheet, cell -> inspect(sheetName, cell).ifPresent(errors::add));
    return errors;
  }

  public static Optional<FormulaErrorDetail> inspect(String sheetName, Cell cell) {
    if (cell == null || cell.getCellType() != CellType.FORMULA) {
      return Optional.empty();
    }
    String formula = cell.getCellFormula();
    String cellAddress = new CellReference(cell).formatAsString();
    if (cell.getCachedFormulaResultType() == CellType.ERROR) {
      String errorType = FormulaError.forInt(cell.getErrorCellValue()).getString();
      return Optional.of(
          new FormulaErrorDetail(
              sheetName, cellAddress, errorType, ErrorSource.EVALUATED, formula));
    }
    return firstErrorToken(formula)
        .map(token ->
            new FormulaErrorDetail(sheetName, cellAddress, token, ErrorSource.STRUCTURAL, formula));
  }

  public static boolean isFormulaError(Cell cell) {
    return inspect("", cell).isPresent();
  }

  public static Optional<String> firstErrorToken(String formula) {
    if (formula == null || formula.isBlank()) {
      return Optional.empty();
    }
    String upper = formula.toUpperCase();
    return ERROR_TOKENS.stream().filter(token -> upper.contains(token.toUpperCase())).findFirst();
  }

  public static Map<String, Long> countByType(List<FormulaErrorDetail> errors) {
    return errors.stream()
        .collect(Collectors.groupingBy(FormulaErrorDetail::errorType, Collectors.counting()));
  }

  public static Map<ErrorSource, Long> countBySource(List<FormulaErrorDetail> errors) {
    return errors.stream()
        .collect(Collectors.groupingBy(FormulaErrorDetail::source, Collectors.counting()));
  }

  public enum ErrorSource {
    STRUCTURAL,
    EVALUATED
  }

  public record FormulaErrorDetail(
      String sheetName, String cellAddress, String errorType, ErrorSource source, String formula) {
    @Override
    public String toString() {
      return String.format(
          "Sheet: %-15s | Cell: %-5s | Error: %-9s | Source: %-10s | Formula: %s",
          "'" + sheetName + "'", cellAddress, errorType, source, formula);
    }
  }
}
