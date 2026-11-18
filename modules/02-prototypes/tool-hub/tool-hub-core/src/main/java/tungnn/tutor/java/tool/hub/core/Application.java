package tungnn.tutor.java.tool.hub.core;

import java.nio.file.Files;
import java.nio.file.Path;

public class Application {

  public static void main(String[] args) throws Exception {
    Path pluginsDir = Path.of(System.getProperty("hub.plugins.dir", "plugins"));
    Files.createDirectories(pluginsDir);

    ToolRegistry registry = new ToolRegistry();
    PluginService service = new PluginService(pluginsDir, Path.of("data"), registry);
    service.loadAll();
    Runtime.getRuntime().addShutdownHook(new Thread(service::shutdown));

    Thread t = new Thread(new PluginWatcher(pluginsDir, service), "plugin-watcher");
    t.setDaemon(true);
    t.start();

    registry.all().forEach(e -> System.out.println("Tool: " + e.descriptor().id()));
    Thread.currentThread().join();
  }
}
