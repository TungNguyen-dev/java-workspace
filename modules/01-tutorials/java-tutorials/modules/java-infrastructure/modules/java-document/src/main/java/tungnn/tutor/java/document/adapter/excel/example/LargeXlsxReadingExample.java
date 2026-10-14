package tungnn.tutor.java.document.adapter.excel.example;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class LargeXlsxReadingExample {

  static void main() throws IOException {
    Path path = Path.of("storage", "largeXlsxReading.xlsx");
    Files.createFile(path);

    // Auto-Flushing with Window Size = 100
    try (SXSSFWorkbook wb = new SXSSFWorkbook(100);
        OutputStream out = Files.newOutputStream(path); ) {
      Sheet sh = wb.createSheet();

      for (int rowNum = 0; rowNum < 1000; rowNum++) {
        Row row = sh.createRow(rowNum);
        for (int cellNum = 0; cellNum < 100; cellNum++) {
          Cell cell = row.createCell(cellNum);
          cell.setCellValue(cellNum);
        }
      }

      wb.write(out);
    }
  }

  public static void main2() throws IOException {
    Path path = Path.of("storage", "largeXlsxReading.xlsx");
    Files.createFile(path);

    // Manual-Flushing -> rowAccessWindowSize = -1
    try (SXSSFWorkbook wb = new SXSSFWorkbook(-1);
        OutputStream out = Files.newOutputStream(path); ) {
      SXSSFSheet sh = wb.createSheet();

      for (int rowNum = 0; rowNum < 1000; rowNum++) {
        Row row = sh.createRow(rowNum);
        for (int cellNum = 0; cellNum < 100; cellNum++) {
          Cell cell = row.createCell(cellNum);
          cell.setCellValue(cellNum);
        }
        // Manual flushing
        if (rowNum % 100 == 0) {
          sh.flushRows();
        }
      }

      wb.write(out);
    }
  }
}
