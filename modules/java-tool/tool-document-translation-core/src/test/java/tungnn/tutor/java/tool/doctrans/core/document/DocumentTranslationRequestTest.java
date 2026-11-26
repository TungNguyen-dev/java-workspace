package tungnn.tutor.java.tool.doctrans.core.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;

class DocumentTranslationRequestTest {

    private static DocumentTranslationRequest.DocumentSource source(String name) {
        return new DocumentTranslationRequest.DocumentSource(Path.of("documents", name));
    }

    @Test
    void singleShouldKeepSharedFieldsOfBatch() {
        var context = new DocumentTranslationRequest.TranslationContext();
        var glossary = new DocumentTranslationRequest.TranslationGlossary();
        var source = source("a.docx");
        var batch = new DocumentTranslationRequest.Batch(List.of(source), LanguageCode.VI, context, glossary);

        var single = batch.single(source);

        assertThat(single.documentSource()).isEqualTo(source);
        assertThat(single.targetLanguage()).isEqualTo(LanguageCode.VI);
        assertThat(single.translationContext()).isSameAs(context);
        assertThat(single.translationGlossary()).isSameAs(glossary);
    }

    @Test
    void batchShouldCopyDocumentSourcesDefensively() {
        var sources = new ArrayList<DocumentTranslationRequest.DocumentSource>();
        sources.add(source("a.docx"));
        var batch = new DocumentTranslationRequest.Batch(sources, LanguageCode.VI, null, null);

        sources.add(source("b.docx"));

        assertThat(batch.documentSources()).hasSize(1);
    }

    @Test
    void batchShouldRejectNullDocumentSources() {
        assertThatThrownBy(() -> new DocumentTranslationRequest.Batch(null, LanguageCode.VI, null, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void defaultTranslateBatchShouldReturnOneResultPerSourceInOrder() {
        var translated = new ArrayList<Path>();
        DocumentTranslator translator = request -> {
            translated.add(request.documentSource().path());
            return new DocumentTranslationResult.Success(new DocumentTranslationResult.TranslatedDocument(
                    request.documentSource().path()));
        };
        var batch = new DocumentTranslationRequest.Batch(
                List.of(source("a.docx"), source("b.docx")), LanguageCode.VI, null, null);

        var results = translator.translateBatch(batch);

        assertThat(results).hasSize(2);
        assertThat(translated).containsExactly(Path.of("documents", "a.docx"), Path.of("documents", "b.docx"));
        assertThat(((DocumentTranslationResult.Success) results.get(1))
                        .translatedDocument()
                        .path())
                .isEqualTo(Path.of("documents", "b.docx"));
    }
}
