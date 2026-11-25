package tungnn.tutor.java.tool.doctrans.core.infra.translator.document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import tungnn.tutor.java.infra.document.utils.DocumentSemanticUnitCollector;
import tungnn.tutor.java.tool.doctrans.core.domain.document.AbstractDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentSegment;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTextBlock;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchOverride;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchPolicy;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.cache.TranslationCache;

public class WordDocumentTranslator extends AbstractDocumentTranslator<XWPFDocument> {
    private static final TextTranslationBatchPolicy DEFAULT_BATCH_POLICY = new TextTranslationBatchPolicy(20, 3000);

    public WordDocumentTranslator(TextTranslator textTranslator, TranslationCache textTranslationCache) {
        super(textTranslator, textTranslationCache);
    }

    public WordDocumentTranslator(
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
    protected XWPFDocument openDocument(DocumentTranslationRequest.Single request) {
        try {
            return new XWPFDocument(
                    Files.newInputStream(request.documentSource().path()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to open Word document: " + e.getMessage(), e);
        }
    }

    @Override
    protected List<DocumentSegment> collectSegments(XWPFDocument document) {
        var segments = new ArrayList<DocumentSegment>();
        var current = new ArrayList<DocumentTextBlock>();
        // 1. Paragraphs: a segment ends when the container changes or a heading starts a new section
        Object currentContainer = null;
        for (var paragraph : DocumentSemanticUnitCollector.collectParagraphs(document)) {
            var container = containerOf(paragraph);
            if (!current.isEmpty() && (container != currentContainer || isHeading(paragraph))) {
                flush(segments, current);
            }
            current.add(new ParagraphDocumentTextBlock(paragraph));
            currentContainer = container;
        }
        flush(segments, current);
        // 2. Table cells: one segment per table row
        Object currentRow = null;
        for (var cell : DocumentSemanticUnitCollector.collectTableCells(document)) {
            var row = cell.getTableRow();
            if (!current.isEmpty() && row != currentRow) {
                flush(segments, current);
            }
            current.add(new TableCellDocumentTextBlock(cell));
            currentRow = row;
        }
        flush(segments, current);
        return segments;
    }

    private static Object containerOf(XWPFParagraph paragraph) {
        var body = paragraph.getBody();
        return body instanceof XWPFTableCell cell ? cell.getTableRow() : body;
    }

    private static boolean isHeading(XWPFParagraph paragraph) {
        var styleId = paragraph.getStyleID();
        return styleId != null && styleId.regionMatches(true, 0, "Heading", 0, "Heading".length());
    }

    private static void flush(List<DocumentSegment> segments, List<DocumentTextBlock> current) {
        if (!current.isEmpty()) {
            segments.add(new DocumentSegment(current));
            current.clear();
        }
    }

    @Override
    protected void saveDocument(Path translationPath, XWPFDocument document) {
        try (var out = Files.newOutputStream(translationPath)) {
            document.write(out);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save Word document: " + e.getMessage(), e);
        }
    }

    // --- Implementations of DocumentTextBlock ---
    private record ParagraphDocumentTextBlock(XWPFParagraph paragraph) implements DocumentTextBlock {
        @Override
        public String content() {
            return paragraph.getText();
        }

        @Override
        public void setContent(String content) {
            var runs = paragraph.getRuns();
            if (runs != null && !runs.isEmpty()) {
                // Keep the first run to preserve formatting styles (font, color, etc.)
                runs.getFirst().setText(content, 0);
                // Remove subsequent runs to prevent old content duplication
                for (int i = runs.size() - 1; i > 0; i--) {
                    paragraph.removeRun(i);
                }
            } else {
                // If the paragraph has no existing runs, create a new one
                paragraph.createRun().setText(content);
            }
        }
    }

    private record TableCellDocumentTextBlock(XWPFTableCell tableCell) implements DocumentTextBlock {
        @Override
        public String content() {
            return tableCell.getText();
        }

        @Override
        public void setContent(String content) {
            if (!tableCell.getParagraphs().isEmpty()) {
                tableCell.removeParagraph(0);
            }
            XWPFParagraph paragraph = tableCell.addParagraph();
            paragraph.createRun().setText(content);
        }
    }
}
