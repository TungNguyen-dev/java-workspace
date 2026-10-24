package tungnn.tutor.java.tool.doctrans.core.domain.document;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.core.lib.io.filesystem.FileNameUtil;
import tungnn.tutor.java.core.lib.multithread.ConcurrentUtils;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationMemory;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.memory.NoOpTextTranslationMemory;

public abstract class AbstractDocumentTranslator<D> implements DocumentTranslator {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDocumentTranslator.class);

  protected final TextTranslator textTranslator;
  protected final TextTranslationMemory translationMemory;

  protected AbstractDocumentTranslator(TextTranslator textTranslator) {
    this(textTranslator, NoOpTextTranslationMemory.INSTANCE);
  }

  protected AbstractDocumentTranslator(
      TextTranslator textTranslator, TextTranslationMemory translationMemory) {
    this.textTranslator = Objects.requireNonNull(textTranslator, "textTranslator must not be null");
    this.translationMemory =
        Objects.requireNonNull(translationMemory, "translationMemory must not be null");
  }

  private static boolean isApplicable(TextTranslationResult result) {
    return result
            instanceof
            TextTranslationResult.Success(TextTranslationResult.TextTranslation translation)
        && translation != null
        && translation.translation() != null;
  }

  private static boolean isMemorizable(TextTranslationResult.TextTranslation translation) {
    return !translation.translation().isBlank();
  }

  private static boolean isBlank(String content) {
    return content == null || content.isBlank();
  }

  @Override
  public DocumentTranslationResult translate(DocumentTranslationRequest request) {
    LOGGER.info(
        "Starting translation request for document path: {}, target language: {}",
        request.documentSource().path(),
        request.targetLanguage());

    LOGGER.info("Opening document...");
    var document = openDocument(request);
    LOGGER.info("Document opened successfully.");
    try {
      LOGGER.info("Collecting text blocks from document...");
      var textBlocks = collectTextBlock(document);
      LOGGER.info("Collected text blocks number: {}", textBlocks.size());

      LOGGER.info("Starting translation for {} text blocks...", textBlocks.size());
      var textBlockTranslations = translateTextBlock(request, textBlocks);
      LOGGER.info("Completed translation for {} text blocks.", textBlockTranslations.size());

      LOGGER.info("Applying translations to document blocks...");
      applyTranslation(textBlockTranslations);
      LOGGER.info("Translations applied successfully.");

      var translationPath = buildTranslationPath(request);
      LOGGER.info("Saving translated document to path: {}", translationPath);
      saveDocument(translationPath, document);
      LOGGER.info("Document saved successfully.");

      return new DocumentTranslationResult.Success(
          new DocumentTranslationResult.DocumentTranslation(translationPath));
    } catch (Exception e) {
      LOGGER.info("Failed to translate document: {}", request.documentSource().path(), e);
      return new DocumentTranslationResult.Failure(
          new DocumentTranslationResult.Error("", e.getMessage()));
    } finally {
      LOGGER.info("Closing document...");
      closeDocument(document);
      LOGGER.info("Document closed.");
    }
  }

  protected abstract D openDocument(DocumentTranslationRequest request);

  protected abstract List<DocumentTextBlock> collectTextBlock(D document);

  protected abstract void saveDocument(Path translationPath, D document);

  private List<TextBlockTranslation> translateTextBlock(
      DocumentTranslationRequest request, List<DocumentTextBlock> textBlocks) {

    if (textBlocks.isEmpty()) {
      LOGGER.debug("Text blocks list is empty, skipping translation.");
      return List.of();
    }

    var uniqueContents = new LinkedHashSet<String>();
    for (var textBlock : textBlocks) {
      if (!isBlank(textBlock.content())) {
        uniqueContents.add(textBlock.content());
      }
    }
    LOGGER.debug(
        "Deduplicated {} text blocks into {} unique contents.",
        textBlocks.size(),
        uniqueContents.size());

    var contentTranslations =
        uniqueContents.isEmpty()
            ? List.<ContentTranslation>of()
            : ConcurrentUtils.executeConcurrently(
                new ArrayList<>(uniqueContents),
                content -> translateContent(request.targetLanguage(), content),
                5); // TODO: Update for dynamic maxConcurrency

    Map<String, TextTranslationResult.TextTranslation> translationByContent = new HashMap<>();
    for (var contentTranslation : contentTranslations) {
      translationByContent.put(contentTranslation.content(), contentTranslation.translation());
    }

    var textBlockTranslations = new ArrayList<TextBlockTranslation>(textBlocks.size());
    for (var textBlock : textBlocks) {
      var translation = translationByContent.get(textBlock.content());
      textBlockTranslations.add(
          translation != null
              ? new TextBlockTranslation(textBlock, translation)
              : keepOriginal(textBlock, request.targetLanguage()));
    }
    return textBlockTranslations;
  }

  private ContentTranslation translateContent(LanguageCode targetLanguage, String content) {
    var textUnit = new TextTranslationRequest.TextUnit(UUID.randomUUID().toString(), content);
    var memorized = translationMemory.find(targetLanguage, content);
    if (memorized.isPresent()) {
      LOGGER.debug("Translation memory hit for text unit [id={}]", textUnit.id());
      return new ContentTranslation(
          content,
          new TextTranslationResult.TextTranslation(
              textUnit.id(), content, memorized.get(), targetLanguage));
    }
    LOGGER.debug("Translation memory miss for text unit [id={}]", textUnit.id());

    var textTranslationRequest =
        new TextTranslationRequest.Standard(textUnit, targetLanguage, null, null);
    LOGGER.debug(
        "Translating text unit [id={}] with content length: {}", textUnit.id(), content.length());
    var textTranslationResult = textTranslator.translate(textTranslationRequest);

    if (!isApplicable(textTranslationResult)) {
      LOGGER.debug(
          "Translation failed or returned non-success for text unit [id={}], falling back to original content",
          textUnit.id());
      return new ContentTranslation(content, null);
    }

    var translation = ((TextTranslationResult.Success) textTranslationResult).translation();
    LOGGER.debug("Successfully translated text unit [id={}]", textUnit.id());

    if (isMemorizable(translation)) {
      translationMemory.save(targetLanguage, content, translation.translation());
    } else {
      LOGGER.debug("Skip saving blank translation to memory for text unit [id={}]", textUnit.id());
    }

    return new ContentTranslation(content, translation);
  }

  private TextBlockTranslation keepOriginal(
      DocumentTextBlock textBlock, LanguageCode targetLanguage) {
    return new TextBlockTranslation(
        textBlock,
        new TextTranslationResult.TextTranslation(
            UUID.randomUUID().toString(),
            textBlock.content(),
            textBlock.content(),
            targetLanguage));
  }

  private void applyTranslation(List<TextBlockTranslation> textBlockTranslations) {
    int count = 0;
    for (var item : textBlockTranslations) {
      item.documentTextBlock().setContent(item.translation().translation());
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

  protected Path buildTranslationPath(DocumentTranslationRequest request) {
    var documentPath = request.documentSource().path();
    var targetCountryCode = request.targetLanguage().countryCode().toUpperCase();

    var fileNameNew =
        FileNameUtil.appendFilenameSuffix(
            documentPath.getFileName().toString(),
            "_" + targetCountryCode + "_" + Instant.now().getEpochSecond());

    var parentDir = documentPath.getParent();
    var path = parentDir != null ? parentDir.resolve(fileNameNew) : Path.of(fileNameNew);
    LOGGER.debug("Built translation file path: {}", path);
    return path;
  }

  record TextBlockTranslation(
      DocumentTextBlock documentTextBlock, TextTranslationResult.TextTranslation translation) {}

  record ContentTranslation(String content, TextTranslationResult.TextTranslation translation) {}
}
