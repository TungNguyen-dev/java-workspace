package tungnn.tutor.java.tool.crawler.core.infra.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.selenium.pool.WebDriverPool;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawlRequest;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawlResult;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawler;

class DefaultWebCrawlServiceTest {

    private static final WebCrawlOptions OPTIONS = new WebCrawlOptions(2, Duration.ZERO);

    private final FakeDriverPool driverPool = new FakeDriverPool();

    @Test
    void crawlAllKeepsInputOrderAndDuplicates() {
        var service = serviceWith(driver -> request -> success(request.url()), 5);
        var requests = pages("a", "b", "a", "c");

        var results = service.crawlAll(requests);

        assertThat(results).extracting(r -> ((WebCrawlResult.Success) r).url()).containsExactly("a", "b", "a", "c");
        assertThat(driverPool.returned).hasSize(4);
        assertThat(driverPool.invalidated).isEmpty();
    }

    @Test
    void deadSessionInvalidatesDriverAndRetries() {
        var calls = new AtomicInteger();
        var service = serviceWith(
                driver -> request -> calls.getAndIncrement() == 0
                        ? new WebCrawlResult.Failure(request.url(), new NoSuchSessionException("gone"))
                        : success(request.url()),
                5);

        var result = service.crawl(new WebCrawlRequest.PageUrl("a"));

        assertThat(result).isInstanceOf(WebCrawlResult.Success.class);
        assertThat(calls).hasValue(2);
        assertThat(driverPool.invalidated).hasSize(1);
        assertThat(driverPool.returned).hasSize(1);
    }

    @Test
    void timeoutReturnsDriverWithoutRetry() {
        var calls = new AtomicInteger();
        var service = serviceWith(
                driver -> request -> {
                    calls.incrementAndGet();
                    return new WebCrawlResult.Failure(request.url(), new TimeoutException("slow"));
                },
                5);

        var result = service.crawl(new WebCrawlRequest.PageUrl("a"));

        assertThat(result).isInstanceOf(WebCrawlResult.Failure.class);
        assertThat(calls).hasValue(1);
        assertThat(driverPool.invalidated).isEmpty();
        assertThat(driverPool.returned).hasSize(1);
    }

    @Test
    void borrowFailureBecomesFailureWithoutThrowing() {
        driverPool.failBorrow = true;
        var service = serviceWith(driver -> request -> success(request.url()), 5);

        var results = service.crawlAll(pages("a", "b"));

        assertThat(results).hasSize(2).allSatisfy(r -> assertThat(r).isInstanceOf(WebCrawlResult.Failure.class));
    }

    @Test
    void nullRequestAndNullUrlReturnFailure() {
        var service = serviceWith(driver -> request -> success(request.url()), 5);

        assertThat(service.crawl(null)).isInstanceOf(WebCrawlResult.Failure.class);
        assertThat(service.crawl(new WebCrawlRequest.PageUrl(null))).isInstanceOf(WebCrawlResult.Failure.class);
    }

    @Test
    void closedServiceReturnsFailure() {
        var service = serviceWith(driver -> request -> success(request.url()), 5);
        service.close();

        assertThat(service.crawl(new WebCrawlRequest.PageUrl("a"))).isInstanceOf(WebCrawlResult.Failure.class);
    }

    @Test
    void perSpecCapHolds() {
        var running = new AtomicInteger();
        var peak = new AtomicInteger();
        var service = serviceWith(
                driver -> request -> {
                    peak.accumulateAndGet(running.incrementAndGet(), Math::max);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    running.decrementAndGet();
                    return success(request.url());
                },
                1);

        service.crawlAll(pages("a", "b", "c", "d", "e"));

        assertThat(peak).hasValue(1);
    }

    @Test
    void registryFirstMatchWinsAndFallsBack() {
        var registry = WebCrawlerRegistry.defaults();

        assertThat(registry.resolve(new WebCrawlRequest.PageUrl("https://www.coursera.org/x")).name())
                .isEqualTo("coursera");
        assertThat(registry.resolve(new WebCrawlRequest.PageUrl("https://youtu.be/x")).name())
                .isEqualTo("youtube");
        assertThat(registry.resolve(new WebCrawlRequest.PageUrl("https://docs.spring.io/x")).name())
                .isEqualTo("spring-doc");
        assertThat(registry.resolve(new WebCrawlRequest.PageUrl("https://example.com")).name())
                .isEqualTo("generic");
    }

    private DefaultWebCrawlService serviceWith(Function<WebDriver, WebCrawler> factory, int maxConcurrency) {
        var spec = new WebCrawlerSpec("stub", request -> true, factory, maxConcurrency);
        return new DefaultWebCrawlService(driverPool, new WebCrawlerRegistry(List.of(), spec), OPTIONS);
    }

    private static List<WebCrawlRequest> pages(String... urls) {
        return java.util.Arrays.stream(urls)
                .<WebCrawlRequest>map(WebCrawlRequest.PageUrl::new)
                .toList();
    }

    private static WebCrawlResult success(String url) {
        return new WebCrawlResult.Success(url, "title", "content");
    }

    private static class FakeDriverPool implements WebDriverPool {

        final List<WebDriver> returned = Collections.synchronizedList(new ArrayList<>());
        final List<WebDriver> invalidated = Collections.synchronizedList(new ArrayList<>());
        volatile boolean failBorrow;

        @Override
        public WebDriver borrowDriver() {
            if (failBorrow) {
                throw new IllegalStateException("Unable to borrow a WebDriver from the pool");
            }
            return mock(WebDriver.class);
        }

        @Override
        public void returnDriver(WebDriver driver) {
            returned.add(driver);
        }

        @Override
        public void invalidateDriver(WebDriver driver) {
            invalidated.add(driver);
        }

        @Override
        public int size() {
            return 8;
        }

        @Override
        public void close() {}
    }
}
