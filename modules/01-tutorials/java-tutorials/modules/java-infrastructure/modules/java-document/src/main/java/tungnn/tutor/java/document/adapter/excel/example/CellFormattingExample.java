package tungnn.tutor.java.document.adapter.excel.example;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CellFormattingExample {

  static void main() throws IOException {
    Path path = Path.of("storage", "formatting-cells.xlsx");

    try (Workbook workbook = new XSSFWorkbook();
        OutputStream outputStream = Files.newOutputStream(path)) {

      Sheet sheet = workbook.createSheet("formatting");
      Row headerRow = sheet.createRow(0);
      Cell headerCell = headerRow.createCell(0);

      headerCell.setCellValue("Sample Text");

      // Create and configure cell style
      CellStyle headerStyle = createHeaderStyle(workbook);
      headerCell.setCellStyle(headerStyle);

      // Adjust column width once
      sheet.autoSizeColumn(0);

      workbook.write(outputStream);
    }
  }

  private static CellStyle createHeaderStyle(Workbook workbook) {
    Font font = workbook.createFont();
    font.setBold(true);

    CellStyle style = workbook.createCellStyle();
    style.setFont(font);
    style.setAlignment(HorizontalAlignment.CENTER);

    return style;
  }
}
