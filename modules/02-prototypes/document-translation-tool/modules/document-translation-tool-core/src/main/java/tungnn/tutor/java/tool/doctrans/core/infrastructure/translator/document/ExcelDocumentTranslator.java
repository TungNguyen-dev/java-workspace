package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import tungnn.tutor.java.infra.document.utils.DocumentSemanticUnitCollector;
import tungnn.tutor.java.tool.doctrans.core.domain.document.AbstractDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTextBlock;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationMemory;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;

public class ExcelDocumentTranslator extends AbstractDocumentTranslator<Workbook> {

  public ExcelDocumentTranslator(
      TextTranslator textTranslator, TextTranslationMemory textTranslationMemory) {
    super(textTranslator, textTranslationMemory);
  }

  @Override
  protected Workbook openDocument(DocumentTranslationRequest request) {
    try {
      return WorkbookFactory.create(request.documentSource().path().toFile());
    } catch (IOException e) {
      throw new RuntimeException("Lỗi khi mở tài liệu Excel: " + e.getMessage(), e);
    }
  }

  @Override
  protected List<DocumentTextBlock> collectTextBlock(Workbook document) {
    var textBlocks = new ArrayList<DocumentTextBlock>();

    // 1. Cell
    DocumentSemanticUnitCollector.collectCells(document).stream()
        .map(CellDocumentTextBlock::new)
        .forEach(textBlocks::add);

    // 2. Comment
    DocumentSemanticUnitCollector.collectComments(document).stream()
        .map(CommentDocumentTextBlock::new)
        .forEach(textBlocks::add);

    // 3. Header
    DocumentSemanticUnitCollector.collectHeaders(document).stream()
        .map(HeaderDocumentTextBlock::new)
        .forEach(textBlocks::add);

    // 4. Footer
    DocumentSemanticUnitCollector.collectFooters(document).stream()
        .map(FooterDocumentTextBlock::new)
        .forEach(textBlocks::add);

    // 5. Shape
    DocumentSemanticUnitCollector.collectTextShapes(document).stream()
        .map(ShapeDocumentTextBlock::new)
        .forEach(textBlocks::add);

    return textBlocks;
  }

  @Override
  protected void saveDocument(Path translationPath, Workbook document) {
    try (var out = java.nio.file.Files.newOutputStream(translationPath)) {
      document.write(out);
    } catch (IOException e) {
      throw new RuntimeException("Lỗi khi lưu tài liệu Excel: " + e.getMessage(), e);
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
