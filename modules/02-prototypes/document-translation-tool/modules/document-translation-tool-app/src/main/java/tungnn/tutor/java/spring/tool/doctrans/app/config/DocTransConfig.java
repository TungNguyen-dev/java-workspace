package tungnn.tutor.java.spring.tool.doctrans.app.config;

import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "doctrans")
public record DocTransConfig(Storage storage, Dir dir, @DefaultValue("2") int workerThreads) {

  public DocTransConfig {
    storage = storage != null ? storage : new Storage("./data");
    dir = dir != null ? dir : new Dir("jobs", "memory");
    workerThreads = workerThreads > 0 ? workerThreads : 2;
  }

  public Path baseDir() {
    String base =
        (storage.baseDir() == null || storage.baseDir().isBlank()) ? "./data" : storage.baseDir();
    return Path.of(base);
  }

  /** Root directory containing one subdirectory per translation job. */
  public Path jobDir() {
    String job = (dir.job() == null || dir.job().isBlank()) ? "jobs" : dir.job();
    return baseDir().resolve(job);
  }

  public Path memoryDir() {
    String memory = (dir.memory() == null || dir.memory().isBlank()) ? "memory" : dir.memory();
    return baseDir().resolve(memory);
  }

  public record Storage(@DefaultValue("./data") String baseDir) {}

  public record Dir(@DefaultValue("jobs") String job, @DefaultValue("memory") String memory) {}
}
