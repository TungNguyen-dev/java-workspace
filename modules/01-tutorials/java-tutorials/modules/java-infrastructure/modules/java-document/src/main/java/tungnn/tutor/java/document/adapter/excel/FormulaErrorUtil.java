package tungnn.tutor.java.document.adapter.excel;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

/**
 * Static utility to investigate Excel formula errors.
 *
 * <p>Two kinds of formula errors are covered:
 *
 * <ul>
 *   <li><b>STRUCTURAL</b> - an error token embedded in the formula text (e.g. {@code =SUM(#REF!)}).
 *   <li><b>EVALUATED</b> - a valid formula whose cached result is an error (e.g. {@code =A1/0 ->
 *       #DIV/0!}).
 * </ul>
 *
 * All error kinds are matched via {@link FormulaError}, so no error string is hard-coded.
 */
public final class FormulaErrorUtil {

  /**
   * Every Excel error literal ("#REF!", "#DIV/0!", ...) derived from FormulaError, deduplicated.
   */
  private static final List<String> ERROR_TOKENS =
      Arrays.stream(FormulaError.values()).map(FormulaError::getString).distinct().toList();

  private FormulaErrorUtil() {}

  /**
   * Scans an Excel file and returns every formula cell that is (or evaluates to) an error.
   *
   * @param excelFilePath Path to the Excel file (.xlsx or .xls)
   * @return List of error details across all sheets
   * @throws IOException if the workbook cannot be read
   */
  public static List<FormulaErrorDetail> findErrors(Path excelFilePath) throws IOException {
    try (Workbook workbook = ExcelUtil.readWorkbook(excelFilePath)) {
      return findErrors(workbook);
    }
  }

  /** Scans an already-open workbook for formula errors across all sheets. */
  public static List<FormulaErrorDetail> findErrors(Workbook workbook) {
    List<FormulaErrorDetail> errors = new ArrayList<>();
    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
      errors.addAll(findErrors(workbook.getSheetAt(i)));
    }
    return errors;
  }

  // ---------------------------------------------------------------------------
  // Workbook / sheet level investigation
  // ---------------------------------------------------------------------------

  /** Scans a single sheet for formula errors. */
  public static List<FormulaErrorDetail> findErrors(Sheet sheet) {
    List<FormulaErrorDetail> errors = new ArrayList<>();
    String sheetName = sheet.getSheetName();
    ExcelUtil.iterateOverRowsAndCells(
        sheet, cell -> inspect(sheetName, cell).ifPresent(errors::add));
    return errors;
  }

  /**
   * Investigates a single cell and, if it is a formula in error, returns the detail.
   *
   * @param sheetName name used to label the result
   * @param cell any cell (non-formula cells are ignored)
   * @return an {@link Optional} error detail, empty when the cell has no formula error
   */
  public static Optional<FormulaErrorDetail> inspect(String sheetName, Cell cell) {
    if (cell == null || cell.getCellType() != CellType.FORMULA) {
      return Optional.empty();
    }

    String formula = cell.getCellFormula();
    String cellAddress = new CellReference(cell).formatAsString();

    // 1) EVALUATED error: the cached result of the formula is an error value.
    if (cell.getCachedFormulaResultType() == CellType.ERROR) {
      String errorType = FormulaError.forInt(cell.getErrorCellValue()).getString();
      return Optional.of(
          new FormulaErrorDetail(
              sheetName, cellAddress, errorType, ErrorSource.EVALUATED, formula));
    }

    // 2) STRUCTURAL error: an error token is embedded in the formula expression itself.
    return firstErrorToken(formula)
        .map(
            token ->
                new FormulaErrorDetail(
                    sheetName, cellAddress, token, ErrorSource.STRUCTURAL, formula));
  }

  /** {@code true} if the cell is a formula that is (or evaluates to) an error. */
  public static boolean isFormulaError(Cell cell) {
    return inspect("", cell).isPresent();
  }

  // ---------------------------------------------------------------------------
  // Cell level investigation (reusable building blocks)
  // ---------------------------------------------------------------------------

  /** Returns the first Excel error token embedded in a formula's text, if any. */
  public static Optional<String> firstErrorToken(String formula) {
    if (formula == null || formula.isBlank()) {
      return Optional.empty();
    }
    String upper = formula.toUpperCase();
    return ERROR_TOKENS.stream().filter(token -> upper.contains(token.toUpperCase())).findFirst();
  }

  /** Aggregates a list of errors into a count per error type (e.g. {@code #REF! -> 3}). */
  public static Map<String, Long> countByType(List<FormulaErrorDetail> errors) {
    return errors.stream()
        .collect(Collectors.groupingBy(FormulaErrorDetail::errorType, Collectors.counting()));
  }

  /** Aggregates a list of errors into a count per detection source. */
  public static Map<ErrorSource, Long> countBySource(List<FormulaErrorDetail> errors) {
    return errors.stream()
        .collect(Collectors.groupingBy(FormulaErrorDetail::source, Collectors.counting()));
  }

  // ---------------------------------------------------------------------------
  // Reporting helpers
  // ---------------------------------------------------------------------------

  /** Where the error was detected. */
  public enum ErrorSource {
    STRUCTURAL, // error token inside the formula expression
    EVALUATED // formula is valid but its cached result is an error
  }

  /** Immutable description of a single formula error occurrence. */
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
