package tungnn.tutor.java.tool.crawler.app.command;

import java.nio.file.Path;
import java.util.List;
import tungnn.tutor.java.core.lib.io.filesystem.FileNameUtils;
import tungnn.tutor.java.core.lib.io.filesystem.FileUtils;
import tungnn.tutor.java.selenium.driver.chrome.ChromeDriverUtils;
import tungnn.tutor.java.tool.crawler.app.model.ObsidianNote;
import tungnn.tutor.java.tool.crawler.core.WebCrawler;
import tungnn.tutor.java.tool.crawler.core.impl.JavaJlsWebCrawler;

public class CrawlJavaJlsApp {

    private static final Path URL_FILE = Path.of("storage", "jls-java26.txt");
    private static final Path OUTPUT_DIR = Path.of("storage", "jls26");

    private int chapterCount = 0;
    private int noteCount = 0;
    private Path currentDir = OUTPUT_DIR;

    static void main() {
        new CrawlJavaJlsApp().run();
    }

    private void run() {
        var driver = ChromeDriverUtils.createDriver("profile_0");
        try {
            var crawler = new JavaJlsWebCrawler(driver);
            for (var url : readUrls()) {
                crawlAndSave(crawler, url);
            }
        } finally {
            driver.quit();
        }
    }

    private List<String> readUrls() {
        return FileUtils.readString(URL_FILE)
                .lines()
                .map(String::strip)
                .filter(line -> !line.isEmpty())
                .toList();
    }

    private void crawlAndSave(JavaJlsWebCrawler crawler, String url) {
        switch (crawler.crawl(new WebCrawler.Request.PageUrl(url))) {
            case WebCrawler.Result.Success(var pageUrl, var title, var content) -> save(pageUrl, title, content);
            case WebCrawler.Result.Failure(_, var exception) ->
                System.err.println("Failed to crawl " + url + ": " + exception.getMessage());
        }
    }

    private void save(String url, String title, String content) {
        var sectionTitle = FileNameUtils.sanitize(extractSectionTitle(title));

        if (title.startsWith("Chapter")) {
            currentDir = OUTPUT_DIR.resolve("%d - %s".formatted(++chapterCount, sectionTitle));
            FileUtils.createDirectories(currentDir);
            noteCount = 0;
        }

        var filename = "%02d - %s.md".formatted(++noteCount, sectionTitle);
        var note = new ObsidianNote(title, content, url, List.of(), "TungNN", "tungnn.hn@gmail.com");
        FileUtils.writeString(currentDir.resolve(filename), note.toMarkdown());
    }

    // "Chapter 15. Expressions" -> "Expressions"; "15.12.2.5. Choosing ..." -> "Choosing ..."
    private String extractSectionTitle(String title) {
        return title.substring(title.indexOf(". ") + 1).strip();
    }
}
