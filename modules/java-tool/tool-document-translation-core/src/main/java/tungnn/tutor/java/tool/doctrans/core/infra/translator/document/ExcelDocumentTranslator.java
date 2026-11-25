package tungnn.tutor.java.tool.doctrans.core.infra.translator.document;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import tungnn.tutor.java.infra.document.utils.DocumentSemanticUnitCollector;
import tungnn.tutor.java.tool.doctrans.core.domain.document.AbstractDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentSegment;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTextBlock;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchOverride;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchPolicy;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.cache.TranslationCache;

public class ExcelDocumentTranslator extends AbstractDocumentTranslator<Workbook> {
    private static final TextTranslationBatchPolicy DEFAULT_BATCH_POLICY = new TextTranslationBatchPolicy(20, 3000);

    public ExcelDocumentTranslator(TextTranslator textTranslator, TranslationCache textTranslationCache) {
        super(textTranslator, textTranslationCache);
    }

    public ExcelDocumentTranslator(
            TextTranslator textTranslator,
            TranslationCache textTranslationCache,
            TextTranslationBatchOverride batchOverride) {
        super(textTranslator, textTranslationCache, batchOverride);
    }

    @Override
    protected TextTranslationBatchPolicy defaultBatchPolicy() {
        return DEFAULT_BATCH_POLICY;
    }

    @Override
    protected Workbook openDocument(DocumentTranslationRequest.Single request) {
        try {
            return WorkbookFactory.create(request.documentSource().path().toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to open Excel document: " + e.getMessage(), e);
        }
    }

    @Override
    protected List<DocumentSegment> collectSegments(Workbook document) {
        var segments = new ArrayList<DocumentSegment>();
        // 1. Cell: the cells of one row form a segment
        var rowBlocks = new ArrayList<DocumentTextBlock>();
        Row currentRow = null;
        for (var cell : DocumentSemanticUnitCollector.collectCells(document)) {
            if (currentRow != null && cell.getRow() != currentRow) {
                addSegment(segments, rowBlocks);
                rowBlocks = new ArrayList<>();
            }
            currentRow = cell.getRow();
            rowBlocks.add(new CellDocumentTextBlock(cell));
        }
        addSegment(segments, rowBlocks);
        // 2. Comment
        addSegment(
                segments,
                DocumentSemanticUnitCollector.collectComments(document).stream()
                        .<DocumentTextBlock>map(CommentDocumentTextBlock::new)
                        .toList());
        // 3. Header and 4. Footer: page furniture of the sheets
        var furniture = new ArrayList<DocumentTextBlock>();
        DocumentSemanticUnitCollector.collectHeaders(document).stream()
                .map(HeaderDocumentTextBlock::new)
                .forEach(furniture::add);
        DocumentSemanticUnitCollector.collectFooters(document).stream()
                .map(FooterDocumentTextBlock::new)
                .forEach(furniture::add);
        addSegment(segments, furniture);
        // 5. Shape
        addSegment(
                segments,
                DocumentSemanticUnitCollector.collectTextShapes(document).stream()
                        .<DocumentTextBlock>map(ShapeDocumentTextBlock::new)
                        .toList());
        return segments;
    }

    private static void addSegment(List<DocumentSegment> segments, List<DocumentTextBlock> textBlocks) {
        if (!textBlocks.isEmpty()) {
            segments.add(new DocumentSegment(textBlocks));
        }
    }

    @Override
    protected void saveDocument(Path translationPath, Workbook document) {
        try (var out = java.nio.file.Files.newOutputStream(translationPath)) {
            document.write(out);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save Excel document: " + e.getMessage(), e);
        }
    }

    // --- Implementations of DocumentTextBlock ---

    private record CellDocumentTextBlock(Cell cell) implements DocumentTextBlock {
        private static final DataFormatter DATA_FORMATTER = new DataFormatter();

        @Override
        public String content() {
            return DATA_FORMATTER.formatCellValue(cell);
        }

        @Override
        public void setContent(String content) {
            cell.setCellValue(content);
        }
    }

    private record CommentDocumentTextBlock(Comment comment) implements DocumentTextBlock {
        @Override
        public String content() {
            return comment.getString() != null ? comment.getString().getString() : "";
        }

        @Override
        public void setContent(String content) {
            comment.setString(new XSSFRichTextString(content));
        }
    }

    private record HeaderDocumentTextBlock(Header header) implements DocumentTextBlock {
        @Override
        public String content() {
            return String.join(
                    "\n",
                    header.getLeft() != null ? header.getLeft() : "",
                    header.getCenter() != null ? header.getCenter() : "",
                    header.getRight() != null ? header.getRight() : "");
        }

        @Override
        public void setContent(String content) {
            header.setCenter(content);
            header.setLeft("");
            header.setRight("");
        }
    }

    private record FooterDocumentTextBlock(Footer footer) implements DocumentTextBlock {
        @Override
        public String content() {
            return String.join(
                    "\n",
                    footer.getLeft() != null ? footer.getLeft() : "",
                    footer.getCenter() != null ? footer.getCenter() : "",
                    footer.getRight() != null ? footer.getRight() : "");
        }

        @Override
        public void setContent(String content) {
            footer.setCenter(content);
            footer.setLeft("");
            footer.setRight("");
        }
    }

    private record ShapeDocumentTextBlock(XSSFSimpleShape shape) implements DocumentTextBlock {
        @Override
        public String content() {
            return shape.getText();
        }

        @Override
        public void setContent(String content) {
            shape.setText(content);
        }
    }
}
