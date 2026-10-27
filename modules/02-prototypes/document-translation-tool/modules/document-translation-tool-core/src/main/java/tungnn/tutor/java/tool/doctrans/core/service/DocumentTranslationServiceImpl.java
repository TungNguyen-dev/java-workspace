package tungnn.tutor.java.tool.doctrans.core.service;

import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.infra.document.ingestion.TikaFormatDetectorUtil;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationResult;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.ExcelDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.MarkdownDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.SlideShowDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.WordDocumentTranslator;

public class DocumentTranslationServiceImpl implements DocumentTranslationService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DocumentTranslationServiceImpl.class);

  private final WordDocumentTranslator wordDocumentTranslator;
  private final ExcelDocumentTranslator excelDocumentTranslator;
  private final SlideShowDocumentTranslator slideShowDocumentTranslator;
  private final MarkdownDocumentTranslator markdownDocumentTranslator;

  public DocumentTranslationServiceImpl(
      WordDocumentTranslator wordDocumentTranslator,
      ExcelDocumentTranslator excelDocumentTranslator,
      SlideShowDocumentTranslator slideShowDocumentTranslator,
      MarkdownDocumentTranslator markdownDocumentTranslator) {

    this.wordDocumentTranslator = wordDocumentTranslator;
    this.excelDocumentTranslator = excelDocumentTranslator;
    this.slideShowDocumentTranslator = slideShowDocumentTranslator;
    this.markdownDocumentTranslator = markdownDocumentTranslator;
  }

  @Override
  public Path translate(Path documentPath, String targetLang) {
    return handleTranslationResult(translateDocument(documentPath, targetLang));
  }

  @Override
  public DocumentTranslationResult translateDocument(Path documentPath, String targetLang) {
    validateInputs(documentPath, targetLang);

    var mediaType = TikaFormatDetectorUtil.detectMediaType(documentPath);
    var mimeType =
        TikaFormatDetectorUtil.findExtensionByMediaType(mediaType)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Unsupported or unknown file format for path: " + documentPath));

    var request =
        new DocumentTranslationRequest.Standard(
            new DocumentTranslationRequest.DocumentSource(documentPath),
            LanguageCode.valueOf(targetLang.toUpperCase()),
            null,
            null);

    return switch (mimeType.toLowerCase()) {
      case "xlsx" -> excelDocumentTranslator.translate(request);
      case "pptx" -> slideShowDocumentTranslator.translate(request);
      case "docx" -> wordDocumentTranslator.translate(request);
      case "md", "txt" -> markdownDocumentTranslator.translate(request);
      default ->
          throw new IllegalArgumentException("Unknown or unsupported mime type: " + mimeType);
    };
  }

  private void validateInputs(Path documentPath, String targetLang) {
    if (documentPath == null) {
      throw new IllegalArgumentException("Document path must not be null");
    }
    if (targetLang == null || targetLang.isBlank()) {
      throw new IllegalArgumentException("Target language must not be null or blank");
    }
  }

  private Path handleTranslationResult(DocumentTranslationResult result) {
    return switch (result) {
      case DocumentTranslationResult.Success success -> {
        LOGGER.info(
            "Document translated successfully. Output path: {}", success.translation().path());
        yield success.translation().path();
      }
      case DocumentTranslationResult.Failure failure -> {
        var err = failure.error();
        LOGGER.error(
            "Document translation failed. Code: [{}], Message: [{}]", err.code(), err.message());
        yield null;
      }
    };
  }
}
