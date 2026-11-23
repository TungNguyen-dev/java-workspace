package tungnn.tutor.java.infra.document.adapter.markdown.model;

import java.util.List;

public sealed interface MarkdownBlock extends MarkdownElement
    permits MarkdownBlock.Heading,
        MarkdownBlock.Paragraph,
        MarkdownBlock.CodeBlock,
        MarkdownBlock.OrderedList {

  record Heading(int level, String text) implements MarkdownBlock {
    @Override
    public String toMarkdown() {
      return "#".repeat(Math.clamp(level, 1, 6)) + " " + text + "\n";
    }
  }

  record Paragraph(String text) implements MarkdownBlock {
    @Override
    public String toMarkdown() {
      return text + "\n";
    }
  }

  record CodeBlock(String language, String code) implements MarkdownBlock {
    @Override
    public String toMarkdown() {
      String lang = (language != null) ? language : "";
      String body = (code != null) ? code : "";
      int longest = 0;
      for (int i = 0, run = 0; i < body.length(); i++) {
        run = body.charAt(i) == '`' ? run + 1 : 0;
        longest = Math.max(longest, run);
      }
      String fence = "`".repeat(Math.max(3, longest + 1));
      return fence + lang + "\n" + body + "\n" + fence + "\n";
    }
  }

  record ListItem(List<MarkdownBlock> blocks) implements MarkdownElement {
    public ListItem {
      blocks = blocks != null ? List.copyOf(blocks) : List.of();
    }

    @Override
    public String toMarkdown() {
      StringBuilder sb = new StringBuilder();
      for (MarkdownBlock block : blocks) {
        sb.append(block.toMarkdown());
      }
      return sb.toString();
    }
  }

  record OrderedList(int startNumber, List<ListItem> items) implements MarkdownBlock {
    public OrderedList(List<ListItem> items) {
      this(1, items);
    }

    public OrderedList {
      items = items != null ? List.copyOf(items) : List.of();
    }

    @Override
    public String toMarkdown() {
      if (items.isEmpty()) {
        return "";
      }
      StringBuilder sb = new StringBuilder();
      int current = startNumber;
      for (ListItem item : items) {
        String itemContent = item.toMarkdown().trim();
        String[] lines = itemContent.split("\\R");
        if (lines.length > 0 && !lines[0].isBlank()) {
          sb.append(current++).append(". ").append(lines[0]).append("\n");
          for (int i = 1; i < lines.length; i++) {
            sb.append("   ").append(lines[i]).append("\n");
          }
        } else {
          sb.append(current++).append(".\n");
        }
      }
      return sb.toString();
    }
  }
}
