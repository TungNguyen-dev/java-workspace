package tungnn.tutor.java.spring.tool.doctrans.translation_old.core.document.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import tungnn.tutor.java.document.adapter.word.WordUtil;
import tungnn.tutor.java.document.core.DocumentSemanticUnitCollector;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.core.document.AbstractDocumentTranslator;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.core.document.DocumentTranslationRequest;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.core.text.orchestrator.TextTranslatorOrchestrator;
import tungnn.tutor.java.spring.tool.doctrans.translation_old.shared.TextReference;

public class WordDocumentTranslator extends AbstractDocumentTranslator<XWPFDocument> {

  public WordDocumentTranslator(TextTranslatorOrchestrator translatorOrchestrator) {
    super(translatorOrchestrator);
  }

  @Override
  protected XWPFDocument openDocument(DocumentTranslationRequest request) {
    try {
      return WordUtil.readDocument(Files.newInputStream(request.documentPath()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected List<TextReference> collectTextReferences(XWPFDocument document) {
    List<TextReference> refs = new ArrayList<>();

    DocumentSemanticUnitCollector.collectParagraphs(document).stream()
        .map(XWPFParagraphTextReference::new)
        .forEach(refs::add);

    DocumentSemanticUnitCollector.collectTableCells(document).stream()
        .map(XWPFTableCellTextReference::new)
        .forEach(refs::add);

    return refs;
  }

  @Override
  protected void saveDocument(XWPFDocument document, Path translationPath) {
    try {
      WordUtil.writeDocument(document, Files.newOutputStream(translationPath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private record XWPFParagraphTextReference(XWPFParagraph paragraph) implements TextReference {
    @Override
    public String getId() {
      return "paragraph-" + System.identityHashCode(paragraph);
    }

    @Override
    public String getText() {
      return paragraph.getText();
    }

    @Override
    public void setText(String text) {
      while (!paragraph.getRuns().isEmpty()) {
        paragraph.removeRun(0);
      }

      paragraph.createRun().setText(text);
    }
  }

  private record XWPFTableCellTextReference(XWPFTableCell tableCell) implements TextReference {
    @Override
    public String getId() {
      return "cell-" + System.identityHashCode(tableCell);
    }

    @Override
    public String getText() {
      return tableCell.getText();
    }

    @Override
    public void setText(String text) {
      tableCell.removeParagraph(0);

      XWPFParagraph paragraph = tableCell.addParagraph();
      paragraph.createRun().setText(text);
    }
  }
}
