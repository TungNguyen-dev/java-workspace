package tungnn.tutor.java.tool.doctrans.core.infra.translator.document;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentSegment;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTextBlock;
import tungnn.tutor.java.tool.doctrans.core.domain.cache.NoOpTranslationCache;
import tungnn.tutor.java.tool.doctrans.core.infra.translator.text.DummyTextTranslator;

class WordDocumentTranslatorTest {

    private static List<List<String>> contents(List<DocumentSegment> segments) {
        return segments.stream()
                .map(segment -> segment.textBlocks().stream()
                        .map(DocumentTextBlock::content)
                        .toList())
                .toList();
    }

    @Test
    void shouldCutAtHeadingsAndGroupTableCellsByRow() throws Exception {
        try (var document = new XWPFDocument()) {
            document.createParagraph().createRun().setText("intro");
            var heading = document.createParagraph();
            heading.setStyle("Heading1");
            heading.createRun().setText("chapter");
            document.createParagraph().createRun().setText("body");
            var table = document.createTable(2, 2);
            table.getRow(0).getCell(0).setText("r1c1");
            table.getRow(0).getCell(1).setText("r1c2");
            table.getRow(1).getCell(0).setText("r2c1");
            table.getRow(1).getCell(1).setText("r2c2");
            var translator = new WordDocumentTranslator(new DummyTextTranslator(), NoOpTranslationCache.INSTANCE);

            var segments = contents(translator.collectSegments(document));

            assertThat(segments)
                    .contains(
                            List.of("intro"),
                            List.of("chapter", "body"),
                            List.of("r1c1", "r1c2"),
                            List.of("r2c1", "r2c2"));
            assertThat(segments.indexOf(List.of("intro"))).isLessThan(segments.indexOf(List.of("chapter", "body")));
        }
    }
}
