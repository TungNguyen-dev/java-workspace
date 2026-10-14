package tungnn.tutor.java.spring.tool.doctrans.translation_old.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtil;

public record AppConfig(int chunkSize, int maxConcurrency, int apiTimeoutSeconds) {

  public static AppConfig load() {
    return load("translation-tool.properties");
  }

  public static AppConfig load(String resourceName) {
    Properties props = new Properties();

    try (InputStream input = ResourceUtil.getResourceAsStream(resourceName)) {
      if (input == null) {
        throw new IllegalArgumentException("Resource file not found: " + resourceName);
      }
      props.load(input);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to load properties file: " + resourceName, e);
    }

    int chunkSize = Integer.parseInt(props.getProperty("translation.chunk-size", "50"));
    int maxConcurrency = Integer.parseInt(props.getProperty("translation.max-concurrency", "10"));
    int apiTimeoutSeconds =
        Integer.parseInt(props.getProperty("translation.api.timeout-seconds", "30"));

    return new AppConfig(chunkSize, maxConcurrency, apiTimeoutSeconds);
  }
}
