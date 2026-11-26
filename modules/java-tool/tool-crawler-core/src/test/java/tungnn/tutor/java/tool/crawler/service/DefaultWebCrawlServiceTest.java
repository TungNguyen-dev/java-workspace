package tungnn.tutor.java.tool.crawler.service;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.selenium.pool.WebDriverPool;
import tungnn.tutor.java.tool.crawler.core.WebCrawler;
import tungnn.tutor.java.tool.crawler.core.WebCrawlerRegistry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DefaultWebCrawlServiceTest {

    private static final DefaultWebCrawlService.Options OPTIONS = new DefaultWebCrawlService.Options(2, Duration.ZERO);

    private final FakeDriverPool driverPool = new FakeDriverPool();

    @Test
    void crawlAllKeepsInputOrderAndDuplicates() {
        try (var service = serviceWith(_ -> request -> success(request.url()), 5)) {
            var results = service.crawlAll(pages("a", "b", "a", "c"));

            assertThat(results)
                    .extracting(r -> ((WebCrawler.Result.Success) r).url())
                    .containsExactly("a", "b", "a", "c");
            assertThat(driverPool.returned).hasSize(4);
            assertThat(driverPool.invalidated).isEmpty();
        }
    }

    @Test
    void deadSessionInvalidatesDriverAndRetries() {
        var calls = new AtomicInteger();
        WebCrawler crawler = request -> calls.getAndIncrement() == 0
                ? new WebCrawler.Result.Failure(request.url(), new NoSuchSessionException("gone"))
                : success(request.url());
        try (var service = serviceWith(_ -> crawler, 5)) {
            var result = service.crawl(new WebCrawler.Request.PageUrl("a"));

            assertThat(result).isInstanceOf(WebCrawler.Result.Success.class);
            assertThat(calls).hasValue(2);
            assertThat(driverPool.invalidated).hasSize(1);
            assertThat(driverPool.returned).hasSize(1);
        }
    }

    @Test
    void timeoutReturnsDriverWithoutRetry() {
        var calls = new AtomicInteger();
        WebCrawler crawler = request -> {
            calls.incrementAndGet();
            return new WebCrawler.Result.Failure(request.url(), new TimeoutException("slow"));
        };
        try (var service = serviceWith(_ -> crawler, 5)) {
            var result = service.crawl(new WebCrawler.Request.PageUrl("a"));

            assertThat(result).isInstanceOf(WebCrawler.Result.Failure.class);
            assertThat(calls).hasValue(1);
            assertThat(driverPool.invalidated).isEmpty();
            assertThat(driverPool.returned).hasSize(1);
        }
    }

    @Test
    void borrowFailureBecomesFailureWithoutThrowing() {
        driverPool.failBorrow = true;
        try (var service = serviceWith(_ -> request -> success(request.url()), 5)) {
            var results = service.crawlAll(pages("a", "b"));

            assertThat(results).hasSize(2).allSatisfy(r -> assertThat(r).isInstanceOf(WebCrawler.Result.Failure.class));
        }
    }

    @Test
    void nullRequestAndNullUrlReturnFailure() {
        try (var service = serviceWith(_ -> request -> success(request.url()), 5)) {
            assertThat(service.crawl(null)).isInstanceOf(WebCrawler.Result.Failure.class);
            assertThat(service.crawl(new WebCrawler.Request.PageUrl(null))).isInstanceOf(WebCrawler.Result.Failure.class);
        }
    }

    @Test
    void crawlAllTurnsNullElementIntoFailure() {
        try (var service = serviceWith(_ -> request -> success(request.url()), 5)) {
            var results = service.crawlAll(Arrays.asList(new WebCrawler.Request.PageUrl("a"), null));

            assertThat(results).hasSize(2);
            assertThat(results.get(0)).isInstanceOf(WebCrawler.Result.Success.class);
            assertThat(results.get(1)).isInstanceOf(WebCrawler.Result.Failure.class);
        }
    }

    @Test
    void unregisteredSpecStillGetsPermit() {
        var spec = new WebCrawlerRegistry.Spec("ghost", _ -> true, _ -> request -> success(request.url()), 1);
        var registry = new WebCrawlerRegistry(List.of(), spec) {
            @Override
            public WebCrawlerRegistry.Spec resolve(WebCrawler.Request request) {
                return new WebCrawlerRegistry.Spec("other", _ -> true, _ -> r -> success(r.url()), 1);
            }
        };
        try (var service = new DefaultWebCrawlService(driverPool, registry, OPTIONS)) {
            assertThat(service.crawl(new WebCrawler.Request.PageUrl("a"))).isInstanceOf(WebCrawler.Result.Success.class);
        }
    }

    @Test
    void crawlerErrorBecomesFailure() {
        WebCrawler crawler = _ -> {
            throw new NoClassDefFoundError("missing");
        };
        try (var service = serviceWith(_ -> crawler, 5)) {
            assertThat(service.crawl(new WebCrawler.Request.PageUrl("a"))).isInstanceOf(WebCrawler.Result.Failure.class);
            assertThat(driverPool.returned).hasSize(1);
        }
    }

    @Test
    void urlMatchingIsCaseInsensitiveAndNullSafe() {
        var registry = WebCrawlerRegistry.defaults();

        assertThat(registry.resolve(new WebCrawler.Request.PageUrl("HTTPS://YOUTUBE.COM/x"))
                        .name())
                .isEqualTo("youtube");
        assertThat(registry.resolve(new WebCrawler.Request.PageUrl(null)).name()).isEqualTo("generic");
    }

    @Test
    void closedServiceReturnsFailure() {
        try (var service = serviceWith(_ -> request -> success(request.url()), 5)) {
            service.close();

            assertThat(service.crawl(new WebCrawler.Request.PageUrl("a"))).isInstanceOf(WebCrawler.Result.Failure.class);
        }
    }

    @Test
    void perSpecCapHolds() {
        var running = new AtomicInteger();
        var peak = new AtomicInteger();
        WebCrawler crawler = request -> {
            peak.accumulateAndGet(running.incrementAndGet(), Math::max);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            running.decrementAndGet();
            return success(request.url());
        };
        try (var service = serviceWith(_ -> crawler, 1)) {
            service.crawlAll(pages("a", "b", "c", "d", "e"));

            assertThat(peak).hasValue(1);
        }
    }

    @Test
    void registryFirstMatchWinsAndFallsBack() {
        var registry = WebCrawlerRegistry.defaults();

        assertThat(registry.resolve(new WebCrawler.Request.PageUrl("https://www.coursera.org/x"))
                        .name())
                .isEqualTo("coursera");
        assertThat(registry.resolve(new WebCrawler.Request.PageUrl("https://youtu.be/x"))
                        .name())
                .isEqualTo("youtube");
        assertThat(registry.resolve(new WebCrawler.Request.PageUrl("https://docs.spring.io/x"))
                        .name())
                .isEqualTo("spring-doc");
        assertThat(registry.resolve(new WebCrawler.Request.PageUrl("https://example.com"))
                        .name())
                .isEqualTo("generic");
    }

    private DefaultWebCrawlService serviceWith(Function<WebDriver, WebCrawler> factory, int maxConcurrency) {
        var spec = new WebCrawlerRegistry.Spec("stub", _ -> true, factory, maxConcurrency);
        return new DefaultWebCrawlService(driverPool, new WebCrawlerRegistry(List.of(), spec), OPTIONS);
    }

    private static List<WebCrawler.Request> pages(String... urls) {
        return Arrays.stream(urls)
                .<WebCrawler.Request>map(WebCrawler.Request.PageUrl::new)
                .toList();
    }

    private static WebCrawler.Result success(String url) {
        return new WebCrawler.Result.Success(url, "title", "content");
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
