package tungnn.tutor.java.tool.hub.plugin.docconv;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.pf4j.Extension;
import tungnn.tutor.java.infra.document.ingestion.TikaContentExtractionUtils;
import tungnn.tutor.java.tool.hub.api.ParamSpec;
import tungnn.tutor.java.tool.hub.api.ToolContext;
import tungnn.tutor.java.tool.hub.api.ToolDescriptor;
import tungnn.tutor.java.tool.hub.api.ToolException;
import tungnn.tutor.java.tool.hub.api.ToolExtension;
import tungnn.tutor.java.tool.hub.api.ToolFile;
import tungnn.tutor.java.tool.hub.api.ToolLayout;
import tungnn.tutor.java.tool.hub.api.ToolResult;

@Extension
public class ExtractTextTool implements ToolExtension {
    @Override
    public ToolDescriptor descriptor() {
        return new ToolDescriptor(
                "extract-text",
                "Document → Text",
                "Extract text content from PDF, Word, Excel, PowerPoint, HTML... (Apache Tika)",
                "document",
                ToolLayout.FILE);
    }

    @Override
    public List<ParamSpec> params() {
        return List.of(
                new ParamSpec("file", "file", true, "Document"),
                new ParamSpec("output", ConverterSupport.OUTPUT_TYPE, true, "Result", "preview"));
    }

    @Override
    public ToolResult execute(ToolContext ctx, Map<String, Object> in) throws ToolException {
        ToolFile file = (ToolFile) in.get("file");
        String text = ConverterSupport.withPluginClassLoader(() -> {
            try (InputStream is = new ByteArrayInputStream(file.content())) {
                return TikaContentExtractionUtils.extractContent(is);
            }
        });
        text = text == null ? "" : text.strip();
        if (text.isEmpty()) {
            return new ToolResult(true, "", "Unable to extract any text (image/scanned file?)");
        }
        return ConverterSupport.textResult(
                text, in.get("output"), ConverterSupport.baseName(file.filename()) + ".txt", "text/plain");
    }
}
