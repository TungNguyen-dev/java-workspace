package tungnn.tutor.java.tool.doctrans.core.service;

import java.nio.file.Path;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationResult;

public interface DocumentTranslationService {
    Path translateToPath(Path documentPath, String targetLang);

    DocumentTranslationResult translate(Path documentPath, String targetLang);
}
