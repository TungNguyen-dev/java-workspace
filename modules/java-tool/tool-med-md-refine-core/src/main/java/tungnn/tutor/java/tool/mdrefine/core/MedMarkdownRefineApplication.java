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
                "Merge the first 2 paragraphs into the chapter title", MarkdownActions.mergeToParagraph(0, 2, " "))
            .step("Turn the chapter title into a heading", MarkdownActions.toHeading(0, 1))
            .step(
                "Add '## Đại cương' after the '#' header",
                MarkdownActions.insertHeadingAfterHeading(1, 2, "Đại cương"))
            .step(
                "Remove the first OrderedList and turn the remaining ordered lists into separate paragraphs",
                doc ->
                    MarkdownActions.orderedListToParagraphs(false)
                        .apply(MarkdownActions.removeFirstOrderedList().apply(doc)))
            .step(
                "Turn 'Mục N ...' / 'Tiết N ...' paragraphs into headings and drop the 'Mục N'/'Tiết N' prefix",
                MarkdownActions.muc(2))
            .step(
                "Remove empty paragraphs",
                MarkdownActions.removeIf(
                    b -> b instanceof MarkdownBlock.Paragraph(String text) && text.isBlank()))
            .step("Turn the paragraph before '【Thành phần】' into a heading", MarkdownActions.thanhPhan(3))
            .step(
                "'###' header: upper-case the main name, title-case the text in parentheses",
                MarkdownActions.headingMainUpperParenTitle(3))
            .step(
                "Turn '【...】' / '[...]' paragraphs into heading + content",
                MarkdownActions.bracketHeading(4))
            .step(
                "Rename '#### Giải phương' to '#### Giải nghĩa bài thuốc'",
                MarkdownActions.renameHeading(4, "Giải phương", "Giải nghĩa bài thuốc"))
            .step(
                "Rename '#### Vận dụng' to '#### Vận dụng lâm sàng'",
                MarkdownActions.renameHeading(4, "Vận dụng", "Vận dụng lâm sàng"))
            .step(
                "Transform the '#### Thành phần' section content with an LLM (DeepSeek)",
                MarkdownActions.llmSection("Thành phần", 4, llmClient, thanhPhanPrompt))
            .step(
                "Normalize the '#### Phụ phương' section content with an LLM (DeepSeek)",
                MarkdownActions.llmSection("Phụ phương", 4, llmClient, phuPhuongPrompt))
            .step(
                "Rename '#### Phụ phương' to '#### Các bài thuốc biến phương gia giảm' (after the LLM step, since that step looks the section up by the name 'Phụ phương')",
                MarkdownActions.renameHeading(
                    4, "Phụ phương", "Các bài thuốc biến phương gia giảm"))
            .step(
                "Rewrite the '#### Cách dùng' section content as modern usage instructions with an LLM (DeepSeek)",
                MarkdownActions.llmSection("Cách dùng", 4, llmClient, cachDungPrompt))
            .step(
                "Sub-formula header: keep the name only in the header and drop the repeated name from the content",
                MarkdownActions.fixHeadingName(5))
            .step("Normalize text", normalize(MarkdownRefiner.defaultMedical()))
            .step(
                "Turn '#### Cách dùng' + content into '* Lưu ý cách dùng: ...' (after the LLM and normalize steps, since a heading is a section boundary and normalization turns '* ' into '- ')",
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
        throw new IllegalStateException("Prompt not found on the classpath: " + resource);
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
