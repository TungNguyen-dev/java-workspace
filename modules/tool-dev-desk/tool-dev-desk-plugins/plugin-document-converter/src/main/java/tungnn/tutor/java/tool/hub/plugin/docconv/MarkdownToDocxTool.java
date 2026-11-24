package tungnn.tutor.java.tool.hub.plugin.docconv;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.pf4j.Extension;
import tungnn.tutor.java.infra.document.converter.MarkdownDocxConverterUtils;
import tungnn.tutor.java.infra.document.converter.MarkdownDocxConverterUtils.Options;
import tungnn.tutor.java.tool.hub.api.ParamSpec;
import tungnn.tutor.java.tool.hub.api.ToolContext;
import tungnn.tutor.java.tool.hub.api.ToolDescriptor;
import tungnn.tutor.java.tool.hub.api.ToolException;
import tungnn.tutor.java.tool.hub.api.ToolExtension;
import tungnn.tutor.java.tool.hub.api.ToolFile;
import tungnn.tutor.java.tool.hub.api.ToolLayout;
import tungnn.tutor.java.tool.hub.api.ToolResult;

@Extension
public class MarkdownToDocxTool implements ToolExtension {
    private static final String DOCX_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    private static Options options(Map<String, Object> in) throws ToolException {
        Options d = Options.defaults();
        String font = in.get("bodyFont") == null
                ? d.bodyFont()
                : in.get("bodyFont").toString().strip();
        int size = d.bodySizePt();
        if (in.get("bodySize") instanceof BigDecimal n) {
            size = n.intValue();
            if (size < 6 || size > 72) throw new ToolException("Font size must be between 6 and 72 pt");
        }
        return new Options(font, d.codeFont(), size, d.lineSpacing(), d.headings());
    }

    @Override
    public ToolDescriptor descriptor() {
        return new ToolDescriptor(
                "markdown-to-docx",
                "Markdown → DOCX",
                "Create a Word (.docx) document from Markdown: upload an .md file or paste the content",
                "document",
                ToolLayout.FILE);
    }

    @Override
    public List<ParamSpec> params() {
        Options d = Options.defaults();
        return List.of(
                new ParamSpec("file", "file", false, "Markdown file (.md) — leave empty if pasting content"),
                new ParamSpec("markdown", "text", false, "Or paste Markdown content"),
                new ParamSpec("filename", "string", false, "Result file name", "document"),
                new ParamSpec("bodyFont", "string", false, "Font", d.bodyFont()),
                new ParamSpec("bodySize", "number", false, "Font size (pt)", String.valueOf(d.bodySizePt())));
    }

    @Override
    public ToolResult execute(ToolContext ctx, Map<String, Object> in) throws ToolException {
        ToolFile file = (ToolFile) in.get("file");
        String markdown;
        String name;
        if (file != null) {
            ConverterSupport.requireExtension(file, ".md", ".markdown", ".txt");
            markdown = new String(file.content(), StandardCharsets.UTF_8);
            name = ConverterSupport.baseName(file.filename());
        } else if (in.get("markdown") != null) {
            markdown = in.get("markdown").toString();
            name = in.get("filename") == null
                    ? "document"
                    : ConverterSupport.baseName(in.get("filename").toString());
        } else {
            throw new ToolException("Upload an .md file or paste Markdown content");
        }
        Options options = options(in);
        byte[] docx = ConverterSupport.withPluginClassLoader(() -> {
            try (XWPFDocument doc = MarkdownDocxConverterUtils.toDocument(markdown, options);
                    ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                doc.write(out);
                return out.toByteArray();
            }
        });
        if (ctx != null) ctx.logger().info("md→docx {} ({} chars → {} bytes)", name, markdown.length(), docx.length);
        return ToolResult.ok(new ToolFile(name + ".docx", DOCX_TYPE, docx));
    }
}
