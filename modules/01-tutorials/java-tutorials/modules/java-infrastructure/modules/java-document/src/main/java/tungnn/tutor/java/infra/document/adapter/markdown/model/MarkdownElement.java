package tungnn.tutor.java.infra.document.adapter.markdown.model;

import java.util.List;

public sealed interface MarkdownElement
    permits MarkdownBlock, MarkdownBlock.ListItem, MarkdownElement.MarkdownDocument {

  String toMarkdown();

  record MarkdownDocument(List<MarkdownBlock> blocks) implements MarkdownElement {
    public MarkdownDocument {
      blocks = blocks != null ? List.copyOf(blocks) : List.of();
    }

    @Override
    public String toMarkdown() {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < blocks.size(); i++) {
        sb.append(blocks.get(i).toMarkdown());
        if (i < blocks.size() - 1) {
          sb.append("\n");
        }
      }
      return sb.toString().trim();
    }
  }
}
