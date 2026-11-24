package tungnn.tutor.java.tool.crawler.core.application.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import tungnn.tutor.java.core.lib.io.filesystem.FileUtil;
import tungnn.tutor.java.selenium.driver.chrome.ChromeDriverUtils;
import tungnn.tutor.java.tool.crawler.core.infra.obsidian.ObsidianNote;
import tungnn.tutor.java.tool.crawler.core.infra.webpage.JavaJLSPage;

public class CrawlApplicationJavaJlsUseCase {

  private static final Path URL_FILE = Path.of("storage", "jls-java26.txt");
  private static final Path OUTPUT_DIR = Path.of("storage", "jls26");

  private int chapterCount = 0;
  private int noteCount = 0;
  private Path currentDir = OUTPUT_DIR;

  static void main() throws IOException {
    new CrawlApplicationJavaJlsUseCase().run();
  }

  private void run() throws IOException {
    var driver = ChromeDriverUtils.createDriver("profile_0");
    try {
      var page = new JavaJLSPage(driver);
      for (var url : readUrls()) {
        crawlAndSave(page, url);
      }
    } finally {
      driver.quit();
    }
  }

  private List<String> readUrls() {
    return FileUtil.readString(URL_FILE)
        .lines()
        .map(String::strip)
        .filter(line -> !line.isEmpty())
        .toList();
  }

  private void crawlAndSave(JavaJLSPage page, String url) throws IOException {
    var result = page.crawl(url);
    var sectionTitle = extractSectionTitle(result.title());

    if (result.title().contains("Chapter")) {
      currentDir = OUTPUT_DIR.resolve("%d - %s".formatted(++chapterCount, sectionTitle));
      Files.createDirectories(currentDir);
      noteCount = 0;
    }

    var filename = "%02d - %s.md".formatted(++noteCount, sectionTitle);
    var note = new ObsidianNote(result.title(), result.content(), List.of(result.url()));
    FileUtil.writeString(currentDir.resolve(filename), note.toMarkdown());
  }

  private String extractSectionTitle(String title) {
    return title.substring(title.lastIndexOf('.') + 1).strip();
  }
}
