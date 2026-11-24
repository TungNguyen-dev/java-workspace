package tungnn.tutor.java.tool.crawler.core.infra.crawler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.selenium.element.ElementFinder;
import tungnn.tutor.java.selenium.element.ElementInfo;
import tungnn.tutor.java.tool.crawler.core.domain.AbstractWebCrawler;
import tungnn.tutor.java.tool.crawler.core.domain.CrawlRequest;

public class SpringDocWebCrawler extends AbstractWebCrawler {

    public static final String HOME_URL = "https://docs.spring.io/spring-framework/reference/index.html";

    private static final By ARTICLE_LOCATOR = By.cssSelector("article.doc");
    private static final By TITLE_LOCATOR = By.cssSelector("article.doc h1#page-title, h1#page-title");
    private static final By NEXT_BUTTON_LOCATOR = By.cssSelector("nav.pagination span.next > a");
    private static final String NOISE_SELECTOR =
            ".breadcrumbs-container, aside.toc, nav.pagination, #copy-url, .edit-this-page";
    private static final int MAX_PAGES = 1_000;

    private static final String EXTRACT_HTML_SCRIPT = """
        const clone = arguments[0].cloneNode(true);
        clone.querySelectorAll(arguments[1]).forEach(n => n.remove());
        return clone.innerHTML;
        """;

    public SpringDocWebCrawler(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean supports(CrawlRequest request) {
        return request.url().contains("docs.spring.io");
    }

    @Override
    protected String getTitle() {
        return ElementFinder.findElementIfPresent(driver, TITLE_LOCATOR)
                .map(ElementInfo::getText)
                .filter(title -> !title.isBlank())
                .orElseGet(() -> Objects.requireNonNullElse(driver.getTitle(), "").trim());
    }

    @Override
    protected String getContentAsHtml() {
        var article = ElementFinder.findElement(driver, ARTICLE_LOCATOR, timeout());
        return (String) ((JavascriptExecutor) driver).executeScript(EXTRACT_HTML_SCRIPT, article, NOISE_SELECTOR);
    }

    public List<String> collectArticleUrls() {
        Set<String> visited = new LinkedHashSet<>();
        var current = normalize(HOME_URL);

        while (current != null && visited.size() < MAX_PAGES && visited.add(current)) {
            driver.get(current);
            current = findNextPageUrl().map(this::normalize).orElse(null);
        }
        return new ArrayList<>(visited);
    }

    private Optional<String> findNextPageUrl() {
        return ElementFinder.findElementIfPresent(driver, NEXT_BUTTON_LOCATOR)
                .map(link -> link.getDomAttribute("href"))
                .filter(href -> href != null && !href.isBlank())
                .map(this::toAbsolute);
    }

    private String toAbsolute(String href) {
        return URI.create(Objects.requireNonNull(driver.getCurrentUrl())).resolve(href).toString();
    }

    private String normalize(String url) {
        try {
            var uri = new URI(url);
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), uri.getQuery(), null).toString();
        } catch (URISyntaxException e) {
            var hash = url.indexOf('#');
            return hash < 0 ? url : url.substring(0, hash);
        }
    }
}
