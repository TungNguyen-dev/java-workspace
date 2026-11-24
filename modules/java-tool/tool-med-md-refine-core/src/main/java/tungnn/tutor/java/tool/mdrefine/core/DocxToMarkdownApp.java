package tungnn.tutor.java.tool.mdrefine.core;

import java.io.IOException;
import java.nio.file.Path;
import tungnn.tutor.java.infra.document.converter.DocxMarkdownConverterUtils;

public class DocxToMarkdownApp {

    static void main() throws IOException {
        var docxPath = Path.of("storage/sample/Chuong_6_On_ly_ban_dich_tieng_Viet.docx");
        var mdPath = Path.of("storage/sample/Chuong_6_On_ly_ban_dich_tieng_Viet.md");
        DocxMarkdownConverterUtils.convertDocxToMarkdown(docxPath, mdPath);
    }
}
