package tungnn.tutor.java.tool.hub.app.config;

import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("hub")
public record HubProperties(
    @DefaultValue("plugins") Path pluginsDir,
    @DefaultValue("data") Path dataDir,
    @DefaultValue("true") boolean watch) {}
