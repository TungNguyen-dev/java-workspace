package tungnn.tutor.java.tool.hub.core;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.pf4j.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.tool.hub.api.ToolContext;
import tungnn.tutor.java.tool.hub.api.ToolExtension;

public class PluginService {
  private static final Logger log = LoggerFactory.getLogger(PluginService.class);
  private final PluginManager pm;
  private final ToolRegistry registry;
  private final Path dataRoot;

  public PluginService(Path pluginsDir, Path dataRoot, ToolRegistry registry) {
    this.pm = new DefaultPluginManager(pluginsDir);
    this.registry = registry;
    this.dataRoot = dataRoot;
    pm.addPluginStateListener(e ->
        log.info(
            "[{}] {} -> {}", e.getPlugin().getPluginId(), e.getOldState(), e.getPluginState()));
  }

  private static Path expandedDir(Path zip) {
    String n = zip.getFileName().toString();
    return zip.resolveSibling(n.substring(0, n.length() - ".zip".length()));
  }

  private static void deleteRecursively(Path p) {
    if (!Files.exists(p)) return;
    try (var s = Files.walk(p)) {
      s.sorted(Comparator.reverseOrder()).forEach(f -> f.toFile().delete());
    } catch (IOException e) {
      log.warn("Unable to delete {}", p, e);
    }
  }

  public synchronized void loadAll() {
    pm.loadPlugins();
    pm.startPlugins();
    pm.getPlugins().forEach(w -> registerExtensions(w.getPluginId()));
  }

  public synchronized void loadOne(Path zip) {
    try {
      Path dir = FileUtils.expandIfZip(zip);
      String id = pm.loadPlugin(dir);
      if (id == null) {
        log.warn("Unable to load plugin: {}", zip);
        return;
      }
      pm.startPlugin(id);
      registerExtensions(id);
    } catch (Exception e) {
      log.error("Error loading plugin {}", zip, e);
    }
  }

  public synchronized void unloadOne(String pluginId) {
    PluginWrapper w = pm.getPlugin(pluginId);
    registry.unregisterPlugin(pluginId);
    pm.stopPlugin(pluginId);
    pm.unloadPlugin(pluginId);
    if (w != null) deleteRecursively(w.getPluginPath());
  }

  public synchronized String pluginIdOf(Path zip) {
    Path dir = expandedDir(zip).toAbsolutePath().normalize();
    return pm.getPlugins().stream()
        .filter(w -> w.getPluginPath().toAbsolutePath().normalize().equals(dir))
        .map(PluginWrapper::getPluginId)
        .findFirst()
        .orElse(null);
  }

  public void shutdown() {
    pm.stopPlugins();
  }

  private void registerExtensions(String pluginId) {
    for (ToolExtension ext : pm.getExtensions(ToolExtension.class, pluginId)) {
      try {
        registry.register(pluginId, ext, newContext(pluginId));
      } catch (Exception e) {
        log.error("Unable to register tools of plugin {}", pluginId, e);
      }
    }
  }

  private ToolContext newContext(String pluginId) throws IOException {
    Path dir = Files.createDirectories(dataRoot.resolve(pluginId));
    return new DefaultToolContext(LoggerFactory.getLogger("plugin." + pluginId), dir);
  }
}
