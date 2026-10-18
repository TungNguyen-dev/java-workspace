package tungnn.tutor.java.tool.doctrans.core.domain.document;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.core.lib.io.filesystem.FileNameUtil;
import tungnn.tutor.java.core.lib.multithread.ConcurrentUtils;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;

public abstract class AbstractDocumentTranslator<D> implements DocumentTranslator {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDocumentTranslator.class);

  protected final TextTranslator textTranslator;

  protected AbstractDocumentTranslator(TextTranslator textTranslator) {
    this.textTranslator = Objects.requireNonNull(textTranslator, "textTranslator must not be null");
  }

  @Override
  public DocumentTranslationResult translate(DocumentTranslationRequest request) {
    LOGGER.info(
        "Starting translation request for document path: {}, target language: {}",
        request.documentSource().path(),
        request.targetLanguage());

    // 1. Open document D
    LOGGER.info("Opening document...");
    var document = openDocument(request);
    LOGGER.info("Document opened successfully.");

    try {
      // 2. Collect list of TextBlock
      LOGGER.info("Collecting text blocks from document...");
      var textBlocks = collectTextBlock(document);
      LOGGER.info("Collected text blocks number: {}", textBlocks.size());

      // 3. Translate TextBlocks
      LOGGER.info("Starting translation for {} text blocks...", textBlocks.size());
      var textBlockTranslations = translateTextBlock(request, textBlocks);
      LOGGER.info("Completed translation for {} text blocks.", textBlockTranslations.size());

      // 4. Apply translation
      LOGGER.info("Applying translations to document blocks...");
      applyTranslation(textBlockTranslations);
      LOGGER.info("Translations applied successfully.");

      // 5. Save document
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
      // 6. Close document D
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

    return ConcurrentUtils.executeConcurrently(
        textBlocks, textBlock -> translateSingleBlock(request, textBlock));
  }

  private TextBlockTranslation translateSingleBlock(
      DocumentTranslationRequest request, DocumentTextBlock textBlock) {

    var textUnit =
        new TextTranslationRequest.TextUnit(UUID.randomUUID().toString(), textBlock.content());
    var textTranslationRequest =
        new TextTranslationRequest.Standard(textUnit, request.targetLanguage(), null, null);

    LOGGER.debug(
        "Translating single text block [id={}] with content length: {}",
        textUnit.id(),
        textBlock.content() != null ? textBlock.content().length() : 0);

    var textTranslationResult = textTranslator.translate(textTranslationRequest);

    if (textTranslationResult
        instanceof
        TextTranslationResult.Success(TextTranslationResult.TextTranslation translation)) {
      LOGGER.debug("Successfully translated text block [id={}]", textUnit.id());
      return new TextBlockTranslation(textBlock, translation);
    } else {
      LOGGER.debug(
          "Translation failed or returned non-success for text block [id={}], falling back to original content",
          textUnit.id());
      // Non-fail-fast: If translation fails, fallback to keep the original content
      return new TextBlockTranslation(
          textBlock,
          new TextTranslationResult.TextTranslation(
              "", textBlock.content(), textBlock.content(), request.targetLanguage()));
    }
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
}
