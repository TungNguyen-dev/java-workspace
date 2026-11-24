package tungnn.tutor.java.tool.crawler.core.domain;

import java.time.Duration;
import java.util.Objects;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.infra.document.adapter.markdown.MarkdownDocumentUtils;
import tungnn.tutor.java.selenium.browser.NavigationUtils;
import tungnn.tutor.java.selenium.browser.ScriptUtils;
import tungnn.tutor.java.selenium.wait.Waits;

public abstract class AbstractWebCrawler implements WebCrawler {

    private static final System.Logger LOGGER = System.getLogger(AbstractWebCrawler.class.getName());

    protected final WebDriver driver;

    public AbstractWebCrawler(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public WebCrawlResult crawl(WebCrawlRequest request) {
        var url = request.url();
        try {
            NavigationUtils.navigateTo(driver, url);
            waitForPageToLoad();

            var title = getTitle();
            var content = MarkdownDocumentUtils.convertHtmlToMarkdown(getContentAsHtml());

            LOGGER.log(System.Logger.Level.INFO, "Success to crawl URL: " + url);
            return new WebCrawlResult.Success(url, title, content);
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.ERROR, "Failed to crawl URL: " + url, e);
            return new WebCrawlResult.Failure(url, e);
        }
    }

    protected Duration timeout() {
        return Duration.ofMinutes(5);
    }

    protected abstract String getTitle();

    protected abstract String getContentAsHtml();

    public WebDriver driver() {
        return driver;
    }

    private void waitForPageToLoad() {
        Waits.newWait(driver, timeout())
                .until(driver ->
                        Objects.equals(ScriptUtils.executeScript(driver, "return document.readyState"), "complete"));
    }
}
