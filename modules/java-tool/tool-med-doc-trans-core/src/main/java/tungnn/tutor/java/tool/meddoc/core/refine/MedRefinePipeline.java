package tungnn.tutor.java.tool.meddoc.core.refine;

import static tungnn.tutor.java.tool.meddoc.core.markdown.MarkdownActions.normalize;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.tool.meddoc.core.llm.MarkdownChat;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.tool.meddoc.core.markdown.MarkdownAction;
import tungnn.tutor.java.tool.meddoc.core.markdown.MarkdownActions;
import tungnn.tutor.java.tool.meddoc.core.markdown.MarkdownPipeline;
import tungnn.tutor.java.tool.meddoc.core.markdown.MarkdownRefiner;

public final class MedRefinePipeline {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedRefinePipeline.class);
    private static final Pattern SECTION_PREFIX = Pattern.compile("^第[一二三四五六七八九十百\\d]+[节節]\\s*");
    private static final Pattern CATEGORY_LEAD = Pattern.compile("^(\\p{IsHan}{1,8})剂[，,]");
    private static final Map<String, String> VI_LABELS = Map.of(
            "组成", "Thành phần",
            "用法", "Cách dùng",
            "功用", "Công dụng",
            "主治", "Chủ trị",
            "方解", "Giải nghĩa bài thuốc",
            "运用", "Vận dụng lâm sàng",
            "附方", "Phụ phương");
    private static final Set<String> REMOVED_SECTIONS = Set.of("文献摘要", "病案");

    private MedRefinePipeline() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static MarkdownPipeline chineseSource(List<String> skipped) {
        return chineseSource(skipped, new AtomicInteger());
    }

    public static MarkdownPipeline chineseSource(List<String> skipped, AtomicInteger removedHan) {
        return new MarkdownPipeline()
                .step("Remove bold markers", MarkdownActions.stripBold())
                .step(
                        "Turn the chapter title into a heading",
                        required(skipped, "chapter title heading", MarkdownActions.chapterTitle(1)))
                .step(
                        "Remove the numbered learning objectives after the chapter title",
                        safe(skipped, "learning objectives", MarkdownActions.removeNumberedAfterHeading(1)))
                .step(
                        "Turn '第N节 ...' paragraphs into headings and drop the prefix",
                        required(
                                skipped,
                                "section headings",
                                MarkdownActions.sectionHeading(SECTION_PREFIX, 2, MedRefinePipeline::categoryTitle)))
                .step(
                        "Remove empty paragraphs",
                        MarkdownActions.removeIf(
                                b -> b instanceof MarkdownBlock.Paragraph(String text) && text.isBlank()))
                .step(
                        "Turn the paragraph before '【组成】' into a heading",
                        required(skipped, "formula name headings", MarkdownActions.headingBeforeLabel("组成", 3)))
                .step(
                        "Turn '【...】' paragraphs into heading + content",
                        required(skipped, "bracket headings", MarkdownActions.bracketHeading(4)))
                .step(
                        "Remove the '#### 文献摘要' and '#### 病案' sections",
                        safe(
                                skipped,
                                "literature and case sections",
                                MarkdownActions.removeSections(4, REMOVED_SECTIONS, removedHan::addAndGet)))
                .step(
                        "Join paragraphs split across pages",
                        safe(skipped, "split paragraphs", MarkdownActions.mergeBrokenParagraphs()))
                .step(
                        "Turn numbered lists into plain paragraphs",
                        safe(skipped, "numbered lists", MarkdownActions.unnumberLists()));
    }

    private static String categoryTitle(String next) {
        if (next == null) {
            return null;
        }
        var m = CATEGORY_LEAD.matcher(next);
        return m.find() ? m.group(1) : null;
    }

    public static MarkdownPipeline vietnamese(
            MarkdownChat chat, List<String> skipped, MarkdownDocument source, AtomicInteger llmFailures) {
        var ingredientsPrompt = ResourceUtils.getResourceAsString("prompts/refine-ingredients-table.md");
        var variantFormulasPrompt = ResourceUtils.getResourceAsString("prompts/refine-variant-formulas.md");
        var usagePrompt = ResourceUtils.getResourceAsString("prompts/refine-usage-instructions.md");

        MarkdownAction labels = source == null
                ? doc -> {
                    LOGGER.warn("Section labels skipped: no Chinese source in this run");
                    skipped.add("section labels: no Chinese source in this run");
                    return doc;
                }
                : safe(skipped, "section labels", MarkdownActions.relabelHeadings(source, 4, VI_LABELS));

        return new MarkdownPipeline()
                .step("Set the Vietnamese section labels from the source headings", labels)
                .step("Remove bold markers", MarkdownActions.stripBold())
                .step(
                        "Add '## Đại cương' after the '#' header",
                        safe(
                                skipped,
                                "Đại cương heading",
                                MarkdownActions.insertHeadingAfterHeading(1, 2, "Đại cương")))
                .step(
                        "Turn ordered lists into separate paragraphs",
                        safe(skipped, "ordered lists", MarkdownActions.orderedListToParagraphs(false)))
                .step(
                        "Remove empty paragraphs",
                        MarkdownActions.removeIf(
                                b -> b instanceof MarkdownBlock.Paragraph(String text) && text.isBlank()))
                .step("'###' header: turn 《book》 into (book)", MarkdownActions.bookTitleToParen(3))
                .step(
                        "'###' header: upper-case the main name, title-case the text in parentheses",
                        safe(skipped, "formula name headings", MarkdownActions.headingMainUpperParenTitle(3)))
                .step(
                        "Transform the '#### Thành phần' section into a table with an LLM",
                        safe(
                                skipped,
                                "Thành phần table",
                                MarkdownActions.llmSection(
                                        "Thành phần",
                                        4,
                                        chat,
                                        ingredientsPrompt,
                                        sectionErrors(skipped, llmFailures, "Thành phần table"))))
                .step(
                        "Normalize the '#### Phụ phương' section with an LLM",
                        safe(
                                skipped,
                                "Phụ phương normalize",
                                MarkdownActions.llmSection(
                                        "Phụ phương",
                                        4,
                                        chat,
                                        variantFormulasPrompt,
                                        sectionErrors(skipped, llmFailures, "Phụ phương normalize"))))
                .step(
                        "Rename '#### Phụ phương' to '#### Các bài thuốc biến phương gia giảm'",
                        MarkdownActions.renameHeading(4, "Phụ phương", "Các bài thuốc biến phương gia giảm"))
                .step(
                        "Rewrite the '#### Cách dùng' section as modern usage instructions with an LLM",
                        safe(
                                skipped,
                                "Cách dùng rewrite",
                                MarkdownActions.llmSection(
                                        "Cách dùng",
                                        4,
                                        chat,
                                        usagePrompt,
                                        sectionErrors(skipped, llmFailures, "Cách dùng rewrite"))))
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
                skipped.add(name + ": " + e.getMessage());
                return doc;
            }
        };
    }

    private static MarkdownAction required(List<String> skipped, String name, MarkdownAction action) {
        var guarded = safe(skipped, name, action);
        return doc -> {
            int before = skipped.size();
            var result = guarded.apply(doc);
            if (result.equals(doc) && skipped.size() == before) {
                LOGGER.warn("Refine step '{}' matched nothing", name);
                skipped.add(name + ": no matching block found");
            }
            return result;
        };
    }

    private static BiConsumer<String, RuntimeException> sectionErrors(
            List<String> skipped, AtomicInteger llmFailures, String name) {
        return (parent, e) -> {
            llmFailures.incrementAndGet();
            LOGGER.warn("Refine step '{}' failed for '{}': {}", name, parent, e.getMessage());
            skipped.add(name + " [" + parent + "]: " + e.getMessage());
        };
    }
}
