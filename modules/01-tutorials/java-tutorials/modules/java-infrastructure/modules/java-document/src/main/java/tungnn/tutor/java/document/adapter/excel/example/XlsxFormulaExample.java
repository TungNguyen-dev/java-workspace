package tungnn.tutor.java.document.adapter.excel.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsxFormulaExample {

  static void main() throws IOException {
    Path path = Path.of("storage", "formular.xlsx");

    try (Workbook workbook = new XSSFWorkbook();
        OutputStream out = Files.newOutputStream(path)) {

      Sheet sheet = workbook.createSheet("formular");
      Row row = sheet.createRow(0);
      Cell cell1 = row.createCell(0);
      Cell cell2 = row.createCell(1);
      Cell cell3 = row.createCell(2);

      cell1.setCellValue(1);
      cell2.setCellValue(2);

      // setCellFormula(String)
      cell3.setCellFormula("A1 + A2");

      workbook.write(out);
    }

    try (InputStream in = Files.newInputStream(path);
        Workbook workbook = new XSSFWorkbook(in)) {
      Sheet sheet = workbook.getSheetAt(0);
      Row row = sheet.getRow(0);
      Cell cell = row.getCell(2);

      // getCellFormula(String)
      String formula = cell.getCellFormula();
      System.out.println(formula);

      // FormulaEvaluator
      FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
      CellValue cellValue = evaluator.evaluate(cell);
      System.out.println(cellValue);
    }
  }
}
