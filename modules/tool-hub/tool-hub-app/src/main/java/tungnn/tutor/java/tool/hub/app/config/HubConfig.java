package tungnn.tutor.java.tool.hub.app.config;

import java.io.IOException;
import java.nio.file.Files;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tungnn.tutor.java.tool.hub.core.PluginService;
import tungnn.tutor.java.tool.hub.core.ToolRegistry;

@Configuration(proxyBeanMethods = false)
public class HubConfig {
  @Bean
  ToolRegistry toolRegistry() {
    return new ToolRegistry();
  }

  @Bean(initMethod = "loadAll", destroyMethod = "shutdown")
  PluginService pluginService(HubProperties props, ToolRegistry registry) throws IOException {
    Files.createDirectories(props.pluginsDir());
    return new PluginService(props.pluginsDir(), props.dataDir(), registry);
  }

  @Bean
  PluginWatcherLifecycle pluginWatcherLifecycle(HubProperties props, PluginService service) {
    return new PluginWatcherLifecycle(props, service);
  }
}
