package tungnn.tutor.java.tool.doctrans.core.infra.translator.document;

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
import tungnn.tutor.java.tool.doctrans.core.domain.document.AbstractDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentSegment;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTextBlock;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchOverride;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchPolicy;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.cache.TranslationCache;

public class SlideShowDocumentTranslator extends AbstractDocumentTranslator<XMLSlideShow> {
    private static final TextTranslationBatchPolicy DEFAULT_BATCH_POLICY = new TextTranslationBatchPolicy(20, 3000);

    public SlideShowDocumentTranslator(TextTranslator textTranslator, TranslationCache textTranslationCache) {
        super(textTranslator, textTranslationCache);
    }

    public SlideShowDocumentTranslator(
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
    protected XMLSlideShow openDocument(DocumentTranslationRequest.Single request) {
        try {
            return new XMLSlideShow(
                    Files.newInputStream(request.documentSource().path()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to open PowerPoint document: " + e.getMessage(), e);
        }
    }

    @Override
    protected List<DocumentSegment> collectSegments(XMLSlideShow document) {
        // One segment per slide (notes, masters and layouts are their own sheets), in first-seen order.
        var textBlocksBySheet = new LinkedHashMap<Object, List<DocumentTextBlock>>();
        for (var shape : DocumentSemanticUnitCollector.collectTextShapes(document)) {
            textBlocksBySheet
                    .computeIfAbsent(shape.getSheet(), sheet -> new ArrayList<>())
                    .add(new TextShapeDocumentTextBlock(shape));
        }
        for (var cell : DocumentSemanticUnitCollector.collectTableCells(document)) {
            textBlocksBySheet
                    .computeIfAbsent(cell.getSheet(), sheet -> new ArrayList<>())
                    .add(new TableCellDocumentTextBlock(cell));
        }
        return textBlocksBySheet.values().stream().map(DocumentSegment::new).toList();
    }

    @Override
    protected void saveDocument(Path translationPath, XMLSlideShow document) {
        try (var out = Files.newOutputStream(translationPath)) {
            document.write(out);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save PowerPoint document: " + e.getMessage(), e);
        }
    }

    // --- Implementations of DocumentTextBlock ---
    private record TextShapeDocumentTextBlock(XSLFTextShape shape) implements DocumentTextBlock {
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

    private record TableCellDocumentTextBlock(XSLFTableCell cell) implements DocumentTextBlock {
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
