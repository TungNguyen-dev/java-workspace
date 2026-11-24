package tungnn.tutor.java.tool.hub.app.config;

import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("hub")
public record HubProperties(
        @DefaultValue("plugins") Path pluginsDir,
        @DefaultValue("data") Path dataDir,
        @DefaultValue("true") boolean watch,
        @DefaultValue Security security) {

    public HubProperties {
        pluginsDir = resolve(pluginsDir);
        dataDir = resolve(dataDir);
    }

    private static Path resolve(Path path) {
        String raw = path.toString();
        if (raw.equals("~") || raw.startsWith("~/")) {
            raw = System.getProperty("user.home") + raw.substring(1);
        }
        return Path.of(raw).toAbsolutePath().normalize();
    }

    public record Security(@DefaultValue("admin") String username, @DefaultValue("") String password) {
        public boolean enabled() {
            return password != null && !password.isBlank();
        }
    }
}
