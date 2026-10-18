package tungnn.tutor.java.spring.tool.doctrans.app.service;

import tungnn.tutor.java.spring.tool.doctrans.app.model.TranslateDocumentRequest;
import tungnn.tutor.java.spring.tool.doctrans.app.model.TranslateDocumentResult;

public interface DocumentTranslationService {

  TranslateDocumentResult translateDocuments(TranslateDocumentRequest request);
}
