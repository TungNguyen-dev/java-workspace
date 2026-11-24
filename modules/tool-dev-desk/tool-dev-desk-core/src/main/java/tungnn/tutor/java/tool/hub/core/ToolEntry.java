package tungnn.tutor.java.tool.hub.core;

import tungnn.tutor.java.tool.hub.api.ToolContext;
import tungnn.tutor.java.tool.hub.api.ToolExtension;

public record ToolEntry(ToolExtension extension, ToolContext context) {}
