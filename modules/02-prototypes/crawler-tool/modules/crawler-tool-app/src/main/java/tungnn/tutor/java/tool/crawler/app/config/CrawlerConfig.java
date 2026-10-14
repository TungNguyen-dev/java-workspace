package tungnn.tutor.java.tool.crawler.app.config;

import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "crawler")
public record CrawlerConfig(Storage storage, Dir dir) {

  /** Gets the base root directory. */
  public Path baseDir() {
    return Path.of(storage.baseDir());
  }

  /** Gets the input directory for incoming task files. */
  public Path inputDir() {
    return baseDir().resolve(dir.input());
  }

  /** Gets the output directory for generated artifacts. */
  public Path outputDir() {
    return baseDir().resolve(dir.output());
  }

  /** Gets the done directory for processed files. */
  public Path doneDir() {
    return baseDir().resolve(dir.done());
  }

  public record Storage(String baseDir) {}

  public record Dir(String input, String output, String done) {}
}
