package tungnn.tutor.java.spring.tool.doctrans.app.config;

import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "doctrans")
public record DocTransConfig(Storage storage, Dir dir, String targetLanguage) {

  /** Gets the base root directory. */
  public Path baseDir() {
    return Path.of(storage.baseDir());
  }

  /** Gets the input directory for incoming task documents. */
  public Path inputDir() {
    return baseDir().resolve(dir.input());
  }

  /** Gets the output directory for generated translated artifacts. */
  public Path outputDir() {
    return baseDir().resolve(dir.output());
  }

  /** Gets the done directory for successfully processed files. */
  public Path doneDir() {
    return baseDir().resolve(dir.done());
  }

  public record Storage(String baseDir) {}

  public record Dir(String input, String output, String done) {}
}
