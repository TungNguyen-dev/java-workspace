package tungnn.tutor.java.tool.hub.core;

import java.nio.file.Path;
import org.slf4j.Logger;
import tungnn.tutor.java.tool.hub.api.ToolContext;

public record DefaultToolContext(Logger logger, Path dataDir) implements ToolContext {}
