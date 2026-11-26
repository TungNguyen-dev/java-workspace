package tungnn.tutor.java.tool.doctrans.core.document.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import tungnn.tutor.java.infra.document.utils.DocumentSemanticUnitCollector;
import tungnn.tutor.java.tool.doctrans.core.cache.TranslationCache;
import tungnn.tutor.java.tool.doctrans.core.document.AbstractDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.document.DocumentText;
import tungnn.tutor.java.tool.doctrans.core.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.text.TextTranslator;

public class ExcelDocumentTranslator extends AbstractDocumentTranslator<Workbook> {
    private static final TextTranslationRequest.BatchPolicy DEFAULT_BATCH_POLICY =
            new TextTranslationRequest.BatchPolicy(20, 3000);

    public ExcelDocumentTranslator(TextTranslator textTranslator, TranslationCache textTranslationCache) {
        super(textTranslator, textTranslationCache);
    }

    public ExcelDocumentTranslator(
            TextTranslator textTranslator,
            TranslationCache textTranslationCache,
            TextTranslationRequest.BatchOverride batchOverride) {
        super(textTranslator, textTranslationCache, batchOverride);
    }

    private static void addSegment(List<DocumentText.Segment> segments, List<DocumentText.Block> textBlocks) {
        if (!textBlocks.isEmpty()) {
            segments.add(new DocumentText.Segment(textBlocks));
        }
    }

    @Override
    protected TextTranslationRequest.BatchPolicy defaultBatchPolicy() {
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
    protected List<DocumentText.Segment> collectSegments(Workbook document) {
        var segments = new ArrayList<DocumentText.Segment>();
        // 1. Cell: the cells of one row form a segment
        var rowBlocks = new ArrayList<DocumentText.Block>();
        Row currentRow = null;
        for (var cell : DocumentSemanticUnitCollector.collectCells(document)) {
            if (currentRow != null && cell.getRow() != currentRow) {
                addSegment(segments, rowBlocks);
                rowBlocks = new ArrayList<>();
            }
            currentRow = cell.getRow();
            rowBlocks.add(new CellBlock(cell));
        }
        addSegment(segments, rowBlocks);
        // 2. Comment
        addSegment(
                segments,
                DocumentSemanticUnitCollector.collectComments(document).stream()
                        .<DocumentText.Block>map(CommentBlock::new)
                        .toList());
        // 3. Header and 4. Footer: page furniture of the sheets
        var furniture = new ArrayList<DocumentText.Block>();
        DocumentSemanticUnitCollector.collectHeaders(document).stream()
                .map(HeaderBlock::new)
                .forEach(furniture::add);
        DocumentSemanticUnitCollector.collectFooters(document).stream()
                .map(FooterBlock::new)
                .forEach(furniture::add);
        addSegment(segments, furniture);
        // 5. Shape
        addSegment(
                segments,
                DocumentSemanticUnitCollector.collectTextShapes(document).stream()
                        .<DocumentText.Block>map(ShapeBlock::new)
                        .toList());
        return segments;
    }

    @Override
    protected void saveDocument(Path translationPath, Workbook document) {
        try (var out = java.nio.file.Files.newOutputStream(translationPath)) {
            document.write(out);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save Excel document: " + e.getMessage(), e);
        }
    }

    // --- Implementations of DocumentText.Block ---

    private record CellBlock(Cell cell) implements DocumentText.Block {
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

    private record CommentBlock(Comment comment) implements DocumentText.Block {
        @Override
        public String content() {
            return comment.getString() != null ? comment.getString().getString() : "";
        }

        @Override
        public void setContent(String content) {
            comment.setString(new XSSFRichTextString(content));
        }
    }

    private record HeaderBlock(Header header) implements DocumentText.Block {
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

    private record FooterBlock(Footer footer) implements DocumentText.Block {
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

    private record ShapeBlock(XSSFSimpleShape shape) implements DocumentText.Block {
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
