package tungnn.tutor.java.tool.meddoc.core.format;

import tungnn.tutor.java.infra.document.adapter.markdown.transform.MarkdownTransformUtils;
import tungnn.tutor.java.tool.meddoc.core.markdown.MedMarkdownActions;
import tungnn.tutor.java.tool.meddoc.core.markdown.MedMarkdownRefiners;
import static tungnn.tutor.java.infra.document.adapter.markdown.transform.MarkdownTransformUtils.normalize;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.ai.chat.TextChat;
import tungnn.tutor.java.infra.document.adapter.markdown.transform.MarkdownAction;
import tungnn.tutor.java.infra.document.adapter.markdown.transform.MarkdownPipeline;
import tungnn.tutor.java.infra.document.adapter.markdown.transform.MarkdownRefiner;

public final class MedFormatPipelines {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedFormatPipelines.class);
    private static final Pattern SECTION_PREFIX = Pattern.compile("^第[一二三四五六七八九十百\\d]+[节節]\\s*");
    private static final Pattern CATEGORY_LEAD = Pattern.compile("^(\\p{IsHan}{1,8})剂[，,]");
    private static final String VARIANT_FORMULAS_LABEL = "Các bài thuốc biến phương gia giảm";
    private static final String USAGE_LABEL = "* Lưu ý cách dùng: ";
    private static final Map<String, String> VI_LABELS = Map.of(
            "组成", "Thành phần",
            "用法", "Cách dùng",
            "功用", "Công dụng",
            "主治", "Chủ trị",
            "方解", "Giải nghĩa bài thuốc",
            "运用", "Vận dụng lâm sàng",
            "附方", VARIANT_FORMULAS_LABEL);
    private static final Set<String> REMOVED_SECTIONS = Set.of("文献摘要", "病案");

    private MedFormatPipelines() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static MarkdownPipeline structureSource(List<String> skipped) {
        return structureSource(skipped, new AtomicInteger());
    }

    public static MarkdownPipeline structureSource(List<String> skipped, AtomicInteger removedHan) {
        return new MarkdownPipeline()
                .step("Remove bold markers", MarkdownTransformUtils.stripBold())
                .step(
                        "Turn the chapter title into a heading",
                        required(skipped, "chapter title heading", MedMarkdownActions.chapterTitle(1)))
                .step(
                        "Remove the numbered learning objectives after the chapter title",
                        safe(skipped, "learning objectives", MedMarkdownActions.removeNumberedAfterHeading(1)))
                .step(
                        "Turn '第N节 ...' paragraphs into headings and drop the prefix",
                        required(
                                skipped,
                                "section headings",
                                MedMarkdownActions.sectionHeading(SECTION_PREFIX, 2, MedFormatPipelines::categoryTitle)))
                .step(
                        "Remove empty paragraphs",
                        MarkdownTransformUtils.removeIf(
                                b -> b instanceof MarkdownBlock.Paragraph(String text) && text.isBlank()))
                .step(
                        "Turn the paragraph before '【组成】' into a heading",
                        required(skipped, "formula name headings", MedMarkdownActions.headingBeforeLabel("组成", 3)))
                .step(
                        "Turn '【...】' paragraphs into heading + content",
                        required(skipped, "bracket headings", MedMarkdownActions.bracketHeading(4)))
                .step(
                        "Remove the '#### 文献摘要' and '#### 病案' sections",
                        safe(
                                skipped,
                                "literature and case sections",
                                MedMarkdownActions.removeSections(4, REMOVED_SECTIONS, removedHan::addAndGet)))
                .step(
                        "Join paragraphs split across pages",
                        safe(skipped, "split paragraphs", MedMarkdownActions.mergeBrokenParagraphs()))
                .step(
                        "Turn numbered lists into plain paragraphs",
                        safe(skipped, "numbered lists", MedMarkdownActions.unnumberLists()));
    }

    private static String categoryTitle(String next) {
        if (next == null) {
            return null;
        }
        var m = CATEGORY_LEAD.matcher(next);
        return m.find() ? m.group(1) : null;
    }

    public static MarkdownPipeline finishTarget(List<String> skipped, MarkdownDocument source) {
        // Relabel must run first: it matches headings to the source by position, and later steps insert headings.
        return new MarkdownPipeline()
                .step(
                        "Set the Vietnamese section labels from the source headings",
                        safe(skipped, "section labels", MarkdownTransformUtils.relabelHeadings(source, 4, VI_LABELS)))
                .step("Remove bold markers", MarkdownTransformUtils.stripBold())
                .step(
                        "Add '## Đại cương' after the '#' header",
                        safe(
                                skipped,
                                "Đại cương heading",
                                MarkdownTransformUtils.insertHeadingAfterHeading(1, 2, "Đại cương")))
                .step(
                        "Remove empty paragraphs",
                        MarkdownTransformUtils.removeIf(
                                b -> b instanceof MarkdownBlock.Paragraph(String text) && text.isBlank()))
                .step("'###' header: turn 《book》 into (book)", MedMarkdownActions.bookTitleToParen(3))
                .step(
                        "'###' header: upper-case the main name, title-case the text in parentheses",
                        safe(skipped, "formula name headings", MedMarkdownActions.headingMainUpperParenTitle(3)))
                .step("Normalize text", normalize(MedMarkdownRefiners.defaultMedical()));
    }

    public static MarkdownPipeline formatTarget(TextChat chat, List<String> skipped, AtomicInteger llmFailures) {
        var ingredientsPrompt = ResourceUtils.getResourceAsString("prompts/format-ingredients-table.md");
        var variantFormulasPrompt = ResourceUtils.getResourceAsString("prompts/format-variant-formulas.md");
        var usagePrompt = ResourceUtils.getResourceAsString("prompts/format-usage-instructions.md");

        return new MarkdownPipeline()
                .step(
                        "Transform the '#### Thành phần' section into a table with an LLM",
                        safe(
                                skipped,
                                "Thành phần table",
                                MedMarkdownActions.llmSection(
                                        "Thành phần",
                                        4,
                                        chat,
                                        ingredientsPrompt,
                                        sectionErrors(skipped, llmFailures, "Thành phần table"))))
                .step(
                        "Normalize the variant formulas section with an LLM",
                        safe(
                                skipped,
                                "Phụ phương normalize",
                                MedMarkdownActions.llmSection(
                                        VARIANT_FORMULAS_LABEL,
                                        4,
                                        chat,
                                        variantFormulasPrompt,
                                        sectionErrors(skipped, llmFailures, "Phụ phương normalize"))))
                .step(
                        "Sub-formula header: keep the name only in the header",
                        safe(skipped, "sub-formula headings", MedMarkdownActions.fixHeadingName(5)))
                .step(
                        "Rewrite the '#### Cách dùng' section as modern usage instructions with an LLM",
                        safe(
                                skipped,
                                "Cách dùng rewrite",
                                MedMarkdownActions.llmSection(
                                        "Cách dùng",
                                        4,
                                        chat,
                                        usagePrompt,
                                        sectionErrors(skipped, llmFailures, "Cách dùng rewrite"))))
                .step("Normalize text", normalize(MedMarkdownRefiners.defaultMedical()))
                .step(
                        "Turn '#### Cách dùng' + content into '* Lưu ý cách dùng: ...'",
                        MarkdownTransformUtils.headingToInlineLabel(4, "Cách dùng", USAGE_LABEL));
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
