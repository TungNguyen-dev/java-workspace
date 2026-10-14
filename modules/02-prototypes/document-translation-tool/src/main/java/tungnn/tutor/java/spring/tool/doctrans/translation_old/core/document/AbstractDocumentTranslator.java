package tungnn.tutor.java.spring.tool.doctrans.translation_old.core.document;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import tungnn.tutor.java.core.lib.io.filesystem.FileNameUtil;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.core.text.orchestrator.TextTranslatorOrchestrator;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.core.text.orchestrator.TranslationContext;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.shared.TextReference;

public abstract class AbstractDocumentTranslator<D extends AutoCloseable>
    implements DocumentTranslator {

  private final TextTranslatorOrchestrator translatorOrchestrator;

  protected AbstractDocumentTranslator(TextTranslatorOrchestrator translatorOrchestrator) {
    this.translatorOrchestrator = translatorOrchestrator;
  }

  @Override
  public DocumentTranslationResponse translate(DocumentTranslationRequest request) {
    D document = openDocument(request);
    try {
      var textReferences = refineTextReferences(collectTextReferences(document));
      var translationContext = new TranslationContext(request.targetLanguage());
      var translationResult = translatorOrchestrator.translate(textReferences, translationContext);

      translationResult
          .translations()
          .forEach(
              translation -> translation.textReference().setText(translation.translatedText()));

      var translationPath = buildTranslationPath(request);
      saveDocument(document, translationPath);

      return buildResponse(translationPath);
    } finally {
      closeDocument(document);
    }
  }

  protected abstract D openDocument(DocumentTranslationRequest request);

  protected abstract List<TextReference> collectTextReferences(D document);

  protected abstract void saveDocument(D document, Path translationPath);

  protected List<TextReference> refineTextReferences(List<TextReference> textReferences) {
    return textReferences.stream()
        .filter(textReference -> !textReference.getText().isBlank())
        .toList();
  }

  protected Path buildTranslationPath(DocumentTranslationRequest request) {
    var documentPath = request.documentPath();
    var targetCountryCode = request.targetLanguage().getCountryCode().toUpperCase();
    var fileName = documentPath.getFileName().toString();
    var fileNameNew =
        FileNameUtil.appendFilenameSuffix(
            fileName, "_" + targetCountryCode + "_" + Instant.now().getEpochSecond());
    return documentPath.getParent().resolve(fileNameNew);
  }

  protected void closeDocument(D document) {
    if (document != null) {
      try {
        document.close();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  protected DocumentTranslationResponse buildResponse(Path translationPath) {
    return new DocumentTranslationResponse(translationPath);
  }
}
