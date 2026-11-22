package tungnn.tutor.java.tool.hub.plugin.docconv;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.pf4j.Extension;
import tungnn.tutor.java.infra.document.converter.DocxMarkdownConverterUtils;
import tungnn.tutor.java.tool.hub.api.ParamSpec;
import tungnn.tutor.java.tool.hub.api.ToolContext;
import tungnn.tutor.java.tool.hub.api.ToolDescriptor;
import tungnn.tutor.java.tool.hub.api.ToolException;
import tungnn.tutor.java.tool.hub.api.ToolExtension;
import tungnn.tutor.java.tool.hub.api.ToolFile;
import tungnn.tutor.java.tool.hub.api.ToolLayout;
import tungnn.tutor.java.tool.hub.api.ToolResult;

@Extension
public class DocxToMarkdownTool implements ToolExtension {
  @Override
  public ToolDescriptor descriptor() {
    return new ToolDescriptor(
        "docx-to-markdown",
        "DOCX → Markdown",
        "Chuyển tài liệu Word (.docx) sang Markdown: heading, đậm/nghiêng, code, danh sách, bảng",
        "document",
        ToolLayout.FILE);
  }

  @Override
  public List<ParamSpec> params() {
    return List.of(
        new ParamSpec("file", "file", true, "File Word (.docx)"),
        new ParamSpec("output", ConverterSupport.OUTPUT_TYPE, true, "Kết quả", "preview"));
  }

  @Override
  public ToolResult execute(ToolContext ctx, Map<String, Object> in) throws ToolException {
    ToolFile file = (ToolFile) in.get("file");
    ConverterSupport.requireExtension(file, ".docx");
    String markdown =
        ConverterSupport.withPluginClassLoader(() -> {
          Path tmp = ConverterSupport.toTempFile(ctx, file, ".docx");
          try {
            return DocxMarkdownConverterUtils.toMarkdown(tmp);
          } finally {
            ConverterSupport.deleteQuietly(tmp);
          }
        });
    if (ctx != null) ctx.logger().info("docx→md {} ({} bytes)", file.filename(), file.size());
    return ConverterSupport.textResult(
        markdown,
        in.get("output"),
        ConverterSupport.baseName(file.filename()) + ".md",
        "text/markdown");
  }
}
