package tungnn.tutor.java.tool.crawler.app.config;

import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "crawler")
public record CrawlerConfig(Storage storage, Dir dir) {

    public Path baseDir() {
        return Path.of(storage.baseDir());
    }

    public Path inputDir() {
        return baseDir().resolve(dir.input());
    }

    public Path outputDir() {
        return baseDir().resolve(dir.output());
    }

    public Path doneDir() {
        return baseDir().resolve(dir.done());
    }

    public record Storage(String baseDir) {}

    public record Dir(String input, String output, String done) {}
}
