package tungnn.tutor.java.tool.crawler.app.command;

import java.nio.file.Path;
import java.util.List;
import tungnn.tutor.java.core.lib.io.filesystem.FileNameUtils;
import tungnn.tutor.java.core.lib.io.filesystem.FileUtils;
import tungnn.tutor.java.selenium.driver.chrome.ChromeDriverUtils;
import tungnn.tutor.java.tool.crawler.app.model.ObsidianNote;
import tungnn.tutor.java.tool.crawler.core.WebCrawlRequest;
import tungnn.tutor.java.tool.crawler.core.WebCrawlResult;
import tungnn.tutor.java.tool.crawler.core.impl.SpringDocWebCrawler;

public class CrawlSpringDocApp {

    private static final Path OUTPUT_DIR = Path.of("storage", "spring-docs");

    static void main() {
        var driver = ChromeDriverUtils.createDriver("profile_0");
        try {
            var crawler = new SpringDocWebCrawler(driver);
            var urls = crawler.collectArticleUrls();
            FileUtils.createDirectories(OUTPUT_DIR);

            for (var i = 0; i < urls.size(); i++) {
                crawlAndSave(crawler, urls.get(i), i + 1);
            }
        } finally {
            driver.quit();
        }
    }

    private static void crawlAndSave(SpringDocWebCrawler crawler, String url, int index) {
        switch (crawler.crawl(new WebCrawlRequest.PageUrl(url))) {
            case WebCrawlResult.Success(var pageUrl, var title, var content) -> {
                var filename = FileNameUtils.sanitize("%03d - %s.md".formatted(index, title));
                var note = new ObsidianNote(title, content, pageUrl, List.of(), "TungNN", "tungnn.hn@gmail.com");
                FileUtils.writeString(OUTPUT_DIR.resolve(filename), note.toMarkdown());
            }
            case WebCrawlResult.Failure(_, var exception) ->
                System.err.println("Failed to crawl " + url + ": " + exception.getMessage());
        }
    }
}
