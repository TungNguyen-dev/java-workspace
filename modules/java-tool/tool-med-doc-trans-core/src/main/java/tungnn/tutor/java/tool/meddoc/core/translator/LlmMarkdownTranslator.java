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
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.meddoc.core.markdown.MarkdownActions;

public final class LlmMarkdownTranslator {

    public static final int DEFAULT_CONCURRENCY = 5;
    public static final int DEFAULT_MAX_CHUNK_CHARS = 4000;
    public static final int DEFAULT_MAX_PRESCRIPTION_CHARS = 15000;

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmMarkdownTranslator.class);
    private static final String PROMPT_RESOURCE = "prompts/translate-tcm-text.md";
    private static final String EDIT_PROMPT_RESOURCE = "prompts/edit-polish-vietnamese.md";
    private static final String LANGUAGE_PLACEHOLDER = "{targetLanguage}";
    private static final int MAX_SECTION_LEVEL = 3;
    private static final int PRESCRIPTION_LEVEL = 3;

    private final LlmClient llmClient;
    private final String promptTemplate;
    private final int maxConcurrency;
    private final int maxChunkChars;
    private final int maxPrescriptionChars;
    private final AtomicInteger success = new AtomicInteger();
    private final AtomicInteger failure = new AtomicInteger();
    private final AtomicReference<String> firstError = new AtomicReference<>();
    private final List<String> warnings = new CopyOnWriteArrayList<>();

    public LlmMarkdownTranslator(LlmClient llmClient) {
        this(llmClient, DEFAULT_CONCURRENCY, DEFAULT_MAX_CHUNK_CHARS);
    }

    public LlmMarkdownTranslator(LlmClient llmClient, int maxConcurrency, int maxChunkChars) {
        this(llmClient, maxConcurrency, maxChunkChars, DEFAULT_MAX_PRESCRIPTION_CHARS);
    }

    public LlmMarkdownTranslator(LlmClient llmClient, int maxConcurrency, int maxChunkChars, int maxPrescriptionChars) {
        this(llmClient, PROMPT_RESOURCE, maxConcurrency, maxChunkChars, maxPrescriptionChars);
    }

    private LlmMarkdownTranslator(
            LlmClient llmClient,
            String promptResource,
            int maxConcurrency,
            int maxChunkChars,
            int maxPrescriptionChars) {
        this.llmClient = Objects.requireNonNull(llmClient, "llmClient must not be null");
        this.promptTemplate = ResourceUtils.getResourceAsString(promptResource);
        this.maxConcurrency = Math.max(1, maxConcurrency);
        this.maxChunkChars = Math.max(1, maxChunkChars);
        this.maxPrescriptionChars = Math.max(1, maxPrescriptionChars);
    }

    public static LlmMarkdownTranslator editor(LlmClient llmClient) {
        return new LlmMarkdownTranslator(
                llmClient,
                EDIT_PROMPT_RESOURCE,
                DEFAULT_CONCURRENCY,
                DEFAULT_MAX_CHUNK_CHARS,
                DEFAULT_MAX_PRESCRIPTION_CHARS);
    }

    public MarkdownDocument translate(MarkdownDocument source, LanguageCode target) {
        return process(source, promptTemplate.replace(LANGUAGE_PLACEHOLDER, target.languageName()), "Translating");
    }

    public MarkdownDocument edit(MarkdownDocument source) {
        return process(source, promptTemplate, "Editing");
    }

    private MarkdownDocument process(MarkdownDocument source, String prompt, String action) {
        var chunks = chunk(source.blocks());
        LOGGER.info("{} {} blocks in {} sections", action, source.blocks().size(), chunks.size());

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
            var parts = isPrescription(section) ? splitPrescription(section, title) : splitByBlocks(section);
            for (var part : parts) {
                chunks.add(new Chunk(chunks.size() + 1, title, part));
            }
        }
        return chunks;
    }

    private static boolean isPrescription(List<MarkdownBlock> section) {
        return section.get(0) instanceof Heading h && h.level() == PRESCRIPTION_LEVEL;
    }

    private static int sizeOf(List<MarkdownBlock> blocks) {
        return blocks.stream().mapToInt(b -> b.toMarkdown().length()).sum();
    }

    private List<List<MarkdownBlock>> splitByBlocks(List<MarkdownBlock> section) {
        var parts = new ArrayList<List<MarkdownBlock>>();
        var part = new ArrayList<MarkdownBlock>();
        int size = 0;
        for (var block : section) {
            int blockSize = block.toMarkdown().length();
            if (!part.isEmpty() && size + blockSize > maxChunkChars) {
                parts.add(part);
                part = new ArrayList<>();
                size = 0;
            }
            part.add(block);
            size += blockSize;
        }
        if (!part.isEmpty()) {
            parts.add(part);
        }
        return parts;
    }

    private List<List<MarkdownBlock>> splitPrescription(List<MarkdownBlock> section, String title) {
        int total = sizeOf(section);
        if (total <= maxPrescriptionChars) {
            return List.of(section);
        }

        // The prescription heading and any lead text stay with the first sub-section.
        var subSections = new ArrayList<List<MarkdownBlock>>();
        var current = new ArrayList<MarkdownBlock>();
        boolean seenSubHeading = false;
        for (var block : section) {
            if (block instanceof Heading h && h.level() == PRESCRIPTION_LEVEL + 1) {
                if (seenSubHeading) {
                    subSections.add(current);
                    current = new ArrayList<>();
                }
                seenSubHeading = true;
            }
            current.add(block);
        }
        subSections.add(current);

        var parts = new ArrayList<List<MarkdownBlock>>();
        var part = new ArrayList<MarkdownBlock>();
        int size = 0;
        for (var subSection : subSections) {
            int subSize = sizeOf(subSection);
            if (!part.isEmpty() && size + subSize > maxPrescriptionChars) {
                parts.add(part);
                part = new ArrayList<>();
                size = 0;
            }
            part.addAll(subSection);
            size += subSize;
        }
        if (!part.isEmpty()) {
            parts.add(part);
        }
        LOGGER.warn(
                "Prescription {} ({} chars) exceeds {} chars, split into {} parts at sub-headings",
                title,
                total,
                maxPrescriptionChars,
                parts.size());
        return parts;
    }

    private static String titleOf(List<MarkdownBlock> section) {
        var first = section.get(0);
        var text = first instanceof Heading h ? h.text() : first instanceof Paragraph p ? p.text() : "";
        var title = text.strip().replaceAll("\\s+", " ");
        return title.length() <= 30 ? title : title.substring(0, 30) + "…";
    }

    private record Chunk(int index, String title, List<MarkdownBlock> blocks) {}
}
