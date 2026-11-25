package tungnn.tutor.java.tool.doctrans.core.infra.translator.document;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentSegment;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTextBlock;
import tungnn.tutor.java.tool.doctrans.core.domain.cache.NoOpTranslationCache;
import tungnn.tutor.java.tool.doctrans.core.infra.translator.text.DummyTextTranslator;

class SlideShowDocumentTranslatorTest {

    private static List<List<String>> contents(List<DocumentSegment> segments) {
        return segments.stream()
                .map(segment -> segment.textBlocks().stream()
                        .map(DocumentTextBlock::content)
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
