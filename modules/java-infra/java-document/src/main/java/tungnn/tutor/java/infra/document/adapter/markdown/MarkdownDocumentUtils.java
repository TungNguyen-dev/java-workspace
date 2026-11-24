package tungnn.tutor.java.infra.document.adapter.markdown;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.IndentedCodeBlock;
import com.vladsch.flexmark.ast.ListItem;
import com.vladsch.flexmark.ast.OrderedList;
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

    public static MarkdownElement.MarkdownDocument readDocument(Path path) throws IOException {
        String content = Files.readString(path);
        return parse(content);
    }

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

    public static void writeDocument(MarkdownElement.MarkdownDocument document, Path targetPath) throws IOException {
        String content = document.toMarkdown();
        Files.writeString(targetPath, content);
    }

    private static MarkdownBlock convertNodeToBlock(Node node) {
        if (node instanceof Heading headingNode) {
            return new MarkdownBlock.Heading(
                    headingNode.getLevel(), headingNode.getText().toString());
        } else if (node instanceof Paragraph paragraphNode) {
            return new MarkdownBlock.Paragraph(
                    paragraphNode.getContentChars().toString().trim());
        } else if (node instanceof FencedCodeBlock codeNode) {
            return new MarkdownBlock.CodeBlock(
                    codeNode.getInfo().toString().trim(),
                    codeContent(codeNode.getContentChars().toString()));
        } else if (node instanceof IndentedCodeBlock codeNode) {
            return new MarkdownBlock.CodeBlock(
                    "", codeContent(codeNode.getContentChars().toString()));
        } else if (node instanceof OrderedList orderedListNode) {
            List<MarkdownBlock.ListItem> items = new ArrayList<>();
            for (Node itemNode : orderedListNode.getChildren()) {
                if (itemNode instanceof ListItem listItem) {
                    List<MarkdownBlock> itemBlocks = new ArrayList<>();
                    for (Node child = listItem.getFirstChild(); child != null; child = child.getNext()) {
                        MarkdownBlock block = convertNodeToBlock(child);
                        if (block != null) {
                            itemBlocks.add(block);
                        }
                    }
                    items.add(new MarkdownBlock.ListItem(itemBlocks));
                }
            }
            return new MarkdownBlock.OrderedList(orderedListNode.getStartNumber(), items);
        }
        String fallbackText = node.getChars().toString().trim();
        if (!fallbackText.isBlank()) {
            return new MarkdownBlock.Paragraph(fallbackText);
        }
        return null;
    }

    private static String codeContent(String raw) {
        return raw.replaceFirst("(?:[ \t]\\R)+$", "");
    }

    public static String convertHtmlToMarkdown(String htmlString) {
        if (htmlString == null || htmlString.isBlank()) {
            return "";
        }
        Document doc = Jsoup.parseBodyFragment(htmlString);
        for (Element a : doc.select("a[name]")) {
            if (a.text().isBlank() && a.children().isEmpty()) {
                a.remove();
            } else {
                a.removeAttr("name");
            }
        }
        for (Element a : doc.select("a[href]")) {
            String href = a.attr("href");
            if (href.endsWith(".html") || !href.contains("://")) {
                a.unwrap();
            }
        }
        MutableDataSet options = new MutableDataSet();
        options.set(FlexmarkHtmlConverter.OUTPUT_ATTRIBUTES_ID, false);
        options.set(FlexmarkHtmlConverter.SKIP_ATTRIBUTES, true);
        return FlexmarkHtmlConverter.builder(options)
                .build()
                .convert(doc.body().html())
                .trim();
    }
}
