package tungnn.tutor.java.tool.doctrans.core.domain.document;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.core.lib.io.filesystem.FileNameUtils;
import tungnn.tutor.java.core.lib.multithread.ConcurrentUtils;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchOverride;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchPolicy;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextUnitBatchUtils;
import tungnn.tutor.java.tool.doctrans.core.domain.cache.NoOpTranslationCache;
import tungnn.tutor.java.tool.doctrans.core.domain.cache.TranslationCache;

public abstract class AbstractDocumentTranslator<D> implements DocumentTranslator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDocumentTranslator.class);
    protected final TextTranslator textTranslator;
    protected final TranslationCache translationCache;
    private final TextTranslationBatchOverride batchOverride;

    protected AbstractDocumentTranslator(TextTranslator textTranslator) {
        this(textTranslator, NoOpTranslationCache.INSTANCE);
    }

    protected AbstractDocumentTranslator(TextTranslator textTranslator, TranslationCache translationCache) {
        this(textTranslator, translationCache, TextTranslationBatchOverride.NONE);
    }

    protected AbstractDocumentTranslator(
            TextTranslator textTranslator,
            TranslationCache translationCache,
            TextTranslationBatchOverride batchOverride) {
        this.batchOverride = Objects.requireNonNull(batchOverride, "batchOverride must not be null");
        this.textTranslator = Objects.requireNonNull(textTranslator, "textTranslator must not be null");
        this.translationCache = Objects.requireNonNull(translationCache, "translationCache must not be null");
    }

    private static boolean isApplicable(TextTranslationResult.Single result) {
        return result instanceof TextTranslationResult.Success(TextTranslationResult.TranslatedText translatedText)
                && translatedText != null
                && translatedText.translatedContent() != null;
    }

    private static boolean isMemorizable(TextTranslationResult.TranslatedText translatedText) {
        return !translatedText.translatedContent().isBlank();
    }

    private static boolean isBlank(String content) {
        return content == null || content.isBlank();
    }

    @Override
    public DocumentTranslationResult translate(DocumentTranslationRequest.Single request) {
        LOGGER.info(
                "Starting translation request for document path: {}, target language: {}",
                request.documentSource().path(),
                request.targetLanguage());
        LOGGER.info("Opening document...");
        var document = openDocument(request);
        LOGGER.info("Document opened successfully.");
        try {
            LOGGER.info("Collecting segments from document...");
            var segments = collectSegments(document);
            var textBlockCount = flatten(segments).size();
            LOGGER.info("Collected {} segments with {} text blocks", segments.size(), textBlockCount);
            LOGGER.info("Starting translation for {} text blocks...", textBlockCount);
            var translatedTextBlocks = translateSegments(request, segments);
            LOGGER.info("Completed translation for {} text blocks.", translatedTextBlocks.size());
            LOGGER.info("Applying translations to document blocks...");
            applyTranslation(translatedTextBlocks);
            LOGGER.info("Translations applied successfully.");
            var translationPath = buildTranslationPath(request);
            LOGGER.info("Saving translated document to path: {}", translationPath);
            saveDocument(translationPath, document);
            LOGGER.info("Document saved successfully.");
            return new DocumentTranslationResult.Success(
                    new DocumentTranslationResult.TranslatedDocument(translationPath));
        } catch (Exception e) {
            LOGGER.info(
                    "Failed to translate document: {}", request.documentSource().path(), e);
            return new DocumentTranslationResult.Failure(new DocumentTranslationResult.Error("", e.getMessage()));
        } finally {
            LOGGER.info("Closing document...");
            closeDocument(document);
            LOGGER.info("Document closed.");
        }
    }

    protected abstract D openDocument(DocumentTranslationRequest.Single request);

    protected abstract List<DocumentSegment> collectSegments(D document);

    protected abstract void saveDocument(Path translationPath, D document);

    protected abstract TextTranslationBatchPolicy defaultBatchPolicy();

    protected final TextTranslationBatchPolicy batchPolicy() {
        return batchOverride.applyTo(defaultBatchPolicy());
    }

    private static List<DocumentTextBlock> flatten(List<DocumentSegment> segments) {
        return segments.stream()
                .flatMap(segment -> segment.textBlocks().stream())
                .toList();
    }

    private List<TranslatedTextBlock> translateSegments(
            DocumentTranslationRequest.Single request, List<DocumentSegment> segments) {
        var textBlocks = flatten(segments);
        if (textBlocks.isEmpty()) {
            LOGGER.debug("Text blocks list is empty, skipping translation.");
            return List.of();
        }
        var targetLanguage = request.targetLanguage();
        Map<String, TextTranslationResult.TranslatedText> translatedTextByContent = new HashMap<>();
        // The first occurrence of a content decides which segment keeps its text unit.
        var seenContents = new HashSet<String>();
        var pendingBySegment = new ArrayList<List<TextTranslationRequest.TextUnit>>();
        int pendingCount = 0;
        for (var segment : segments) {
            var pendingUnits = new ArrayList<TextTranslationRequest.TextUnit>();
            for (var textBlock : segment.textBlocks()) {
                var content = textBlock.content();
                if (isBlank(content) || !seenContents.add(content)) {
                    continue;
                }
                var textUnitId = UUID.randomUUID().toString();
                var memorized = translationCache.find(targetLanguage, content);
                if (memorized.isPresent()) {
                    LOGGER.debug("Translation memory hit for text unit [id={}]", textUnitId);
                    translatedTextByContent.put(
                            content,
                            new TextTranslationResult.TranslatedText(
                                    textUnitId, content, memorized.get(), targetLanguage));
                } else {
                    LOGGER.debug("Translation memory miss for text unit [id={}]", textUnitId);
                    pendingUnits.add(new TextTranslationRequest.TextUnit(textUnitId, content));
                }
            }
            if (!pendingUnits.isEmpty()) {
                pendingBySegment.add(pendingUnits);
                pendingCount += pendingUnits.size();
            }
        }
        LOGGER.debug("Deduplicated {} text blocks into {} unique contents.", textBlocks.size(), seenContents.size());
        var batchPolicy = batchPolicy();
        var chunks = TextUnitBatchUtils.chunkSegments(pendingBySegment, batchPolicy);
        LOGGER.debug(
                "Split {} pending text units in {} segments into {} chunks [policy={}].",
                pendingCount,
                pendingBySegment.size(),
                chunks.size(),
                batchPolicy);
        var maxConcurrency = Math.max(1, textTranslator.maxConcurrency());
        var chunkTranslations = chunks.isEmpty()
                ? List.<List<TranslatedContent>>of()
                : ConcurrentUtils.executeConcurrently(
                        chunks, chunk -> translateChunk(targetLanguage, chunk), maxConcurrency);
        for (var chunkTranslation : chunkTranslations) {
            for (var translatedContent : chunkTranslation) {
                translatedTextByContent.put(translatedContent.content(), translatedContent.translatedText());
            }
        }
        var translatedTextBlocks = new ArrayList<TranslatedTextBlock>(textBlocks.size());
        for (var textBlock : textBlocks) {
            var translatedText = translatedTextByContent.get(textBlock.content());
            translatedTextBlocks.add(
                    translatedText != null
                            ? new TranslatedTextBlock(textBlock, translatedText)
                            : keepOriginalContent(textBlock, request.targetLanguage()));
        }
        return translatedTextBlocks;
    }

    private List<TranslatedContent> translateChunk(
            LanguageCode targetLanguage, List<TextTranslationRequest.TextUnit> chunk) {
        LOGGER.debug("Translating chunk of {} text units.", chunk.size());
        var batchRequest = new TextTranslationRequest.Batch(chunk, targetLanguage, null, null);
        List<TextTranslationResult.Single> raw;
        try {
            raw = textTranslator.translateBatch(batchRequest).results();
        } catch (Exception e) {
            LOGGER.warn("Chunk translation threw, translating {} text units individually", chunk.size(), e);
            raw = translateUnitByUnit(batchRequest);
        }
        var aligned = TextTranslationResult.Batch.alignedTo(batchRequest, raw).results();
        var translatedContents = new ArrayList<TranslatedContent>(chunk.size());
        for (int i = 0; i < chunk.size(); i++) {
            translatedContents.add(toTranslatedContent(targetLanguage, chunk.get(i), aligned.get(i)));
        }
        return translatedContents;
    }

    private List<TextTranslationResult.Single> translateUnitByUnit(TextTranslationRequest.Batch batchRequest) {
        var results = new ArrayList<TextTranslationResult.Single>(
                batchRequest.textUnits().size());
        for (var textUnit : batchRequest.textUnits()) {
            try {
                results.add(textTranslator.translate(batchRequest.single(textUnit)));
            } catch (Exception e) {
                LOGGER.warn("Translation threw for text unit [id={}]", textUnit.id(), e);
                results.add(null);
            }
        }
        return results;
    }

    private TranslatedContent toTranslatedContent(
            LanguageCode targetLanguage,
            TextTranslationRequest.TextUnit textUnit,
            TextTranslationResult.Single result) {
        var content = textUnit.content();
        if (!isApplicable(result)) {
            LOGGER.debug(
                    "Translation failed or returned non-success for text unit [id={}], falling back to original"
                            + " content",
                    textUnit.id());
            return new TranslatedContent(content, null);
        }
        var translatedText = ((TextTranslationResult.Success) result).translatedText();
        LOGGER.debug("Successfully translated text unit [id={}]", textUnit.id());
        if (isMemorizable(translatedText)) {
            translationCache.save(targetLanguage, content, translatedText.translatedContent());
        } else {
            LOGGER.debug("Skip saving blank translation to memory for text unit [id={}]", textUnit.id());
        }
        return new TranslatedContent(content, translatedText);
    }

    private TranslatedTextBlock keepOriginalContent(DocumentTextBlock textBlock, LanguageCode targetLanguage) {
        return new TranslatedTextBlock(
                textBlock,
                new TextTranslationResult.TranslatedText(
                        UUID.randomUUID().toString(), textBlock.content(), textBlock.content(), targetLanguage));
    }

    private void applyTranslation(List<TranslatedTextBlock> translatedTextBlocks) {
        int count = 0;
        for (var item : translatedTextBlocks) {
            item.documentTextBlock().setContent(item.translatedText().translatedContent());
            count++;
        }
        LOGGER.debug("Applied translation to {} text blocks.", count);
    }

    private void closeDocument(D document) {
        if (document instanceof AutoCloseable autoCloseable) {
            try {
                autoCloseable.close();
                LOGGER.debug("AutoCloseable document closed successfully.");
            } catch (Exception e) {
                LOGGER.debug("Ignored exception while closing document", e);
            }
        }
    }

    protected Path buildTranslationPath(DocumentTranslationRequest.Single request) {
        var documentPath = request.documentSource().path();
        var targetCountryCode = request.targetLanguage().countryCode().toUpperCase();
        var fileNameNew = FileNameUtils.appendFilenameSuffix(
                documentPath.getFileName().toString(),
                "_" + targetCountryCode + "_" + Instant.now().getEpochSecond());
        var parentDir = documentPath.getParent();
        var path = parentDir != null ? parentDir.resolve(fileNameNew) : Path.of(fileNameNew);
        LOGGER.debug("Built translation file path: {}", path);
        return path;
    }

    record TranslatedTextBlock(
            DocumentTextBlock documentTextBlock, TextTranslationResult.TranslatedText translatedText) {}

    record TranslatedContent(String content, TextTranslationResult.TranslatedText translatedText) {}
}
