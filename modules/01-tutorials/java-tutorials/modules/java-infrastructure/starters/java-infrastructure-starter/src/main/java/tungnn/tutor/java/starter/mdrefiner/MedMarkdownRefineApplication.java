package tungnn.tutor.java.starter.mdrefiner;

import static tungnn.tutor.java.starter.mdrefiner.MarkdownActions.normalize;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;

public class MedMarkdownRefineApplication {

  static void main() throws IOException {
    var mdPath = Path.of("storage/sample/Chuong 4 Thanh nhiet_VN_1795339108.md");
    var outPath = mdPath.resolveSibling("Chuong 4 Thanh nhiet_VN_1795339108-refined.md");

    var original = MarkdownDocumentUtils.readDocument(mdPath);

    var refined =
        new MarkdownPipeline()
            .step(
                "Gộp 2 paragraph đầu thành tên chương", MarkdownActions.mergeToParagraph(0, 2, " "))
            .step("Chuyển tên chương thành heading", MarkdownActions.toHeading(0, 1))
            .step("Paragraph 'Mục ...' thành heading", MarkdownActions.muc(2))
            .step(
                "Bỏ paragraph rỗng",
                MarkdownActions.removeIf(
                    b -> b instanceof MarkdownBlock.Paragraph(String text) && text.isBlank()))
            .step(
                "Paragraph trước '【Thành phần】' thành heading",
                MarkdownActions.thanhPhan(3))
            .step(
                "Paragraph '【...】' / '[...]' thành heading + content",
                MarkdownActions.bracketHeading(4))
            // .step("Sửa lỗi OCR", replaceText("Hoàng liên", "Hoàng liên"))
            .step("Chuẩn hóa văn bản", normalize(MarkdownRefiner.defaultMedical()))
            .run(original);

    System.out.println("\n--- Result ---");
    System.out.println(refined.toMarkdown());

    Files.writeString(outPath, refined.toMarkdown() + "\n");
    System.out.println("\nWritten: " + outPath);
  }
}
