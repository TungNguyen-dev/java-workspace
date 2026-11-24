package tungnn.tutor.java.tool.mdrefine.core;

import static tungnn.tutor.java.tool.mdrefine.core.MarkdownActions.normalize;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.util.LlmClientUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;

public class MedMarkdownRefineApplication {

  private static final String THANH_PHAN_PROMPT_RESOURCE = "prompts/thanhphan-table.md";

  private static final String PHU_PHUONG_PROMPT_RESOURCE = "prompts/phuphuong-normalize.md";

  private static final String CACH_DUNG_PROMPT_RESOURCE = "prompts/cachdung-modern.md";

  static void main() throws IOException {
    var mdPath = Path.of("storage/sample/Chuong_6_On_ly_ban_dich_tieng_Viet.md");
    var outPath = mdPath.resolveSibling("Chuong_6_On_ly_ban_dich_tieng_Viet-refined.md");

    var original = MarkdownDocumentUtils.readDocument(mdPath);
    var thanhPhanPrompt = loadPrompt(THANH_PHAN_PROMPT_RESOURCE);
    var phuPhuongPrompt = loadPrompt(PHU_PHUONG_PROMPT_RESOURCE);
    var cachDungPrompt = loadPrompt(CACH_DUNG_PROMPT_RESOURCE);
    var llmClient = LlmClientUtils.fromEnv(LlmProvider.DEEPSEEK);

    var refined =
        new MarkdownPipeline()
            .step(
                "Gộp 2 paragraph đầu thành tên chương", MarkdownActions.mergeToParagraph(0, 2, " "))
            .step("Chuyển tên chương thành heading", MarkdownActions.toHeading(0, 1))
            .step(
                "Thêm '## Đại cương' sau header '#'",
                MarkdownActions.insertHeadingAfterHeading(1, 2, "Đại cương"))
            .step(
                "Xoá OrderedList đầu tiên, chuyển các ordered list còn lại thành paragraph riêng biệt",
                doc ->
                    MarkdownActions.orderedListToParagraphs(false)
                        .apply(MarkdownActions.removeFirstOrderedList().apply(doc)))
            .step(
                "Paragraph 'Mục N ...' / 'Tiết N ...' thành heading, bỏ prefix 'Mục N'/'Tiết N'",
                MarkdownActions.muc(2))
            .step(
                "Bỏ paragraph rỗng",
                MarkdownActions.removeIf(
                    b -> b instanceof MarkdownBlock.Paragraph(String text) && text.isBlank()))
            .step("Paragraph trước '【Thành phần】' thành heading", MarkdownActions.thanhPhan(3))
            .step(
                "Header '###': tên chính viết HOA, nội dung trong ngoặc viết hoa chữ cái đầu mỗi từ",
                MarkdownActions.headingMainUpperParenTitle(3))
            .step(
                "Paragraph '【...】' / '[...]' thành heading + content",
                MarkdownActions.bracketHeading(4))
            .step(
                "Đổi '#### Giải phương' thành '#### Giải nghĩa bài thuốc'",
                MarkdownActions.renameHeading(4, "Giải phương", "Giải nghĩa bài thuốc"))
            .step(
                "Đổi '#### Vận dụng' thành '#### Vận dụng lâm sàng'",
                MarkdownActions.renameHeading(4, "Vận dụng", "Vận dụng lâm sàng"))
            .step(
                "Biến đổi nội dung section '#### Thành phần' bằng LLM (DeepSeek)",
                MarkdownActions.llmSection("Thành phần", 4, llmClient, thanhPhanPrompt))
            .step(
                "Chuẩn hoá nội dung section '#### Phụ phương' bằng LLM (DeepSeek)",
                MarkdownActions.llmSection("Phụ phương", 4, llmClient, phuPhuongPrompt))
            .step(
                "Đổi '#### Phụ phương' thành '#### Các bài thuốc biến phương gia giảm' (sau bước LLM, vì bước đó tìm theo tên 'Phụ phương')",
                MarkdownActions.renameHeading(
                    4, "Phụ phương", "Các bài thuốc biến phương gia giảm"))
            .step(
                "Chuyển nội dung section '#### Cách dùng' thành hướng dẫn sử dụng hiện đại bằng LLM (DeepSeek)",
                MarkdownActions.llmSection("Cách dùng", 4, llmClient, cachDungPrompt))
            .step(
                "Header phụ phương: tên chỉ nằm ở header, bỏ tên lặp lại ở nội dung",
                MarkdownActions.fixHeadingName(5))
            .step("Chuẩn hóa văn bản", normalize(MarkdownRefiner.defaultMedical()))
            .step(
                "Đổi '#### Cách dùng' + nội dung thành '* Lưu ý cách dùng: ...' (sau LLM và sau chuẩn hóa, vì heading là ranh giới section và bước chuẩn hóa đổi '* ' thành '- ')",
                MarkdownActions.headingToInlineLabel(4, "Cách dùng", "* Lưu ý cách dùng: "))
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
