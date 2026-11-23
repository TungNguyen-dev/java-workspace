package tungnn.tutor.java.tool.hub.api;

import java.util.List;
import java.util.Map;
import org.pf4j.ExtensionPoint;

public interface ToolExtension extends ExtensionPoint {
  ToolDescriptor descriptor();

  List<ParamSpec> params();

  ToolResult execute(ToolContext ctx, Map<String, Object> input) throws ToolException;

  default void init(ToolContext ctx) {}

  default void destroy() {}
}
