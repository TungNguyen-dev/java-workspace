package tungnn.tutor.java.tool.doctrans.service;

import java.nio.file.Path;
import tungnn.tutor.java.tool.doctrans.core.document.DocumentTranslationResult;

public interface DocumentTranslationService {

    DocumentTranslationResult translate(Path documentPath, String targetLang);
}
