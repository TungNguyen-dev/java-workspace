package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement;
import tungnn.tutor.java.tool.doctrans.core.domain.document.AbstractDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTextBlock;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationMemory;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;

public class MarkdownDocumentTranslator
    extends AbstractDocumentTranslator<MarkdownElement.MarkdownDocument> {

  private final Map<MarkdownBlock, DocumentTextBlock> blockMapping = new IdentityHashMap<>();

  public MarkdownDocumentTranslator(
      TextTranslator textTranslator, TextTranslationMemory textTranslationMemory) {
    super(textTranslator, textTranslationMemory);
  }

  @Override
  protected MarkdownElement.MarkdownDocument openDocument(DocumentTranslationRequest request) {
    try {
      blockMapping.clear();
      return MarkdownDocumentUtils.readDocument(request.documentSource().path());
    } catch (IOException e) {
      throw new RuntimeException("Failed to open Markdown document: " + e.getMessage(), e);
    }
  }

  @Override
  protected List<DocumentTextBlock> collectTextBlock(MarkdownElement.MarkdownDocument document) {
    var textBlocks = new ArrayList<DocumentTextBlock>();
    for (var block : document.blocks()) {
      DocumentTextBlock textBlock;
      if (block instanceof MarkdownBlock.Heading heading) {
        textBlock = new HeadingTextBlock(heading);
        textBlocks.add(textBlock);
        blockMapping.put(heading, textBlock);
      } else if (block instanceof MarkdownBlock.Paragraph paragraph) {
        textBlock = new ParagraphTextBlock(paragraph);
        textBlocks.add(textBlock);
        blockMapping.put(paragraph, textBlock);
      }
      // CodeBlock and other elements are preserved intact (source code is not translated)
    }
    return textBlocks;
  }

  @Override
  protected void saveDocument(Path translationPath, MarkdownElement.MarkdownDocument document) {
    List<MarkdownBlock> translatedBlocks = new ArrayList<>();
    for (var block : document.blocks()) {
      var textBlock = blockMapping.get(block);
      if (textBlock != null) {
        if (block instanceof MarkdownBlock.Heading h) {
          translatedBlocks.add(new MarkdownBlock.Heading(h.level(), textBlock.content()));
        } else if (block instanceof MarkdownBlock.Paragraph p) {
          translatedBlocks.add(new MarkdownBlock.Paragraph(textBlock.content()));
        } else {
          translatedBlocks.add(block);
        }
      } else {
        translatedBlocks.add(block);
      }
    }

    var translatedDoc = new MarkdownElement.MarkdownDocument(translatedBlocks);
    try {
      MarkdownDocumentUtils.writeDocument(translatedDoc, translationPath);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save Markdown document: " + e.getMessage(), e);
    } finally {
      blockMapping.clear();
    }
  }

  // --- Implementations of DocumentTextBlock ---

  private static class HeadingTextBlock implements DocumentTextBlock {
    private final MarkdownBlock.Heading heading;
    private String content;

    public HeadingTextBlock(MarkdownBlock.Heading heading) {
      this.heading = heading;
      this.content = heading.text();
    }

    @Override
    public String content() {
      return content;
    }

    @Override
    public void setContent(String content) {
      this.content = content;
    }
  }

  private static class ParagraphTextBlock implements DocumentTextBlock {
    private final MarkdownBlock.Paragraph paragraph;
    private String content;

    public ParagraphTextBlock(MarkdownBlock.Paragraph paragraph) {
      this.paragraph = paragraph;
      this.content = paragraph.text();
    }

    @Override
    public String content() {
      return content;
    }

    @Override
    public void setContent(String content) {
      this.content = content;
    }
  }
}
