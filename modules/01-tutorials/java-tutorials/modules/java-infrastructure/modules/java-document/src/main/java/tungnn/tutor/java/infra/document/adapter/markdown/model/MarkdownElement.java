package tungnn.tutor.java.infra.document.adapter.markdown.model;

import java.util.ArrayList;
import java.util.List;

public sealed interface MarkdownElement permits MarkdownBlock, MarkdownElement.MarkdownDocument {

  String toMarkdown();

  record MarkdownDocument(List<MarkdownBlock> blocks) implements MarkdownElement {

    public MarkdownDocument(List<MarkdownBlock> blocks) {
      this.blocks = blocks != null ? new ArrayList<>(blocks) : new ArrayList<>();
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
