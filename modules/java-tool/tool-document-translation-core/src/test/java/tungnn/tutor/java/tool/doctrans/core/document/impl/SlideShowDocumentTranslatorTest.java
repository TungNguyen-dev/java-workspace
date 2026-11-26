package tungnn.tutor.java.tool.doctrans.core.document.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.doctrans.core.cache.NoOpTranslationCache;
import tungnn.tutor.java.tool.doctrans.core.document.DocumentText;
import tungnn.tutor.java.tool.doctrans.core.text.impl.DummyTextTranslator;

class SlideShowDocumentTranslatorTest {

    private static List<List<String>> contents(List<DocumentText.Segment> segments) {
        return segments.stream()
                .map(segment -> segment.blocks().stream()
                        .map(DocumentText.Block::content)
                        .toList())
                .toList();
    }

    @Test
    void shouldGroupShapesBySlide() throws Exception {
        try (var slideShow = new XMLSlideShow()) {
            var first = slideShow.createSlide();
            first.createTextBox().setText("title one");
            first.createTextBox().setText("body one");
            var second = slideShow.createSlide();
            second.createTextBox().setText("title two");
            var translator = new SlideShowDocumentTranslator(new DummyTextTranslator(), NoOpTranslationCache.INSTANCE);

            var segments = translator.collectSegments(slideShow);

            assertThat(contents(segments).subList(0, 2))
                    .containsExactly(List.of("title one", "body one"), List.of("title two"));
        }
    }
}
