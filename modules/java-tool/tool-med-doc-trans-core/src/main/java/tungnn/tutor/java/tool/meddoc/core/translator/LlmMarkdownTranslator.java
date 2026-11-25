package tungnn.tutor.java.tool.meddoc.core.translator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.ai.util.LlmOutputUtils;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtils;
import tungnn.tutor.java.core.lib.multithread.ConcurrentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.markdown.MarkdownActions;

public final class LlmMarkdownTranslator {

    public static final int DEFAULT_CONCURRENCY = 5;
    public static final int DEFAULT_MAX_CHUNK_CHARS = 4000;

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmMarkdownTranslator.class);
    private static final String PROMPT_RESOURCE = "prompts/translate-dongy.md";
    private static final String LANGUAGE_PLACEHOLDER = "{targetLanguage}";
    private static final int MAX_SECTION_LEVEL = 3;

    private final LlmClient llmClient;
    private final String promptTemplate;
    private final int maxConcurrency;
    private final int maxChunkChars;
    private final AtomicInteger success = new AtomicInteger();
    private final AtomicInteger failure = new AtomicInteger();
    private final AtomicReference<String> firstError = new AtomicReference<>();
    private final List<String> warnings = new CopyOnWriteArrayList<>();

    public LlmMarkdownTranslator(LlmClient llmClient) {
        this(llmClient, DEFAULT_CONCURRENCY, DEFAULT_MAX_CHUNK_CHARS);
    }

    public LlmMarkdownTranslator(LlmClient llmClient, int maxConcurrency, int maxChunkChars) {
        this.llmClient = Objects.requireNonNull(llmClient, "llmClient must not be null");
        this.promptTemplate = ResourceUtils.getResourceAsString(PROMPT_RESOURCE);
        this.maxConcurrency = Math.max(1, maxConcurrency);
        this.maxChunkChars = Math.max(1, maxChunkChars);
    }

    public MarkdownDocument translate(MarkdownDocument source, LanguageCode target) {
        var prompt = promptTemplate.replace(LANGUAGE_PLACEHOLDER, target.languageName());
        var chunks = chunk(source.blocks());
        LOGGER.info("Translating {} blocks in {} sections", source.blocks().size(), chunks.size());

        var translated = ConcurrentUtils.executeConcurrently(chunks, c -> translateChunk(c, prompt), maxConcurrency);

        var blocks = new ArrayList<MarkdownBlock>();
        translated.forEach(blocks::addAll);
        var document = new MarkdownDocument(blocks);
        // The LLM may answer "1. text" as a list; keep every list number as a plain "N. text" paragraph.
        document = MarkdownActions.orderedListToParagraphs(true).apply(document);
        return MarkdownActions.enumerationMarkToListNumber().apply(document);
    }

    public int successCount() {
        return success.get();
    }

    public int failureCount() {
        return failure.get();
    }

    public String firstError() {
        return firstError.get();
    }

    public List<String> warnings() {
        return List.copyOf(warnings);
    }

    private List<MarkdownBlock> translateChunk(Chunk chunk, String prompt) {
        var text = chunk.blocks().stream().map(MarkdownBlock::toMarkdown).collect(Collectors.joining("\n"));
        if (text.isBlank()) {
            return chunk.blocks();
        }
        try {
            var answer = LlmOutputUtils.clean(llmClient.chat(prompt, text));
            var blocks = answer == null || answer.isBlank()
                    ? List.<MarkdownBlock>of()
                    : MarkdownDocumentUtils.parse(answer).blocks();
            if (blocks.isEmpty()) {
                recordFailure(chunk, "the LLM returned an empty translation");
                return chunk.blocks();
            }
            checkHeadings(chunk, blocks);
            success.incrementAndGet();
            return blocks;
        } catch (RuntimeException e) {
            recordFailure(chunk, e.getMessage());
            return chunk.blocks();
        }
    }

    private void checkHeadings(Chunk chunk, List<MarkdownBlock> translated) {
        var before = headingLevels(chunk.blocks());
        var after = headingLevels(translated);
        if (!before.equals(after)) {
            warnings.add("section %d (%s): heading levels changed from %s to %s"
                    .formatted(chunk.index(), chunk.title(), before, after));
        }
    }

    private static List<Integer> headingLevels(List<MarkdownBlock> blocks) {
        return blocks.stream()
                .filter(Heading.class::isInstance)
                .map(b -> ((Heading) b).level())
                .toList();
    }

    private void recordFailure(Chunk chunk, String message) {
        LOGGER.warn(
                "Translation of section {} ({}) failed, keeping the original: {}",
                chunk.index(),
                chunk.title(),
                message);
        failure.incrementAndGet();
        firstError.compareAndSet(
                null,
                "section %d (%s): %s"
                        .formatted(chunk.index(), chunk.title(), message == null ? "unknown error" : message));
    }

    private List<Chunk> chunk(List<MarkdownBlock> blocks) {
        var sections = new ArrayList<List<MarkdownBlock>>();
        var current = new ArrayList<MarkdownBlock>();
        for (var block : blocks) {
            if (block instanceof Heading h && h.level() <= MAX_SECTION_LEVEL && !current.isEmpty()) {
                sections.add(current);
                current = new ArrayList<>();
            }
            current.add(block);
        }
        if (!current.isEmpty()) {
            sections.add(current);
        }

        var chunks = new ArrayList<Chunk>();
        for (var section : sections) {
            var title = titleOf(section);
            var part = new ArrayList<MarkdownBlock>();
            int size = 0;
            for (var block : section) {
                int blockSize = block.toMarkdown().length();
                if (!part.isEmpty() && size + blockSize > maxChunkChars) {
                    chunks.add(new Chunk(chunks.size() + 1, title, part));
                    part = new ArrayList<>();
                    size = 0;
                }
                part.add(block);
                size += blockSize;
            }
            if (!part.isEmpty()) {
                chunks.add(new Chunk(chunks.size() + 1, title, part));
            }
        }
        return chunks;
    }

    private static String titleOf(List<MarkdownBlock> section) {
        var first = section.get(0);
        var text = first instanceof Heading h ? h.text() : first instanceof Paragraph p ? p.text() : "";
        var title = text.strip().replaceAll("\\s+", " ");
        return title.length() <= 30 ? title : title.substring(0, 30) + "…";
    }

    private record Chunk(int index, String title, List<MarkdownBlock> blocks) {}
}
