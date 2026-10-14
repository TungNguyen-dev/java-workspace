package tungnn.tutor.java.spring.tool.doctrans.translation_old.core.document;

import java.nio.file.Path;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.shared.LanguageCode;

public record DocumentTranslationRequest(Path documentPath, LanguageCode targetLanguage) {}
