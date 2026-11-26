package tungnn.tutor.java.tool.doctrans.core.document;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;

public sealed interface DocumentTranslationRequest
        permits DocumentTranslationRequest.Single, DocumentTranslationRequest.Batch {

    LanguageCode targetLanguage();

    TranslationContext translationContext();

    TranslationGlossary translationGlossary();

    record DocumentSource(Path path) {}

    record TranslationContext() {}

    record TranslationGlossary() {}

    record Single(
            DocumentSource documentSource,
            LanguageCode targetLanguage,
            TranslationContext translationContext,
            TranslationGlossary translationGlossary)
            implements DocumentTranslationRequest {}

    record Batch(
            List<DocumentSource> documentSources,
            LanguageCode targetLanguage,
            TranslationContext translationContext,
            TranslationGlossary translationGlossary)
            implements DocumentTranslationRequest {

        public Batch {
            Objects.requireNonNull(documentSources, "documentSources must not be null");
            documentSources = List.copyOf(documentSources);
        }

        public Single single(DocumentSource documentSource) {
            return new Single(documentSource, targetLanguage, translationContext, translationGlossary);
        }
    }
}
