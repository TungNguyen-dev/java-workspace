package tungnn.tutor.java.tool.hub.core;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import tungnn.tutor.java.tool.hub.api.ToolContext;
import tungnn.tutor.java.tool.hub.api.ToolExtension;

public class ToolRegistry {

  private final Map<String, ToolExtension> tools = new ConcurrentHashMap<>();
  private final Map<String, Set<String>> byPlugin = new ConcurrentHashMap<>();

  public void register(String pluginId, ToolExtension ext, ToolContext ctx) {
    String id = ext.descriptor().id();
    if (tools.putIfAbsent(id, ext) != null) {
      throw new IllegalStateException("Trùng tool id: " + id);
    }
    byPlugin.computeIfAbsent(pluginId, k -> ConcurrentHashMap.newKeySet()).add(id);
    ext.init(ctx);
  }

  public void unregisterPlugin(String pluginId) {
    Set<String> ids = byPlugin.remove(pluginId);
    if (ids == null) return;
    for (String id : ids) {
      ToolExtension ext = tools.remove(id);
      if (ext != null)
        try {
          ext.destroy();
        } catch (Exception ignored) {
        }
    }
  }

  public Optional<ToolExtension> get(String id) {
    return Optional.ofNullable(tools.get(id));
  }

  public Collection<ToolExtension> all() {
    return tools.values();
  }
}
