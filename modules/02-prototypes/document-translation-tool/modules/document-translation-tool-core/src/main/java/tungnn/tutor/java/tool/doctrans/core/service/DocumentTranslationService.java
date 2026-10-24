package tungnn.tutor.java.tool.doctrans.core.service;

import java.nio.file.Path;

public interface DocumentTranslationService {

  Path translate(Path documentPath, String targetLang);
}
