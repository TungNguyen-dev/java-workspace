package tungnn.tutor.java.tool.hub.plugin.base64;

import java.nio.charset.StandardCharsets;
import java.util.*;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import tungnn.tutor.java.tool.hub.api.*;

public class Base64Plugin extends Plugin {
    @Extension
    public static class Base64Tool implements ToolExtension {
        @Override
        public ToolDescriptor descriptor() {
            return new ToolDescriptor("base64", "Base64", "Encode/decode Base64", "encoding", ToolLayout.TRANSFORM);
        }

        @Override
        public List<ParamSpec> params() {
            return List.of(
                    new ParamSpec("mode", "enum:encode|decode", true, "Mode", "encode"),
                    new ParamSpec("text", "text", true, "Content"));
        }

        private static String normalizeNewlines(String s) {
            return s.replace("\r\n", "\n");
        }

        @Override
        public ToolResult execute(ToolContext ctx, Map<String, Object> in) throws ToolException {
            Object text = in.get("text");
            Object mode = in.get("mode");
            if (text == null || mode == null) return ToolResult.fail("Missing mode/text parameter");
            try {
                return switch (mode.toString()) {
                    case "encode" ->
                        ToolResult.ok(Base64.getEncoder()
                                .encodeToString(normalizeNewlines(text.toString()).getBytes(StandardCharsets.UTF_8)));
                    case "decode" ->
                        ToolResult.ok(new String(Base64.getDecoder().decode(text.toString().replaceAll("\\s+", "")), StandardCharsets.UTF_8));
                    default -> ToolResult.fail("Invalid mode");
                };
            } catch (IllegalArgumentException e) {
                return ToolResult.fail("Invalid Base64 string");
            }
        }
    }
}
