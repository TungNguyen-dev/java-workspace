package tungnn.tutor.java.tool.hub.core;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.pf4j.CompoundPluginDescriptorFinder;
import org.pf4j.DefaultPluginManager;
import org.pf4j.ManifestPluginDescriptorFinder;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginDescriptorFinder;
import org.pf4j.PluginManager;
import org.pf4j.PropertiesPluginDescriptorFinder;
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
    private final Path pluginsDir;
    private final Map<Path, String> zipToPlugin = new HashMap<>();
    private final PluginDescriptorFinder descriptorFinder = new CompoundPluginDescriptorFinder()
            .add(new PropertiesPluginDescriptorFinder())
            .add(new ManifestPluginDescriptorFinder());

    public PluginService(Path pluginsDir, Path dataRoot, ToolRegistry registry) {
        this.pluginsDir = pluginsDir;
        this.pm = new DefaultPluginManager(pluginsDir);
        this.registry = registry;
        this.dataRoot = dataRoot;
        pm.addPluginStateListener(
                e -> log.info("[{}] {} -> {}", e.getPlugin().getPluginId(), e.getOldState(), e.getPluginState()));
    }

    private static Path expandedDir(Path zip) {
        String n = zip.getFileName().toString();
        return zip.resolveSibling(n.substring(0, n.length() - ".zip".length()));
    }

    private static Path key(Path zip) {
        return zip.toAbsolutePath().normalize();
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
        List<Path> zips;
        try (var s = Files.list(pluginsDir)) {
            zips = s.filter(f -> f.getFileName().toString().toLowerCase().endsWith(".zip"))
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparing(PluginService::lastModified))
                    .toList();
        } catch (IOException e) {
            log.error("Unable to list plugins directory {}", pluginsDir, e);
            return;
        }
        // oldest first, so when several zips share a plugin id the newest wins
        zips.forEach(this::loadOne);
    }

    private static FileTime lastModified(Path p) {
        try {
            return Files.getLastModifiedTime(p);
        } catch (IOException e) {
            return FileTime.fromMillis(0);
        }
    }

    public synchronized void loadOne(Path zip) {
        Path zipKey = key(zip);
        String previous = zipToPlugin.get(zipKey);
        if (previous != null) unloadOne(previous);
        try {
            Path dir = expandedDir(zip);
            deleteRecursively(dir);
            dir = FileUtils.expandIfZip(zip);
            PluginDescriptor descriptor = descriptorFinder.find(dir);
            String id = descriptor.getPluginId();
            if (pm.getPlugin(id) != null) {
                log.info("Plugin '{}' is already loaded from another file, replacing it with {}", id, zip.getFileName());
                unloadOne(id);
            }
            String loaded = pm.loadPlugin(dir);
            if (loaded == null) {
                log.warn("Unable to load plugin: {}", zip);
                return;
            }
            zipToPlugin.put(zipKey, loaded);
            pm.startPlugin(loaded);
            registerExtensions(loaded);
        } catch (Throwable e) {
            log.error("Error loading plugin {}", zip, e);
        }
    }

    public synchronized void unloadOne(String pluginId) {
        PluginWrapper w = pm.getPlugin(pluginId);
        registry.unregisterPlugin(pluginId);
        zipToPlugin.values().removeIf(pluginId::equals);
        if (w == null) return;
        pm.stopPlugin(pluginId);
        pm.unloadPlugin(pluginId);
        deleteRecursively(w.getPluginPath());
    }

    public synchronized void unloadZip(Path zip) {
        String id = zipToPlugin.get(key(zip));
        if (id == null) {
            deleteRecursively(expandedDir(zip));
            return;
        }
        unloadOne(id);
    }

    public synchronized void shutdown() {
        registry.unregisterAll();
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
