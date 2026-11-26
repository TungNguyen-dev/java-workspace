package tungnn.tutor.java.tool.doctrans.service;

import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.infra.document.ingestion.TikaFormatDetectorUtils;
import tungnn.tutor.java.tool.doctrans.core.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.document.DocumentTranslationResult;
import tungnn.tutor.java.tool.doctrans.core.document.impl.ExcelDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.document.impl.MarkdownDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.document.impl.SlideShowDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.document.impl.WordDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;

public class DefaultDocumentTranslationService implements DocumentTranslationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDocumentTranslationService.class);
    private final WordDocumentTranslator wordDocumentTranslator;
    private final ExcelDocumentTranslator excelDocumentTranslator;
    private final SlideShowDocumentTranslator slideShowDocumentTranslator;
    private final MarkdownDocumentTranslator markdownDocumentTranslator;

    public DefaultDocumentTranslationService(
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
    public DocumentTranslationResult translate(Path documentPath, String targetLang) {
        validateInputs(documentPath, targetLang);
        var mediaType = TikaFormatDetectorUtils.detectMediaType(documentPath);
        var mimeType = TikaFormatDetectorUtils.findExtensionByMediaType(mediaType)
                .orElseThrow(() ->
                        new IllegalArgumentException("Unsupported or unknown file format for path: " + documentPath));
        var request = new DocumentTranslationRequest.Single(
                new DocumentTranslationRequest.DocumentSource(documentPath),
                LanguageCode.valueOf(targetLang.toUpperCase()),
                null,
                null);
        return switch (mimeType.toLowerCase()) {
            case "xlsx" -> excelDocumentTranslator.translate(request);
            case "pptx" -> slideShowDocumentTranslator.translate(request);
            case "docx" -> wordDocumentTranslator.translate(request);
            case "md", "txt" -> markdownDocumentTranslator.translate(request);
            default -> throw new IllegalArgumentException("Unknown or unsupported mime type: " + mimeType);
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
                        "Document translated successfully. Output path: {}",
                        success.translatedDocument().path());
                yield success.translatedDocument().path();
            }
            case DocumentTranslationResult.Failure failure -> {
                var err = failure.error();
                LOGGER.error("Document translation failed. Code: [{}], Message: [{}]", err.code(), err.message());
                yield null;
            }
        };
    }
}
