package tungnn.tutor.java.tool.meddoc.core.ocr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.tool.meddoc.core.llm.MarkdownChat;
import tungnn.tutor.java.ai.util.LlmOutputUtils;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtils;
import tungnn.tutor.java.core.lib.multithread.ConcurrentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.tool.meddoc.core.markdown.MarkdownActions;
import tungnn.tutor.java.tool.meddoc.core.markdown.MarkdownRefiner;

public class OcrCleanup {

    private static final Logger LOGGER = LoggerFactory.getLogger(OcrCleanup.class);
    private static final String PROMPT_RESOURCE = "prompts/ocr-correct-chinese.md";
    private static final int DEFAULT_CONCURRENCY = 5;

    private final MarkdownChat chat;
    private final String systemPrompt;
    private final int maxConcurrency;

    public OcrCleanup(MarkdownChat chat) {
        this(chat, DEFAULT_CONCURRENCY);
    }

    public OcrCleanup(MarkdownChat chat, int maxConcurrency) {
        this.chat = Objects.requireNonNull(chat, "chat must not be null");
        this.systemPrompt = ResourceUtils.getResourceAsString(PROMPT_RESOURCE);
        this.maxConcurrency = Math.max(1, maxConcurrency);
    }

    public Result clean(MarkdownDocument source) {
        var normalized = MarkdownActions.normalize(MarkdownRefiner.forSource()).apply(source);
        var numbered = MarkdownActions.orderedListToParagraphs(true).apply(normalized);
        var structured = MarkdownActions.listNumbersToEnumerationMark().apply(numbered);
        var blocks = structured.blocks();

        var indexes = new ArrayList<Integer>();
        for (int i = 0; i < blocks.size(); i++) {
            if (!MarkdownActions.textOf(blocks.get(i)).isBlank() && isTextBlock(blocks.get(i))) indexes.add(i);
        }
        var groups = new LinkedHashMap<String, List<Integer>>();
        for (int i : indexes) {
            groups.computeIfAbsent(MarkdownActions.textOf(blocks.get(i)), k -> new ArrayList<>())
                    .add(i);
        }
        LOGGER.info(
                "OCR cleanup: {} of {} blocks sent to the LLM as {} unique texts",
                indexes.size(),
                blocks.size(),
                groups.size());

        var texts = new ArrayList<>(groups.keySet());
        var fixedTexts = ConcurrentUtils.executeConcurrently(texts, this::fixText, maxConcurrency);

        var out = new ArrayList<MarkdownBlock>(blocks);
        var fixes = new ArrayList<OcrFix>();
        for (int k = 0; k < texts.size(); k++) {
            var fixed = fixedTexts.get(k);
            for (int i : groups.get(texts.get(k))) {
                var before = blocks.get(i);
                var after = withText(before, fixed);
                if (!after.equals(before)) {
                    out.set(i, after);
                    fixes.add(new OcrFix(i, MarkdownActions.textOf(before), fixed));
                }
            }
        }
        fixes.sort(Comparator.comparingInt(OcrFix::blockIndex));
        return new Result(new MarkdownDocument(out), fixes);
    }

    private static boolean isTextBlock(MarkdownBlock block) {
        return block instanceof Heading || block instanceof Paragraph;
    }

    private static MarkdownBlock withText(MarkdownBlock block, String text) {
        return switch (block) {
            case Heading h -> new Heading(h.level(), text);
            default -> new Paragraph(text);
        };
    }

    private String fixText(String text) {
        try {
            var fixed = LlmOutputUtils.clean(chat.chat(systemPrompt, text));
            // an empty answer would silently drop the block, so keep the original instead
            if (fixed == null || fixed.isBlank()) return text;
            return fixed.strip();
        } catch (RuntimeException e) {
            LOGGER.warn("OCR cleanup failed for a block, keeping the original text: {}", e.getMessage());
            return text;
        }
    }

    public record Result(MarkdownDocument document, List<OcrFix> fixes) {}
}
