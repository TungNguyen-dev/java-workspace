package tungnn.tutor.java.spring.tool.doctrans.translation_old.service;

import java.nio.file.Path;

public interface TranslationService {

  Path translateDocument(Path sourcePath, String languageCode);
}
