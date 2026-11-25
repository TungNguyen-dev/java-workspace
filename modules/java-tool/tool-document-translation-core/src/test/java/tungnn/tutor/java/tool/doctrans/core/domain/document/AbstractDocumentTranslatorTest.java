package tungnn.tutor.java.tool.doctrans.core.domain.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchOverride;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchPolicy;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.cache.NoOpTranslationCache;

class AbstractDocumentTranslatorTest {

    @Test
    void shouldBuildTranslationPathCorrectly() {
        // Given
        TextTranslator textTranslator = mock(TextTranslator.class);
        StubDocumentTranslator translator = new StubDocumentTranslator(textTranslator);

        Path sourcePath = Path.of("documents/sample.docx");
        var request = new DocumentTranslationRequest.Single(
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
            public TextTranslationResult.Single translate(TextTranslationRequest.Single request) {
                throw new AssertionError("single translate must not be used");
            }

            @Override
            public TextTranslationResult.Batch translateBatch(TextTranslationRequest.Batch request) {
                batches.add(request.textUnits().stream()
                        .map(TextTranslationRequest.TextUnit::content)
                        .toList());
                return new TextTranslationResult.Batch(request.textUnits().stream()
                        .<TextTranslationResult.Single>map(
                                unit -> new TextTranslationResult.Success(new TextTranslationResult.TranslatedText(
                                        unit.id(), unit.content(), "vi:" + unit.content(), request.targetLanguage())))
                        .toList());
            }

            @Override
            public int maxConcurrency() {
                return 1;
            }
        };
        var blocks = List.of(block("a"), block("b"), block("a"), block("c"), block(" "));
        var documentTranslator = new BlockDocumentTranslator(translator, singleSegment(blocks));
        var request = new DocumentTranslationRequest.Single(
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
    void shouldUseConstructorOverrideForChunkSize() {
        var batches = new java.util.concurrent.CopyOnWriteArrayList<List<String>>();
        TextTranslator translator = new TextTranslator() {
            @Override
            public TextTranslationResult.Single translate(TextTranslationRequest.Single request) {
                throw new AssertionError("single translate must not be used");
            }

            @Override
            public TextTranslationResult.Batch translateBatch(TextTranslationRequest.Batch request) {
                batches.add(request.textUnits().stream()
                        .map(TextTranslationRequest.TextUnit::content)
                        .toList());
                return new TextTranslationResult.Batch(request.textUnits().stream()
                        .<TextTranslationResult.Single>map(
                                unit -> new TextTranslationResult.Success(new TextTranslationResult.TranslatedText(
                                        unit.id(), unit.content(), "vi:" + unit.content(), request.targetLanguage())))
                        .toList());
            }

            @Override
            public int maxConcurrency() {
                return 1;
            }
        };
        var blocks = List.of(block("a"), block("b"), block("c"));
        var documentTranslator = new BlockDocumentTranslator(
                translator, singleSegment(blocks), new TextTranslationBatchOverride(1, null));
        var request = new DocumentTranslationRequest.Single(
                new DocumentTranslationRequest.DocumentSource(Path.of("documents/sample.docx")),
                LanguageCode.VI,
                null,
                null);

        documentTranslator.translate(request);

        assertThat(batches).containsExactlyInAnyOrder(List.of("a"), List.of("b"), List.of("c"));
    }

    @Test
    void shouldKeepSegmentsTogetherWhenChunking() {
        var batches = new java.util.concurrent.CopyOnWriteArrayList<List<String>>();
        TextTranslator translator = new TextTranslator() {
            @Override
            public TextTranslationResult.Single translate(TextTranslationRequest.Single request) {
                throw new AssertionError("single translate must not be used");
            }

            @Override
            public TextTranslationResult.Batch translateBatch(TextTranslationRequest.Batch request) {
                batches.add(request.textUnits().stream()
                        .map(TextTranslationRequest.TextUnit::content)
                        .toList());
                return new TextTranslationResult.Batch(request.textUnits().stream()
                        .<TextTranslationResult.Single>map(
                                unit -> new TextTranslationResult.Success(new TextTranslationResult.TranslatedText(
                                        unit.id(), unit.content(), "vi:" + unit.content(), request.targetLanguage())))
                        .toList());
            }

            @Override
            public int maxConcurrency() {
                return 1;
            }
        };
        var segments = List.of(
                new DocumentSegment(List.of(block("a"))),
                new DocumentSegment(List.of(block("b"), block("c"))),
                new DocumentSegment(List.of(block("d"))));
        var documentTranslator = new BlockDocumentTranslator(translator, segments);
        var request = new DocumentTranslationRequest.Single(
                new DocumentTranslationRequest.DocumentSource(Path.of("documents/sample.docx")),
                LanguageCode.VI,
                null,
                null);

        documentTranslator.translate(request);

        assertThat(batches).containsExactlyInAnyOrder(List.of("a"), List.of("b", "c"), List.of("d"));
    }

    @Test
    void shouldKeepOriginalContentForFailedUnits() {
        TextTranslator translator = new TextTranslator() {
            @Override
            public TextTranslationResult.Single translate(TextTranslationRequest.Single request) {
                return new TextTranslationResult.Failure(new TextTranslationResult.Error("", "boom"));
            }
        };
        var blocks = List.of(block("a"), block("b"));
        var documentTranslator = new BlockDocumentTranslator(translator, singleSegment(blocks));
        var request = new DocumentTranslationRequest.Single(
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

    private static List<DocumentSegment> singleSegment(List<DocumentTextBlock> blocks) {
        return List.of(new DocumentSegment(blocks));
    }

    private static class BlockDocumentTranslator extends AbstractDocumentTranslator<Object> {
        private final List<DocumentSegment> segments;

        BlockDocumentTranslator(TextTranslator textTranslator, List<DocumentSegment> segments) {
            this(textTranslator, segments, TextTranslationBatchOverride.NONE);
        }

        BlockDocumentTranslator(
                TextTranslator textTranslator, List<DocumentSegment> segments, TextTranslationBatchOverride override) {
            super(textTranslator, NoOpTranslationCache.INSTANCE, override);
            this.segments = segments;
        }

        @Override
        protected TextTranslationBatchPolicy defaultBatchPolicy() {
            return new TextTranslationBatchPolicy(2, Integer.MAX_VALUE);
        }

        @Override
        protected Object openDocument(DocumentTranslationRequest.Single request) {
            return new Object();
        }

        @Override
        protected List<DocumentSegment> collectSegments(Object document) {
            return segments;
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
        protected TextTranslationBatchPolicy defaultBatchPolicy() {
            return TextTranslationBatchPolicy.SINGLE;
        }

        @Override
        protected Object openDocument(DocumentTranslationRequest.Single request) {
            return new Object();
        }

        @Override
        protected List<DocumentSegment> collectSegments(Object document) {
            return List.of();
        }

        @Override
        protected void saveDocument(Path translationPath, Object document) {
            // No-op for test stub
        }
    }
}
