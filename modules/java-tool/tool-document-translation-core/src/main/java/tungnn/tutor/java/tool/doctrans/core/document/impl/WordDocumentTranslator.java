package tungnn.tutor.java.tool.doctrans.core.document.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import tungnn.tutor.java.infra.document.utils.DocumentSemanticUnitCollector;
import tungnn.tutor.java.tool.doctrans.core.cache.TranslationCache;
import tungnn.tutor.java.tool.doctrans.core.document.AbstractDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.document.DocumentText;
import tungnn.tutor.java.tool.doctrans.core.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.text.TextTranslator;

public class WordDocumentTranslator extends AbstractDocumentTranslator<XWPFDocument> {
    private static final TextTranslationRequest.BatchPolicy DEFAULT_BATCH_POLICY =
            new TextTranslationRequest.BatchPolicy(20, 3000);

    public WordDocumentTranslator(TextTranslator textTranslator, TranslationCache textTranslationCache) {
        super(textTranslator, textTranslationCache);
    }

    public WordDocumentTranslator(
            TextTranslator textTranslator,
            TranslationCache textTranslationCache,
            TextTranslationRequest.BatchOverride batchOverride) {
        super(textTranslator, textTranslationCache, batchOverride);
    }

    private static Object containerOf(XWPFParagraph paragraph) {
        var body = paragraph.getBody();
        return body instanceof XWPFTableCell cell ? cell.getTableRow() : body;
    }

    private static boolean isHeading(XWPFParagraph paragraph) {
        var styleId = paragraph.getStyleID();
        return styleId != null && styleId.regionMatches(true, 0, "Heading", 0, "Heading".length());
    }

    private static void flush(List<DocumentText.Segment> segments, List<DocumentText.Block> current) {
        if (!current.isEmpty()) {
            segments.add(new DocumentText.Segment(current));
            current.clear();
        }
    }

    @Override
    protected TextTranslationRequest.BatchPolicy defaultBatchPolicy() {
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
    protected List<DocumentText.Segment> collectSegments(XWPFDocument document) {
        var segments = new ArrayList<DocumentText.Segment>();
        var current = new ArrayList<DocumentText.Block>();
        // 1. Paragraphs: a segment ends when the container changes or a heading starts a new section
        Object currentContainer = null;
        for (var paragraph : DocumentSemanticUnitCollector.collectParagraphs(document)) {
            var container = containerOf(paragraph);
            if (!current.isEmpty() && (container != currentContainer || isHeading(paragraph))) {
                flush(segments, current);
            }
            current.add(new ParagraphBlock(paragraph));
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
            current.add(new TableCellBlock(cell));
            currentRow = row;
        }
        flush(segments, current);
        return segments;
    }

    @Override
    protected void saveDocument(Path translationPath, XWPFDocument document) {
        try (var out = Files.newOutputStream(translationPath)) {
            document.write(out);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save Word document: " + e.getMessage(), e);
        }
    }

    // --- Implementations of DocumentText.Block ---
    private record ParagraphBlock(XWPFParagraph paragraph) implements DocumentText.Block {
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

    private record TableCellBlock(XWPFTableCell tableCell) implements DocumentText.Block {
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
