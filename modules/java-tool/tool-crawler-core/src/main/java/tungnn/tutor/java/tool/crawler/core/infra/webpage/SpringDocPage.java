package tungnn.tutor.java.tool.crawler.core.infra.webpage;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import tungnn.tutor.java.selenium.element.ElementFinder;
import tungnn.tutor.java.selenium.element.ElementInfo;
import tungnn.tutor.java.tool.crawler.core.infra.webpage.crawler.PageCrawlResult;
import tungnn.tutor.java.tool.crawler.core.infra.webpage.crawler.PageCrawler;

public class SpringDocPage extends BasePage implements PageCrawler {

    private static final By ARTICLE_CONTENT_SELECTOR = By.cssSelector("article.doc");

    private static final By TITLE_SELECTOR = By.cssSelector("article.doc h1#page-title, h1#page-title");

    private static final By NEXT_BUTTON_SELECTOR = By.cssSelector("nav.pagination span.next > a");

    private static final By PREV_BUTTON_SELECTOR = By.cssSelector("nav.pagination span.prev > a");

    private static final String NOISE_SELECTOR =
            ".breadcrumbs-container, aside.toc, nav.pagination, #copy-url, .edit-this-page";

    private static final int MAX_PAGES = 1_000;

    private static final String EXTRACT_CONTENT_SCRIPT = """
        const root = arguments[0];
        const noises = root.querySelectorAll(arguments[1]);
        const saved = [];
        noises.forEach(n => { saved.push([n, n.style.display]); n.style.display = 'none'; });
        const text = root.innerText;
        saved.forEach(entry => { entry[0].style.display = entry[1]; });
        return text;
        """;

    public SpringDocPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected Duration timeout() {
        return Duration.ofSeconds(30);
    }

    @Override
    protected String homeUrl() {
        return "https://docs.spring.io/spring-framework/reference/index.html";
    }

    // =====================================================================================
    // PageCrawler
    // =====================================================================================

    @Override
    public PageCrawlResult crawl(String url) {
        navigateTo(url);
        return capture(url);
    }

    public List<PageCrawlResult> crawlAll() {
        List<PageCrawlResult> results = new ArrayList<>();
        traverse((url, hasArticle) -> {
            if (hasArticle) {
                results.add(capture(url));
            }
        });
        return List.copyOf(results);
    }

    public List<String> collectAllArticleUrls() {
        List<String> urls = new ArrayList<>();
        traverse((url, hasArticle) -> urls.add(url));
        return List.copyOf(urls);
    }

    // =====================================================================================
    // Traversal
    // =====================================================================================

    private void traverse(PageVisitor visitor) {
        Set<String> visited = new LinkedHashSet<>();
        String current = normalize(homeUrl());

        while (current != null && visited.size() < MAX_PAGES) {
            if (!visited.add(current)) {
                break; // already visited -> loop detected, stop safely
            }
            navigateTo(current);
            boolean hasArticle = waitForArticle().isPresent();
            visitor.visit(current, hasArticle);
            current = findNextPageUrl().map(this::normalize).orElse(null);
        }
    }

    private Optional<WebElement> waitForArticle() {
        try {
            return Optional.of(ElementFinder.findElement(driver, ARTICLE_CONTENT_SELECTOR, timeout()));
        } catch (WebDriverException e) {
            return Optional.empty();
        }
    }

    public Optional<String> findNextPageUrl() {
        return findPaginationUrl(NEXT_BUTTON_SELECTOR);
    }

    public Optional<String> findPreviousPageUrl() {
        return findPaginationUrl(PREV_BUTTON_SELECTOR);
    }

    public boolean hasNextPage() {
        return findNextPageUrl().isPresent();
    }

    private Optional<String> findPaginationUrl(By locator) {
        return ElementFinder.findElementIfPresent(driver, locator)
                .map(link -> {
                    String href = link.getDomProperty("href");
                    return (href == null || href.isBlank()) ? link.getDomAttribute("href") : href;
                })
                .filter(href -> !href.isBlank())
                .map(this::toAbsolute);
    }

    private PageCrawlResult capture(String url) {
        return new PageCrawlResult(url, getTitle(), getContent());
    }

    // =====================================================================================
    // Extraction
    // =====================================================================================

    private String getTitle() {
        return ElementFinder.findElementIfPresent(driver, TITLE_SELECTOR)
                .map(ElementInfo::getText)
                .filter(title -> !title.isBlank())
                .orElseGet(() -> {
                    String docTitle = driver.getTitle();
                    return docTitle == null ? "" : docTitle.trim();
                });
    }

    private String getContent() {
        Optional<WebElement> article = ElementFinder.findElementIfPresent(driver, ARTICLE_CONTENT_SELECTOR);
        if (article.isEmpty()) {
            return "";
        }
        WebElement root = article.get();
        if (driver instanceof JavascriptExecutor js) {
            try {
                Object text = js.executeScript(EXTRACT_CONTENT_SCRIPT, root, NOISE_SELECTOR);
                if (text instanceof String s && !s.isBlank()) {
                    return s.strip();
                }
            } catch (WebDriverException ignored) {
                // fall through to the fallback below
            }
        }
        return ElementInfo.getText(root);
    }

    private String toAbsolute(String href) {
        try {
            return URI.create(Objects.requireNonNull(driver.getCurrentUrl()))
                    .resolve(href)
                    .toString();
        } catch (IllegalArgumentException | NullPointerException e) {
            return href;
        }
    }

    // =====================================================================================
    // URL helpers
    // =====================================================================================

    private String normalize(String url) {
        if (url == null || url.isBlank()) {
            return url;
        }
        try {
            URI uri = new URI(url);
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), uri.getQuery(), null).toString();
        } catch (URISyntaxException e) {
            int hash = url.indexOf('#');
            return hash < 0 ? url : url.substring(0, hash);
        }
    }

    @FunctionalInterface
    private interface PageVisitor {
        void visit(String url, boolean hasArticle);
    }
}
