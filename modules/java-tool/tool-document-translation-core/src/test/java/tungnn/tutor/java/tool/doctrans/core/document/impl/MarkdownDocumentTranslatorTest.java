package tungnn.tutor.java.tool.doctrans.core.document.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.tool.doctrans.core.cache.NoOpTranslationCache;
import tungnn.tutor.java.tool.doctrans.core.document.DocumentText;
import tungnn.tutor.java.tool.doctrans.core.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.text.impl.DummyTextTranslator;

class MarkdownDocumentTranslatorTest {

    @TempDir
    Path tempDir;

    private static List<List<String>> contents(List<DocumentText.Segment> segments) {
        return segments.stream()
                .map(segment -> segment.blocks().stream()
                        .map(DocumentText.Block::content)
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
