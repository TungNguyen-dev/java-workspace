package tungnn.tutor.java.starter.mdrefiner;

import static tungnn.tutor.java.starter.mdrefiner.MarkdownActions.normalize;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.util.LlmClientUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;

public class MedMarkdownRefineApplication {

  /** System prompt để biến đổi nội dung section "Thành phần" thành bảng bằng LLM. */
  private static final String THANH_PHAN_PROMPT_RESOURCE = "prompts/thanhphan-table.md";

  /** System prompt để chuẩn hoá nội dung section "Phụ phương" bằng LLM. */
  private static final String PHU_PHUONG_PROMPT_RESOURCE = "prompts/phuphuong-normalize.md";

  static void main() throws IOException {
    var mdPath = Path.of("storage/sample/Chuong_4_Thanh_nhiet_te_ban_dich_tieng_Viet.md");
    var outPath = mdPath.resolveSibling("Chuong_4_Thanh_nhiet_te_ban_dich_tieng_Viet-refined.md");

    var original = MarkdownDocumentUtils.readDocument(mdPath);
    var thanhPhanPrompt = loadPrompt(THANH_PHAN_PROMPT_RESOURCE);
    var phuPhuongPrompt = loadPrompt(PHU_PHUONG_PROMPT_RESOURCE);
    var llmClient = LlmClientUtils.fromEnv(LlmProvider.DEEPSEEK);

    var refined =
        new MarkdownPipeline()
            .step(
                "Gộp 2 paragraph đầu thành tên chương", MarkdownActions.mergeToParagraph(0, 2, " "))
            .step("Chuyển tên chương thành heading", MarkdownActions.toHeading(0, 1))
            .step(
                "Chuyển các ordered list (1, 2, 3, ...) thành paragraph riêng biệt",
                MarkdownActions.orderedListToParagraphs(false))
            .step("Paragraph 'Mục ...' / 'Tiết ...' thành heading", MarkdownActions.muc(2))
            .step(
                "Bỏ paragraph rỗng",
                MarkdownActions.removeIf(
                    b -> b instanceof MarkdownBlock.Paragraph(String text) && text.isBlank()))
            .step("Paragraph trước '【Thành phần】' thành heading", MarkdownActions.thanhPhan(3))
            .step(
                "Paragraph '【...】' / '[...]' thành heading + content",
                MarkdownActions.bracketHeading(4))
            .step(
                "Biến đổi nội dung section '#### Thành phần' bằng LLM (DeepSeek)",
                MarkdownActions.llmSection("Thành phần", 4, llmClient, thanhPhanPrompt))
            .step(
                "Chuẩn hoá nội dung section '#### Phụ phương' bằng LLM (DeepSeek)",
                MarkdownActions.llmSection("Phụ phương", 4, llmClient, phuPhuongPrompt))
            .step(
                "Header phụ phương: tên chỉ nằm ở header, bỏ tên lặp lại ở nội dung",
                MarkdownActions.fixHeadingName(5))
            // .step("Sửa lỗi OCR", replaceText("Hoàng liên", "Hoàng liên"))
            .step("Chuẩn hóa văn bản", normalize(MarkdownRefiner.defaultMedical()))
            .run(original);

    System.out.println("\n--- Result ---");
    System.out.println(refined.toMarkdown());

    Files.writeString(outPath, refined.toMarkdown() + "\n");
    System.out.println("\nWritten: " + outPath);
  }

  private static String loadPrompt(String resource) throws IOException {
    try (var in =
        MedMarkdownRefineApplication.class.getClassLoader().getResourceAsStream(resource)) {
      if (in == null) {
        throw new IllegalStateException("Không tìm thấy prompt trên classpath: " + resource);
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
