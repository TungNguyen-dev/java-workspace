package tungnn.tutor.java.starter.application.command;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import tungnn.tutor.java.core.lib.io.filesystem.FileNameUtil;
import tungnn.tutor.java.core.lib.io.filesystem.FileUtil;
import tungnn.tutor.java.selenium.driver.ChromeWebDriverFactory;
import tungnn.tutor.java.selenium.driver.options.ChromeOptionsFactory;
import tungnn.tutor.java.starter.infrastructure.obsidian.ObsidianNote;
import tungnn.tutor.java.starter.infrastructure.webpage.SpringDocPage;

public class CrawlApplicationSpringDocUseCase {

  static void main() throws IOException {
    new CrawlApplicationSpringDocUseCase().run();
  }

  private void run() throws IOException {
    var driverFactory = new ChromeWebDriverFactory(new ChromeOptionsFactory());
    var driver = driverFactory.getWebDrivers(1).getFirst();
    try {
      var page = new SpringDocPage(driver);
      var results = page.crawlAll();
      var storageDir = Path.of("storage", "spring-docs");
      FileUtil.createDirectories(storageDir);
      var counter = new AtomicInteger(0);
      results.forEach(
          result -> {
            var count = counter.incrementAndGet();
            var filename = FileNameUtil.sanitize("%03d - %s.md".formatted(count, result.title()));
            var note = new ObsidianNote(result.title(), result.content(), List.of(result.url()));
            FileUtil.writeString(storageDir.resolve(filename), note.toMarkdown());
          });
    } finally {
      driver.quit();
    }
  }
}
