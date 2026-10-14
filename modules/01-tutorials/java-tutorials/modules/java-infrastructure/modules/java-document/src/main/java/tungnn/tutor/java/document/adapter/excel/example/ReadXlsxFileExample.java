package tungnn.tutor.java.document.adapter.excel.example;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadXlsxFileExample {

  static void main() throws IOException {
    try (InputStream in = Files.newInputStream(Path.of("storage", "data-input.xlsx"));
        Workbook workbook = new XSSFWorkbook(in)) {

      Sheet sheet = workbook.getSheetAt(0);
      DataFormatter formatter = new DataFormatter();

      for (Row row : sheet) {
        for (Cell cell : row) {
          String value = formatter.formatCellValue(cell);
          System.out.print(value + "\t");
        }
        System.out.println();
      }
    }
  }
}
