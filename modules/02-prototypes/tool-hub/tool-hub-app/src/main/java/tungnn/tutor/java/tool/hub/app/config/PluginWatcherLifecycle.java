package tungnn.tutor.java.tool.hub.app.config;

import org.springframework.context.SmartLifecycle;
import tungnn.tutor.java.tool.hub.core.PluginService;
import tungnn.tutor.java.tool.hub.core.PluginWatcher;

public class PluginWatcherLifecycle implements SmartLifecycle {
  private final HubProperties props;
  private final PluginService service;
  private PluginWatcher watcher;
  private Thread thread;

  public PluginWatcherLifecycle(HubProperties props, PluginService service) {
    this.props = props;
    this.service = service;
  }

  @Override
  public synchronized void start() {
    if (!props.watch() || isRunning()) return;
    watcher = new PluginWatcher(props.pluginsDir(), service);
    thread = new Thread(watcher, "plugin-watcher");
    thread.setDaemon(true);
    thread.start();
  }

  @Override
  public synchronized void stop() {
    if (watcher != null) watcher.close();
    if (thread != null) thread.interrupt();
    watcher = null;
    thread = null;
  }

  @Override
  public synchronized boolean isRunning() {
    return thread != null && thread.isAlive();
  }
}
