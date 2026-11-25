package tungnn.tutor.java.tool.doctrans.core.infra.translator.document;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentSegment;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTextBlock;
import tungnn.tutor.java.tool.doctrans.core.domain.cache.NoOpTranslationCache;
import tungnn.tutor.java.tool.doctrans.core.infra.translator.text.DummyTextTranslator;

class ExcelDocumentTranslatorTest {

    private static List<List<String>> contents(List<DocumentSegment> segments) {
        return segments.stream()
                .map(segment -> segment.textBlocks().stream()
                        .map(DocumentTextBlock::content)
                        .toList())
                .toList();
    }

    @Test
    void shouldGroupCellsByRowAndCommentsTogether() throws Exception {
        try (var workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("data");
            var firstRow = sheet.createRow(0);
            firstRow.createCell(0).setCellValue("a1");
            firstRow.createCell(1).setCellValue("b1");
            var secondRow = sheet.createRow(1);
            var commented = secondRow.createCell(0);
            commented.setCellValue("a2");
            var drawing = sheet.createDrawingPatriarch();
            var comment = drawing.createCellComment(workbook.getCreationHelper().createClientAnchor());
            comment.setString(workbook.getCreationHelper().createRichTextString("note"));
            commented.setCellComment(comment);
            var translator = new ExcelDocumentTranslator(new DummyTextTranslator(), NoOpTranslationCache.INSTANCE);

            var segments = translator.collectSegments(workbook);

            assertThat(contents(segments)).containsExactly(List.of("a1", "b1"), List.of("a2"), List.of("note"));
        }
    }
}
