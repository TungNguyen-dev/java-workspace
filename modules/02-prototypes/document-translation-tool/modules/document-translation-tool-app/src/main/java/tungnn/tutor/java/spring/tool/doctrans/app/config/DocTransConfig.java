package tungnn.tutor.java.spring.tool.doctrans.app.config;

import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "doctrans")
public record DocTransConfig(Storage storage, Dir dir, @DefaultValue("2") int workerThreads) {

  public Path baseDir() {
    return Path.of(storage.baseDir());
  }

  /** Root directory containing one subdirectory per translation job. */
  public Path jobDir() {
    return baseDir().resolve(dir.job());
  }

  public Path memoryDir() {
    return baseDir().resolve(dir.memory());
  }

  public record Storage(String baseDir) {}

  public record Dir(String job, String memory) {}
}
