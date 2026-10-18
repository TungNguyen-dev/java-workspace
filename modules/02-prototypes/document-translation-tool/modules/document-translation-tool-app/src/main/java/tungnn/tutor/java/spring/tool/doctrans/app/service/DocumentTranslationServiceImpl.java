package tungnn.tutor.java.spring.tool.doctrans.app.service;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tungnn.tutor.java.infra.document.ingestion.TikaFormatDetectorUtil;
import tungnn.tutor.java.spring.tool.doctrans.app.config.DocTransConfig;
import tungnn.tutor.java.spring.tool.doctrans.app.model.TranslateDocumentRequest;
import tungnn.tutor.java.spring.tool.doctrans.app.model.TranslateDocumentResult;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.ExcelDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.SlideShowDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.WordDocumentTranslator;

@Service
public class DocumentTranslationServiceImpl implements DocumentTranslationService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DocumentTranslationServiceImpl.class);

  private final ExcelDocumentTranslator excelDocumentTranslator;
  private final SlideShowDocumentTranslator slideShowDocumentTranslator;
  private final WordDocumentTranslator wordDocumentTranslator;
  private final DocTransConfig docTransConfig;

  public DocumentTranslationServiceImpl(
      ExcelDocumentTranslator excelDocumentTranslator,
      SlideShowDocumentTranslator slideShowDocumentTranslator,
      WordDocumentTranslator wordDocumentTranslator,
      DocTransConfig docTransConfig) {
    this.excelDocumentTranslator = excelDocumentTranslator;
    this.slideShowDocumentTranslator = slideShowDocumentTranslator;
    this.wordDocumentTranslator = wordDocumentTranslator;
    this.docTransConfig = docTransConfig;
  }

  @Override
  public TranslateDocumentResult translateDocuments(TranslateDocumentRequest request) {
    if (request == null || request.documents() == null || request.documents().isEmpty()) {
      return new TranslateDocumentResult(Set.of());
    }

    Set<TranslateDocumentResult.DocumentResult> documentResults = new HashSet<>();
    String targetLang =
        request.targetLanguageCode() != null && !request.targetLanguageCode().isBlank()
            ? request.targetLanguageCode()
            : docTransConfig.targetLanguage();

    // Duyệt và dịch tuần tự từng tài liệu một
    for (var doc : request.documents()) {
      try {
        LOGGER.info("Bắt đầu dịch tài liệu: {}", doc.relativePath());
        executeTranslate(doc.path(), docTransConfig.outputDir(), targetLang);
        documentResults.add(
            new TranslateDocumentResult.DocumentResult(doc.relativePath(), true, null));
        LOGGER.info("Dịch thành công tài liệu: {}", doc.relativePath());
      } catch (Exception e) {
        LOGGER.error("Lỗi khi dịch tài liệu {}: {}", doc.relativePath(), e.getMessage(), e);
        documentResults.add(
            new TranslateDocumentResult.DocumentResult(doc.relativePath(), false, e));
      }
    }

    return new TranslateDocumentResult(documentResults);
  }

  private void executeTranslate(Path source, Path outputDir, String targetLanguageCode) {
    var mediaType = TikaFormatDetectorUtil.detectMediaType(source);
    var mimeType = TikaFormatDetectorUtil.findExtensionByMediaType(mediaType).orElseThrow();

    var req =
        new DocumentTranslationRequest.Standard(
            new DocumentTranslationRequest.DocumentSource(source),
            LanguageCode.valueOf(targetLanguageCode.toUpperCase()),
            null,
            null);

    switch (mimeType) {
      case "xlsx" -> excelDocumentTranslator.translate(req);
      case "pptx" -> slideShowDocumentTranslator.translate(req);
      case "docx" -> wordDocumentTranslator.translate(req);
      default -> throw new IllegalArgumentException("Unknown mime type: " + mimeType);
    }
  }
}
