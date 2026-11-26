package tungnn.tutor.java.tool.doctrans.core.document.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import tungnn.tutor.java.infra.document.utils.DocumentSemanticUnitCollector;
import tungnn.tutor.java.tool.doctrans.core.cache.TranslationCache;
import tungnn.tutor.java.tool.doctrans.core.document.AbstractDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.document.DocumentText;
import tungnn.tutor.java.tool.doctrans.core.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.text.TextTranslator;

public class SlideShowDocumentTranslator extends AbstractDocumentTranslator<XMLSlideShow> {
    private static final TextTranslationRequest.BatchPolicy DEFAULT_BATCH_POLICY =
            new TextTranslationRequest.BatchPolicy(20, 3000);

    public SlideShowDocumentTranslator(TextTranslator textTranslator, TranslationCache textTranslationCache) {
        super(textTranslator, textTranslationCache);
    }

    public SlideShowDocumentTranslator(
            TextTranslator textTranslator,
            TranslationCache textTranslationCache,
            TextTranslationRequest.BatchOverride batchOverride) {
        super(textTranslator, textTranslationCache, batchOverride);
    }

    @Override
    protected TextTranslationRequest.BatchPolicy defaultBatchPolicy() {
        return DEFAULT_BATCH_POLICY;
    }

    @Override
    protected XMLSlideShow openDocument(DocumentTranslationRequest.Single request) {
        try {
            return new XMLSlideShow(
                    Files.newInputStream(request.documentSource().path()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to open PowerPoint document: " + e.getMessage(), e);
        }
    }

    @Override
    protected List<DocumentText.Segment> collectSegments(XMLSlideShow document) {
        // One segment per slide (notes, masters and layouts are their own sheets), in first-seen order.
        var textBlocksBySheet = new LinkedHashMap<Object, List<DocumentText.Block>>();
        for (var shape : DocumentSemanticUnitCollector.collectTextShapes(document)) {
            textBlocksBySheet
                    .computeIfAbsent(shape.getSheet(), sheet -> new ArrayList<>())
                    .add(new TextShapeBlock(shape));
        }
        for (var cell : DocumentSemanticUnitCollector.collectTableCells(document)) {
            textBlocksBySheet
                    .computeIfAbsent(cell.getSheet(), sheet -> new ArrayList<>())
                    .add(new TableCellBlock(cell));
        }
        return textBlocksBySheet.values().stream()
                .map(DocumentText.Segment::new)
                .toList();
    }

    @Override
    protected void saveDocument(Path translationPath, XMLSlideShow document) {
        try (var out = Files.newOutputStream(translationPath)) {
            document.write(out);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save PowerPoint document: " + e.getMessage(), e);
        }
    }

    // --- Implementations of DocumentText.Block ---
    private record TextShapeBlock(XSLFTextShape shape) implements DocumentText.Block {
        @Override
        public String content() {
            return shape.getText();
        }

        @Override
        public void setContent(String content) {
            shape.clearText();
            shape.setText(content);
        }
    }

    private record TableCellBlock(XSLFTableCell cell) implements DocumentText.Block {
        @Override
        public String content() {
            return cell.getText();
        }

        @Override
        public void setContent(String content) {
            cell.clearText();
            cell.setText(content);
        }
    }
}
