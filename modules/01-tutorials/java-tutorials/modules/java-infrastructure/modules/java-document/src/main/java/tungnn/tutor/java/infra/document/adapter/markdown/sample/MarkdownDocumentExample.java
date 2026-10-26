package tungnn.tutor.java.infra.document.adapter.markdown.sample;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.infra.document.adapter.markdown.model.*;

public class MarkdownDocumentExample {

  static void main() throws IOException {
    Path samplePath = Path.of("storage", "sample.md");

    // 1. Create a new Markdown document using a POI-style model
    MarkdownElement.MarkdownDocument doc =
        new MarkdownElement.MarkdownDocument(
            List.of(
                new MarkdownBlock.Heading(1, "Project Overview Report"),
                new MarkdownBlock.Paragraph(
                    "This document is automatically generated via the MarkdownDocument Model."),
                new MarkdownBlock.Heading(2, "Sample Source Code"),
                new MarkdownBlock.CodeBlock(
                    "java",
                    "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello Markdown POI\");\n    }\n}")));

    // 2. Write the document to file
    MarkdownDocumentUtils.writeDocument(doc, samplePath);
    System.out.println("Markdown file successfully written to: " + samplePath.toAbsolutePath());

    // 3. Read back the written file and iterate through its blocks
    MarkdownElement.MarkdownDocument parsedDoc = MarkdownDocumentUtils.readDocument(samplePath);
    System.out.println("\n--- Reading content from parsed file ---");
    for (MarkdownBlock block : parsedDoc.blocks()) {
      System.out.println("Block type: " + block.getClass().getSimpleName());
      System.out.println("Content:\n" + block.toMarkdown());
      System.out.println("-".repeat(80));
    }
  }
}
