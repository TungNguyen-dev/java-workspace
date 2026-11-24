package tungnn.tutor.java.tool.hub.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.tool.hub.api.ToolContext;
import tungnn.tutor.java.tool.hub.api.ToolExtension;

public class ToolRegistry {
    private static final Logger log = LoggerFactory.getLogger(ToolRegistry.class);
    private final Map<String, ToolEntry> tools = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> byPlugin = new ConcurrentHashMap<>();

    public synchronized void register(String pluginId, ToolExtension ext, ToolContext ctx) {
        String id = ext.descriptor().id();
        if (tools.containsKey(id)) {
            throw new IllegalStateException("Duplicate tool id: " + id);
        }
        // init runs before publishing so requests never see a half-initialized tool
        ext.init(ctx);
        tools.put(id, new ToolEntry(ext, ctx));
        byPlugin.computeIfAbsent(pluginId, k -> ConcurrentHashMap.newKeySet()).add(id);
    }

    public synchronized void unregisterPlugin(String pluginId) {
        Set<String> ids = byPlugin.remove(pluginId);
        if (ids == null) return;
        for (String id : ids) {
            ToolEntry entry = tools.remove(id);
            if (entry != null) destroyQuietly(id, entry.extension());
        }
    }

    public synchronized void unregisterAll() {
        List<String> pluginIds = new ArrayList<>(byPlugin.keySet());
        pluginIds.forEach(this::unregisterPlugin);
    }

    public Optional<ToolEntry> entry(String id) {
        return Optional.ofNullable(tools.get(id));
    }

    public Collection<ToolExtension> all() {
        return tools.values().stream().map(ToolEntry::extension).toList();
    }

    private static void destroyQuietly(String id, ToolExtension ext) {
        try {
            ext.destroy();
        } catch (Throwable t) {
            log.warn("Tool {} failed while being destroyed", id, t);
        }
    }
}
