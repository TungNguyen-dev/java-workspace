package tungnn.tutor.java.tool.doctrans.core.service;

import java.nio.file.Path;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationResult;

public interface DocumentTranslationService {

  Path translate(Path documentPath, String targetLang);

  DocumentTranslationResult translateDocument(Path documentPath, String targetLang);
}
