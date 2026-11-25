package tungnn.tutor.java.tool.doctrans.core.domain.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchPolicy;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;

class AbstractDocumentTranslatorTest {

    @Test
    void shouldBuildTranslationPathCorrectly() {
        // Given
        TextTranslator textTranslator = mock(TextTranslator.class);
        StubDocumentTranslator translator = new StubDocumentTranslator(textTranslator);

        Path sourcePath = Path.of("documents/sample.docx");
        var request = new DocumentTranslationRequest.Standard(
                new DocumentTranslationRequest.DocumentSource(sourcePath), LanguageCode.VI, null, null);

        // When
        Path translationPath = translator.buildTranslationPath(request);

        // Then
        assertThat(translationPath).isNotNull();
        assertThat(translationPath.getParent()).isEqualTo(sourcePath.getParent());
        assertThat(translationPath.getFileName().toString()).startsWith("sample_VN_");
        assertThat(translationPath.getFileName().toString()).endsWith(".docx");
    }

    @Test
    void shouldSendDeduplicatedUnitsAsChunkedBatches() {
        var batches = new java.util.concurrent.CopyOnWriteArrayList<List<String>>();
        TextTranslator translator = new TextTranslator() {
            @Override
            public TextTranslationResult translate(TextTranslationRequest request) {
                throw new AssertionError("single translate must not be used");
            }

            @Override
            public List<TextTranslationResult> translateBatch(TextTranslationBatchRequest request) {
                batches.add(request.textUnits().stream()
                        .map(TextTranslationRequest.TextUnit::content)
                        .toList());
                return request.textUnits().stream()
                        .<TextTranslationResult>map(
                                unit -> new TextTranslationResult.Success(new TextTranslationResult.TextTranslation(
                                        unit.id(), unit.content(), "vi:" + unit.content(), request.targetLanguage())))
                        .toList();
            }

            @Override
            public TextTranslationBatchPolicy batchPolicy() {
                return new TextTranslationBatchPolicy(2, Integer.MAX_VALUE);
            }

            @Override
            public int maxConcurrency() {
                return 1;
            }
        };
        var blocks = List.of(block("a"), block("b"), block("a"), block("c"), block(" "));
        var documentTranslator = new BlockDocumentTranslator(translator, blocks);
        var request = new DocumentTranslationRequest.Standard(
                new DocumentTranslationRequest.DocumentSource(Path.of("documents/sample.docx")),
                LanguageCode.VI,
                null,
                null);

        var result = documentTranslator.translate(request);

        assertThat(result).isInstanceOf(DocumentTranslationResult.Success.class);
        assertThat(batches).containsExactlyInAnyOrder(List.of("a", "b"), List.of("c"));
        assertThat(blocks.stream().map(DocumentTextBlock::content))
                .containsExactly("vi:a", "vi:b", "vi:a", "vi:c", " ");
    }

    @Test
    void shouldKeepOriginalContentForFailedUnits() {
        TextTranslator translator = new TextTranslator() {
            @Override
            public TextTranslationResult translate(TextTranslationRequest request) {
                return new TextTranslationResult.Failure(new TextTranslationResult.Error("", "boom"));
            }
        };
        var blocks = List.of(block("a"), block("b"));
        var documentTranslator = new BlockDocumentTranslator(translator, blocks);
        var request = new DocumentTranslationRequest.Standard(
                new DocumentTranslationRequest.DocumentSource(Path.of("documents/sample.docx")),
                LanguageCode.VI,
                null,
                null);

        documentTranslator.translate(request);

        assertThat(blocks.stream().map(DocumentTextBlock::content)).containsExactly("a", "b");
    }

    private static DocumentTextBlock block(String content) {
        return new DocumentTextBlock() {
            private String value = content;

            @Override
            public String content() {
                return value;
            }

            @Override
            public void setContent(String content) {
                this.value = content;
            }
        };
    }

    private static class BlockDocumentTranslator extends AbstractDocumentTranslator<Object> {
        private final List<DocumentTextBlock> blocks;

        BlockDocumentTranslator(TextTranslator textTranslator, List<DocumentTextBlock> blocks) {
            super(textTranslator);
            this.blocks = blocks;
        }

        @Override
        protected Object openDocument(DocumentTranslationRequest request) {
            return new Object();
        }

        @Override
        protected List<DocumentTextBlock> collectTextBlock(Object document) {
            return blocks;
        }

        @Override
        protected void saveDocument(Path translationPath, Object document) {}
    }

    // --- Stub implementation for testing abstract class ---
    private static class StubDocumentTranslator extends AbstractDocumentTranslator<Object> {
        protected StubDocumentTranslator(TextTranslator textTranslator) {
            super(textTranslator);
        }

        @Override
        protected Object openDocument(DocumentTranslationRequest request) {
            return new Object();
        }

        @Override
        protected List<DocumentTextBlock> collectTextBlock(Object document) {
            return List.of();
        }

        @Override
        protected void saveDocument(Path translationPath, Object document) {
            // No-op for test stub
        }
    }
}
