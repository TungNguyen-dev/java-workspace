package tungnn.tutor.java.tool.doctrans.core.document;

import java.util.List;

public interface DocumentTranslator {

    DocumentTranslationResult translate(DocumentTranslationRequest.Single request);

    default List<DocumentTranslationResult> translateBatch(DocumentTranslationRequest.Batch request) {
        return request.documentSources().stream()
                .map(documentSource -> translate(request.single(documentSource)))
                .toList();
    }
}
