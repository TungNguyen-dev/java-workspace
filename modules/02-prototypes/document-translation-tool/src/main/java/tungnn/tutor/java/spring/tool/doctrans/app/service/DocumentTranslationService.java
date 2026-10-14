package tungnn.tutor.java.spring.tool.doctrans.app.service;

import java.io.InputStream;
import java.io.OutputStream;
import tungnn.tutor.java.spring.tool.doctrans.shared.LanguageCode;

public interface DocumentTranslationService {

  void translate(InputStream inputStream, OutputStream outputStream, LanguageCode targetLanguage);
}
