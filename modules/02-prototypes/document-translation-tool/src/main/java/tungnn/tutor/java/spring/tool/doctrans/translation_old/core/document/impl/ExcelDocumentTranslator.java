package tungnn.tutor.java.spring.tool.doctrans.translation_old.core.document.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import tungnn.tutor.java.document.adapter.excel.ExcelUtil;
import tungnn.tutor.java.document.core.DocumentSemanticUnitCollector;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.core.document.AbstractDocumentTranslator;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.core.document.DocumentTranslationRequest;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.core.text.orchestrator.TextTranslatorOrchestrator;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.shared.TextReference;

public class ExcelDocumentTranslator extends AbstractDocumentTranslator<Workbook> {

  public ExcelDocumentTranslator(TextTranslatorOrchestrator translatorOrchestrator) {
    super(translatorOrchestrator);
  }

  @Override
  protected Workbook openDocument(DocumentTranslationRequest request) {
    try {
      return ExcelUtil.readWorkbook(request.documentPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected List<TextReference> collectTextReferences(Workbook document) {
    var refs = new ArrayList<TextReference>();

    DocumentSemanticUnitCollector.collectCells(document).stream()
        .map(CellTextReference::new)
        .forEach(refs::add);

    DocumentSemanticUnitCollector.collectComments(document).stream()
        .map(CommentTextReference::new)
        .forEach(refs::add);

    DocumentSemanticUnitCollector.collectHeaders(document).stream()
        .map(HeaderTextReference::new)
        .forEach(refs::add);

    DocumentSemanticUnitCollector.collectFooters(document).stream()
        .map(FooterTextReference::new)
        .forEach(refs::add);

    DocumentSemanticUnitCollector.collectTextShapes(document).stream()
        .map(ShapeTextReference::new)
        .forEach(refs::add);

    return refs;
  }

  @Override
  protected void saveDocument(Workbook document, Path translationPath) {
    try {
      ExcelUtil.writeWorkbook(document, translationPath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private record CellTextReference(Cell cell) implements TextReference {

    @Override
    public String getId() {
      return cell.getSheet().getSheetName() + "!" + cell.getAddress().formatAsString();
    }

    @Override
    public String getText() {
      return cell.getStringCellValue();
    }

    @Override
    public void setText(String text) {
      cell.setCellValue(text);
    }
  }

  private record CommentTextReference(Comment comment) implements TextReference {

    @Override
    public String getId() {
      return "comment-" + System.identityHashCode(comment);
    }

    @Override
    public String getText() {
      return comment.getString().getString();
    }

    @Override
    public void setText(String text) {
      comment.setString(new XSSFRichTextString(text));
    }
  }

  private record HeaderTextReference(Header header) implements TextReference {

    @Override
    public String getId() {
      return "header-" + System.identityHashCode(header);
    }

    @Override
    public String getText() {
      return String.join("\n", header.getLeft(), header.getCenter(), header.getRight());
    }

    @Override
    public void setText(String text) {
      header.setCenter(text);
      header.setLeft("");
      header.setRight("");
    }
  }

  private record FooterTextReference(Footer footer) implements TextReference {

    @Override
    public String getId() {
      return "footer-" + System.identityHashCode(footer);
    }

    @Override
    public String getText() {
      return String.join("\n", footer.getLeft(), footer.getCenter(), footer.getRight());
    }

    @Override
    public void setText(String text) {
      footer.setCenter(text);
      footer.setLeft("");
      footer.setRight("");
    }
  }

  private record ShapeTextReference(XSSFSimpleShape shape) implements TextReference {

    @Override
    public String getId() {
      return "shape-" + System.identityHashCode(shape);
    }

    @Override
    public String getText() {
      return shape.getText();
    }

    @Override
    public void setText(String text) {
      shape.setText(text);
    }
  }
}
