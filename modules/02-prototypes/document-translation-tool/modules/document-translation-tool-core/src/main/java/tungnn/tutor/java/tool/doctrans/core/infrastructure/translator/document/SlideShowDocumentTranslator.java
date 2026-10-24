package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import tungnn.tutor.java.infra.document.utils.DocumentSemanticUnitCollector;
import tungnn.tutor.java.tool.doctrans.core.domain.document.AbstractDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTextBlock;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationMemory;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;

public class SlideShowDocumentTranslator extends AbstractDocumentTranslator<XMLSlideShow> {

  public SlideShowDocumentTranslator(
      TextTranslator textTranslator, TextTranslationMemory textTranslationMemory) {
    super(textTranslator, textTranslationMemory);
  }

  @Override
  protected XMLSlideShow openDocument(DocumentTranslationRequest request) {
    try {
      return new XMLSlideShow(Files.newInputStream(request.documentSource().path()));
    } catch (IOException e) {
      throw new RuntimeException("Lỗi khi mở tài liệu PowerPoint: " + e.getMessage(), e);
    }
  }

  @Override
  protected List<DocumentTextBlock> collectTextBlock(XMLSlideShow document) {
    var textBlocks = new ArrayList<DocumentTextBlock>();

    DocumentSemanticUnitCollector.collectTextShapes(document).stream()
        .map(TextShapeDocumentTextBlock::new)
        .forEach(textBlocks::add);

    DocumentSemanticUnitCollector.collectTableCells(document).stream()
        .map(TableCellDocumentTextBlock::new)
        .forEach(textBlocks::add);

    return textBlocks;
  }

  @Override
  protected void saveDocument(Path translationPath, XMLSlideShow document) {
    try (var out = Files.newOutputStream(translationPath)) {
      document.write(out);
    } catch (IOException e) {
      throw new RuntimeException("Lỗi khi lưu tài liệu PowerPoint: " + e.getMessage(), e);
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
