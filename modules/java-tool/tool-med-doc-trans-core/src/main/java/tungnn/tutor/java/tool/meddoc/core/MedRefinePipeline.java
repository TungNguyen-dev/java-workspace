package tungnn.tutor.java.tool.meddoc.core;

import static tungnn.tutor.java.tool.meddoc.core.MarkdownActions.normalize;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;

public final class MedRefinePipeline {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedRefinePipeline.class);

    private MedRefinePipeline() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static MarkdownPipeline vietnamese(LlmClient llmClient, List<String> skipped) {
        var thanhPhanPrompt = ResourceUtils.getResourceAsString("prompts/thanhphan-table.md");
        var phuPhuongPrompt = ResourceUtils.getResourceAsString("prompts/phuphuong-normalize.md");
        var cachDungPrompt = ResourceUtils.getResourceAsString("prompts/cachdung-modern.md");

        return new MarkdownPipeline()
                .step(
                        "Merge the first 2 paragraphs into the chapter title",
                        safe(skipped, "merge chapter title", MarkdownActions.mergeToParagraph(0, 2, " ")))
                .step(
                        "Turn the chapter title into a heading",
                        safe(skipped, "chapter title heading", MarkdownActions.toHeading(0, 1)))
                .step(
                        "Add '## Đại cương' after the '#' header",
                        safe(skipped, "Đại cương heading", MarkdownActions.insertHeadingAfterHeading(1, 2, "Đại cương")))
                .step(
                        "Remove the first OrderedList and turn the remaining ordered lists into separate paragraphs",
                        safe(skipped, "ordered lists", doc -> MarkdownActions.orderedListToParagraphs(false)
                                .apply(MarkdownActions.removeFirstOrderedList().apply(doc))))
                .step(
                        "Turn 'Mục N ...' / 'Tiết N ...' paragraphs into headings and drop the prefix",
                        safe(skipped, "Mục/Tiết headings", MarkdownActions.muc(2)))
                .step(
                        "Remove empty paragraphs",
                        MarkdownActions.removeIf(
                                b -> b instanceof MarkdownBlock.Paragraph(String text) && text.isBlank()))
                .step(
                        "Turn the paragraph before '【Thành phần】' into a heading",
                        safe(skipped, "Thành phần heading", MarkdownActions.thanhPhan(3)))
                .step(
                        "'###' header: upper-case the main name, title-case the text in parentheses",
                        safe(skipped, "formula name headings", MarkdownActions.headingMainUpperParenTitle(3)))
                .step(
                        "Turn '【...】' / '[...]' paragraphs into heading + content",
                        safe(skipped, "bracket headings", MarkdownActions.bracketHeading(4)))
                .step(
                        "Rename '#### Giải phương' to '#### Giải nghĩa bài thuốc'",
                        MarkdownActions.renameHeading(4, "Giải phương", "Giải nghĩa bài thuốc"))
                .step(
                        "Rename '#### Vận dụng' to '#### Vận dụng lâm sàng'",
                        MarkdownActions.renameHeading(4, "Vận dụng", "Vận dụng lâm sàng"))
                .step(
                        "Transform the '#### Thành phần' section into a table with an LLM",
                        safe(skipped, "Thành phần table", MarkdownActions.llmSection("Thành phần", 4, llmClient, thanhPhanPrompt)))
                .step(
                        "Normalize the '#### Phụ phương' section with an LLM",
                        safe(skipped, "Phụ phương normalize", MarkdownActions.llmSection("Phụ phương", 4, llmClient, phuPhuongPrompt)))
                .step(
                        "Rename '#### Phụ phương' to '#### Các bài thuốc biến phương gia giảm'",
                        MarkdownActions.renameHeading(4, "Phụ phương", "Các bài thuốc biến phương gia giảm"))
                .step(
                        "Rewrite the '#### Cách dùng' section as modern usage instructions with an LLM",
                        safe(skipped, "Cách dùng rewrite", MarkdownActions.llmSection("Cách dùng", 4, llmClient, cachDungPrompt)))
                .step(
                        "Sub-formula header: keep the name only in the header",
                        safe(skipped, "sub-formula headings", MarkdownActions.fixHeadingName(5)))
                .step("Normalize text", normalize(MarkdownRefiner.defaultMedical()))
                .step(
                        "Turn '#### Cách dùng' + content into '* Lưu ý cách dùng: ...'",
                        MarkdownActions.headingToInlineLabel(4, "Cách dùng", "* Lưu ý cách dùng: "));
    }

    private static MarkdownAction safe(List<String> skipped, String name, MarkdownAction action) {
        return doc -> {
            try {
                return action.apply(doc);
            } catch (RuntimeException e) {
                LOGGER.warn("Refine step '{}' skipped: {}", name, e.getMessage());
                skipped.add(name);
                return doc;
            }
        };
    }
}
