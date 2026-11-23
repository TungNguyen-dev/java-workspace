package tungnn.tutor.java.tool.hub.api;

import java.nio.file.Path;
import org.slf4j.Logger;

public interface ToolContext {
  Logger logger();

  Path dataDir();
}
