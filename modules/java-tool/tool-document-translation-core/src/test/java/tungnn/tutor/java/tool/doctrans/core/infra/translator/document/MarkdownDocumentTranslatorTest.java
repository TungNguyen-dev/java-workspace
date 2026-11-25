package tungnn.tutor.java.tool.doctrans.core.infra.translator.document;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentSegment;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTextBlock;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.cache.NoOpTranslationCache;
import tungnn.tutor.java.tool.doctrans.core.infra.translator.text.DummyTextTranslator;

class MarkdownDocumentTranslatorTest {

    @TempDir
    Path tempDir;

    private static List<List<String>> contents(List<DocumentSegment> segments) {
        return segments.stream()
                .map(segment -> segment.textBlocks().stream()
                        .map(DocumentTextBlock::content)
                        .toList())
                .toList();
    }

    @Test
    void shouldStartNewSegmentAtEachHeading() throws IOException {
        var file = tempDir.resolve("sample.md");
        Files.writeString(
                file, "Intro paragraph\n\n# First\n\nOne\n\nTwo\n\n## Second\n\nThree\n", StandardCharsets.UTF_8);
        var translator = new MarkdownDocumentTranslator(new DummyTextTranslator(), NoOpTranslationCache.INSTANCE);
        var request = new DocumentTranslationRequest.Single(
                new DocumentTranslationRequest.DocumentSource(file), LanguageCode.VI, null, null);

        var document = translator.openDocument(request);
        var segments = translator.collectSegments(document);

        assertThat(contents(segments))
                .containsExactly(
                        List.of("Intro paragraph"), List.of("First", "One", "Two"), List.of("Second", "Three"));
    }
}
