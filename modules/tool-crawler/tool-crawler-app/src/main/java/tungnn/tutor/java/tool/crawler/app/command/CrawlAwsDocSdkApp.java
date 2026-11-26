package tungnn.tutor.java.tool.crawler.app.command;

import java.nio.file.Path;
import java.util.List;
import tungnn.tutor.java.core.lib.io.filesystem.FileNameUtils;
import tungnn.tutor.java.core.lib.io.filesystem.FileUtils;
import tungnn.tutor.java.selenium.driver.chrome.ChromeDriverUtils;
import tungnn.tutor.java.tool.crawler.app.model.ObsidianNote;
import tungnn.tutor.java.tool.crawler.core.WebCrawlRequest;
import tungnn.tutor.java.tool.crawler.core.WebCrawlResult;
import tungnn.tutor.java.tool.crawler.core.impl.AwsDocSdkWebCrawler;

public class CrawlAwsDocSdkApp {

    private static final Path URL_FILE = Path.of("storage", "aws-sdk-urls.txt");
    private static final Path OUTPUT_DIR = Path.of("storage", "aws-sdk-2x");

    static void main() {
        var driver = ChromeDriverUtils.createDriver("profile_0");
        try {
            var crawler = new AwsDocSdkWebCrawler(driver);
            var urls = readUrls();
            var indexWidth = String.valueOf(urls.size()).length();

            FileUtils.createDirectories(OUTPUT_DIR);
            for (var i = 0; i < urls.size(); i++) {
                crawlAndSave(crawler, urls.get(i), i + 1, indexWidth);
            }
        } finally {
            driver.quit();
        }
    }

    private static List<String> readUrls() {
        return FileUtils.readString(URL_FILE)
                .lines()
                .map(line -> line.strip().replace("\"", "").replace(",", ""))
                .filter(line -> !line.isEmpty())
                .toList();
    }

    private static void crawlAndSave(AwsDocSdkWebCrawler crawler, String url, int index, int indexWidth) {
        switch (crawler.crawl(new WebCrawlRequest.PageUrl(url))) {
            case WebCrawlResult.Success(var pageUrl, var title, var content) -> {
                var filename = "%0*d - %s.md".formatted(indexWidth, index, FileNameUtils.sanitize(title));
                var note = new ObsidianNote(title, content, pageUrl, List.of(), "TungNN", "tungnn.hn@gmail.com");
                FileUtils.writeString(OUTPUT_DIR.resolve(filename), note.toMarkdown());
            }
            case WebCrawlResult.Failure(_, var exception) ->
                System.err.println("Failed to crawl " + url + ": " + exception.getMessage());
        }
    }
}
