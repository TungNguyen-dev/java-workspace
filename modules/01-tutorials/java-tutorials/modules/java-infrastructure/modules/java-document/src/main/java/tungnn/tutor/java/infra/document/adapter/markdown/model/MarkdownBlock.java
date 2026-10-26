package tungnn.tutor.java.infra.document.adapter.markdown.model;

public sealed interface MarkdownBlock extends MarkdownElement
    permits MarkdownBlock.Heading, MarkdownBlock.Paragraph, MarkdownBlock.CodeBlock {

  record Heading(int level, String text) implements MarkdownBlock {

    @Override
    public String toMarkdown() {
      return "#".repeat(Math.clamp(level, 1, 6)) + " " + text;
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
      return "```" + lang + "\n" + code + "\n```\n";
    }
  }
}
