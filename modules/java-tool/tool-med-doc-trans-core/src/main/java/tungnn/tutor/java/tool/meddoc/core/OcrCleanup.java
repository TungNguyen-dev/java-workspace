package tungnn.tutor.java.tool.meddoc.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.ai.util.LlmOutputUtils;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtils;
import tungnn.tutor.java.core.lib.multithread.ConcurrentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

public class OcrCleanup {

    private static final Logger LOGGER = LoggerFactory.getLogger(OcrCleanup.class);
    private static final String PROMPT_RESOURCE = "prompts/ocr-fix-zh.md";
    private static final int DEFAULT_CONCURRENCY = 5;

    private final LlmClient llmClient;
    private final String systemPrompt;
    private final int maxConcurrency;

    public OcrCleanup(LlmClient llmClient) {
        this(llmClient, DEFAULT_CONCURRENCY);
    }

    public OcrCleanup(LlmClient llmClient, int maxConcurrency) {
        this.llmClient = Objects.requireNonNull(llmClient, "llmClient must not be null");
        this.systemPrompt = ResourceUtils.getResourceAsString(PROMPT_RESOURCE);
        this.maxConcurrency = Math.max(1, maxConcurrency);
    }

    public Result clean(MarkdownDocument source) {
        var normalized = MarkdownActions.normalize(MarkdownRefiner.forSource()).apply(source);
        var structured = MarkdownActions.orderedListToParagraphs(true).apply(normalized);
        var blocks = structured.blocks();

        var indexes = new ArrayList<Integer>();
        for (int i = 0; i < blocks.size(); i++) {
            if (!MarkdownActions.textOf(blocks.get(i)).isBlank() && isTextBlock(blocks.get(i))) indexes.add(i);
        }
        LOGGER.info("OCR cleanup: {} of {} blocks sent to the LLM", indexes.size(), blocks.size());

        var cleaned = ConcurrentUtils.executeConcurrently(indexes, i -> cleanBlock(blocks.get(i)), maxConcurrency);

        var out = new ArrayList<MarkdownBlock>(blocks);
        var fixes = new ArrayList<OcrFix>();
        for (int k = 0; k < indexes.size(); k++) {
            int i = indexes.get(k);
            var after = cleaned.get(k);
            var before = blocks.get(i);
            if (!after.equals(before)) {
                out.set(i, after);
                fixes.add(new OcrFix(i, MarkdownActions.textOf(before), MarkdownActions.textOf(after)));
            }
        }
        return new Result(new MarkdownDocument(out), fixes);
    }

    private static boolean isTextBlock(MarkdownBlock block) {
        return block instanceof Heading || block instanceof Paragraph;
    }

    private MarkdownBlock cleanBlock(MarkdownBlock block) {
        var text = MarkdownActions.textOf(block);
        try {
            var fixed = LlmOutputUtils.clean(llmClient.chat(systemPrompt, text));
            // an empty answer would silently drop the block, so keep the original instead
            if (fixed == null || fixed.isBlank()) return block;
            return switch (block) {
                case Heading h -> new Heading(h.level(), fixed.strip());
                default -> new Paragraph(fixed.strip());
            };
        } catch (RuntimeException e) {
            LOGGER.warn("OCR cleanup failed for a block, keeping the original text: {}", e.getMessage());
            return block;
        }
    }

    public record Result(MarkdownDocument document, List<OcrFix> fixes) {}
}
