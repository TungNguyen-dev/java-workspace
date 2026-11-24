package tungnn.tutor.java.tool.crawler.core.infra.service;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawlRequest;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawler;

public record WebCrawlerSpec(
        String name,
        Predicate<WebCrawlRequest> matcher,
        Function<WebDriver, WebCrawler> factory,
        int maxConcurrency) {

    public WebCrawlerSpec {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(matcher, "matcher must not be null");
        Objects.requireNonNull(factory, "factory must not be null");
        if (maxConcurrency <= 0) {
            throw new IllegalArgumentException("maxConcurrency must be positive: " + maxConcurrency);
        }
    }
}
