package tungnn.tutor.java.tool.mdrefine.core;

import java.io.IOException;
import java.nio.file.Path;
import tungnn.tutor.java.infra.document.converter.MarkdownDocxConverterUtils;

public class MarkdownToDocxApp {

  static void main() throws IOException {
    var mdPath = Path.of("storage/sample/Chuong_6_On_ly_ban_dich_tieng_Viet-refined.md");
    var docxPath = Path.of("storage/sample/Chuong_6_On_ly_ban_dich_tieng_Viet-refined.docx");
    MarkdownDocxConverterUtils.convertMarkdownToDocx(mdPath, docxPath);
  }
}
