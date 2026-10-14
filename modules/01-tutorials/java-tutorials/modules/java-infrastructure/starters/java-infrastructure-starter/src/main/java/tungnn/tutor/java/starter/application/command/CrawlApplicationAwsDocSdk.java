package tungnn.tutor.java.starter.application.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import tungnn.tutor.java.core.lib.io.filesystem.FileNameUtil;
import tungnn.tutor.java.core.lib.io.filesystem.FileUtil;
import tungnn.tutor.java.selenium.driver.ChromeWebDriverFactory;
import tungnn.tutor.java.selenium.driver.options.ChromeOptionsFactory;
import tungnn.tutor.java.starter.infrastructure.obsidian.ObsidianNote;
import tungnn.tutor.java.starter.infrastructure.webpage.AwsDocSdkPage;

public class CrawlApplicationAwsDocSdk {

  private static final Path URL_FILE = Path.of("storage", "aws-sdk-urls.txt");
  private static final Path OUTPUT_DIR = Path.of("storage", "aws-sdk-2x");

  static void main() throws IOException {
    var driverFactory = new ChromeWebDriverFactory(new ChromeOptionsFactory());
    var driver = driverFactory.getWebDrivers(1).getFirst();
    try {
      var page = new AwsDocSdkPage(driver);
      var urls = readUrls();
      var indexWidth = String.valueOf(urls.size()).length();

      Files.createDirectories(OUTPUT_DIR);
      for (var i = 0; i < urls.size(); i++) {
        crawlAndSave(page, urls.get(i), i + 1, indexWidth);
      }
    } finally {
      driver.quit();
    }
  }

  private static List<String> readUrls() {
    return FileUtil.readString(URL_FILE)
        .lines()
        .map(line -> line.strip().replace("\"", "").replace(",", ""))
        .filter(line -> !line.isEmpty())
        .toList();
  }

  private static void crawlAndSave(AwsDocSdkPage page, String url, int index, int indexWidth) {
    var result = page.crawl(url);

    var paddedIndex = "%0" + indexWidth + "d";
    var filename =
        "%s - %s.md".formatted(paddedIndex.formatted(index), FileNameUtil.sanitize(result.title()));

    var note = new ObsidianNote(result.title(), result.content(), List.of(result.url()));
    FileUtil.writeString(OUTPUT_DIR.resolve(filename), note.toMarkdown());
  }
}
