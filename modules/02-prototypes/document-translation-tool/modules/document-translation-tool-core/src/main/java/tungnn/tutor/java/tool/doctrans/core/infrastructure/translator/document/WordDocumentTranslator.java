package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document;

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
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTextBlock;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationMemory;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;

public class WordDocumentTranslator extends AbstractDocumentTranslator<XWPFDocument> {

  public WordDocumentTranslator(
      TextTranslator textTranslator, TextTranslationMemory textTranslationMemory) {
    super(textTranslator, textTranslationMemory);
  }

  @Override
  protected XWPFDocument openDocument(DocumentTranslationRequest request) {
    try {
      return new XWPFDocument(Files.newInputStream(request.documentSource().path()));
    } catch (IOException e) {
      throw new RuntimeException("Lỗi khi mở tài liệu Word: " + e.getMessage(), e);
    }
  }

  @Override
  protected List<DocumentTextBlock> collectTextBlock(XWPFDocument document) {
    List<DocumentTextBlock> textBlocks = new ArrayList<>();

    // 1. Paragraphs
    DocumentSemanticUnitCollector.collectParagraphs(document).stream()
        .map(ParagraphDocumentTextBlock::new)
        .forEach(textBlocks::add);

    // 2. Table Cells
    DocumentSemanticUnitCollector.collectTableCells(document).stream()
        .map(TableCellDocumentTextBlock::new)
        .forEach(textBlocks::add);

    return textBlocks;
  }

  @Override
  protected void saveDocument(Path translationPath, XWPFDocument document) {
    try (var out = Files.newOutputStream(translationPath)) {
      document.write(out);
    } catch (IOException e) {
      throw new RuntimeException("Lỗi khi lưu tài liệu Word: " + e.getMessage(), e);
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
