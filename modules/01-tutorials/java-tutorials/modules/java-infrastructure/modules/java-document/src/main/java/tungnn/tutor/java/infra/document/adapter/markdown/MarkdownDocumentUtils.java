package tungnn.tutor.java.infra.document.adapter.markdown;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import tungnn.tutor.java.infra.document.adapter.markdown.model.*;

public final class MarkdownDocumentUtils {

  private MarkdownDocumentUtils() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  /** Read a Markdown file from the given path and parse it into a MarkdownDocument DOM. */
  public static MarkdownElement.MarkdownDocument readDocument(Path path) throws IOException {
    String content = Files.readString(path);
    return parse(content);
  }

  /** Convert a Markdown string content into a MarkdownDocument. */
  public static MarkdownElement.MarkdownDocument parse(String markdownContent) {
    if (markdownContent == null) {
      return new MarkdownElement.MarkdownDocument(List.of());
    }

    MutableDataSet options = new MutableDataSet();
    Parser parser = Parser.builder(options).build();
    Node documentNode = parser.parse(markdownContent);

    List<MarkdownBlock> blocks = new ArrayList<>();
    for (Node child : documentNode.getChildren()) {
      MarkdownBlock block = convertNodeToBlock(child);
      if (block != null) {
        blocks.add(block);
      }
    }

    return new MarkdownElement.MarkdownDocument(blocks);
  }

  /** Write a MarkdownDocument to a target file path. */
  public static void writeDocument(MarkdownElement.MarkdownDocument document, Path targetPath)
      throws IOException {
    String content = document.toMarkdown();
    Files.writeString(targetPath, content);
  }

  private static MarkdownBlock convertNodeToBlock(Node node) {
    if (node instanceof Heading headingNode) {
      return new MarkdownBlock.Heading(headingNode.getLevel(), headingNode.getText().toString());
    } else if (node instanceof Paragraph paragraphNode) {
      return new MarkdownBlock.Paragraph(paragraphNode.getContentChars().toString().trim());
    } else if (node instanceof FencedCodeBlock codeNode) {
      return new MarkdownBlock.CodeBlock(
          codeNode.getInfo().toString().trim(), codeNode.getContentChars().toString().trim());
    }
    // Additional Node types (List, Table, Blockquote) can be extended here if needed
    return null;
  }

  /**
   * Convert an HTML string to Markdown. - Remove empty anchor tags <a name="..."></a> and attribute
   * strings like {#...} - Unwrap internal backlinks (.html or relative links), keeping only the
   * inner text.
   *
   * @param htmlString Input HTML string
   * @return Cleaned Markdown string
   */
  public static String convertHtmlToMarkdown(String htmlString) {
    if (htmlString == null || htmlString.isBlank()) {
      return "";
    }

    // STEP 1: Preprocess HTML using Jsoup to clean unwanted link tags
    Document doc = Jsoup.parseBodyFragment(htmlString);

    // 1.1. Remove empty <a name="..."></a> tags
    for (Element a : doc.select("a[name]")) {
      if (a.text().isBlank() && a.children().isEmpty()) {
        a.remove();
      } else {
        a.removeAttr("name");
      }
    }

    // 1.2. Unwrap internal links (.html or lacking http/https protocol)
    for (Element a : doc.select("a[href]")) {
      String href = a.attr("href");
      if (href.endsWith(".html") || !href.contains("://")) {
        a.unwrap();
      }
    }

    // STEP 2: Configure Flexmark Converter
    MutableDataSet options = new MutableDataSet();

    // Disable generation of ID/Name attributes like {#id} in Markdown output
    options.set(FlexmarkHtmlConverter.OUTPUT_ATTRIBUTES_ID, false);
    options.set(FlexmarkHtmlConverter.SKIP_ATTRIBUTES, true);

    // STEP 3: Convert cleaned HTML to Markdown
    return FlexmarkHtmlConverter.builder(options).build().convert(doc.body().html()).trim();
  }
}
