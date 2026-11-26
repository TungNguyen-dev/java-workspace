package tungnn.tutor.java.tool.crawler.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import tungnn.tutor.java.core.lib.multithread.ConcurrentUtils;
import tungnn.tutor.java.selenium.pool.WebDriverPool;
import tungnn.tutor.java.tool.crawler.core.WebCrawler;
import tungnn.tutor.java.tool.crawler.core.WebCrawlerRegistry;

public class DefaultWebCrawlService implements WebCrawlService {

    private static final System.Logger LOGGER = System.getLogger(DefaultWebCrawlService.class.getName());

    private final WebDriverPool driverPool;
    private final WebCrawlerRegistry registry;
    private final Options options;
    private final Map<WebCrawlerRegistry.Spec, Semaphore> permits = new ConcurrentHashMap<>();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final ReentrantReadWriteLock inFlight = new ReentrantReadWriteLock();

    public DefaultWebCrawlService(WebDriverPool driverPool, WebCrawlerRegistry registry, Options options) {
        this.driverPool = Objects.requireNonNull(driverPool, "driverPool must not be null");
        this.registry = Objects.requireNonNull(registry, "registry must not be null");
        this.options = Objects.requireNonNull(options, "options must not be null");
        for (var spec : registry.all()) {
            permits.put(spec, new Semaphore(spec.maxConcurrency(), true));
        }
    }

    @Override
    public WebCrawler.Result crawl(WebCrawler.Request request) {
        if (request == null || request.url() == null) {
            return new WebCrawler.Result.Failure(
                    request == null ? null : request.url(), new IllegalArgumentException("Request url is required"));
        }
        inFlight.readLock().lock();
        try {
            var spec = registry.resolve(request);
            var attempt = 1;
            while (true) {
                if (closed.get()) {
                    return closedFailure(request);
                }
                var outcome = attemptCrawl(spec, request);
                if (!outcome.sessionDead() || attempt >= options.maxAttempts()) {
                    return outcome.result();
                }
                LOGGER.log(System.Logger.Level.WARNING, "Browser session died, retrying URL: " + request.url());
                attempt++;
                Thread.sleep(options.retryBackoff());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new WebCrawler.Result.Failure(request.url(), e);
        } catch (RuntimeException e) {
            return new WebCrawler.Result.Failure(request.url(), e);
        } catch (LinkageError e) {
            return new WebCrawler.Result.Failure(request.url(), new IllegalStateException(e));
        } finally {
            inFlight.readLock().unlock();
        }
    }

    @Override
    public List<WebCrawler.Result> crawlAll(List<? extends WebCrawler.Request> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }
        var safeRequests = new ArrayList<WebCrawler.Request>(requests.size());
        for (var request : requests) {
            safeRequests.add(request != null ? request : new WebCrawler.Request.PageUrl(null));
        }
        return ConcurrentUtils.executeConcurrently(safeRequests, this::crawl, driverPool.size());
    }

    @Override
    public void close() {
        closed.set(true);
        // Taking the write lock waits for crawls that are still running, so the caller can safely close the pool next.
        inFlight.writeLock().lock();
        inFlight.writeLock().unlock();
    }

    private static WebCrawler.Result closedFailure(WebCrawler.Request request) {
        return new WebCrawler.Result.Failure(request.url(), new IllegalStateException("WebCrawlService is closed"));
    }

    private Outcome attemptCrawl(WebCrawlerRegistry.Spec spec, WebCrawler.Request request) throws InterruptedException {
        var permit = permits.computeIfAbsent(spec, key -> new Semaphore(key.maxConcurrency(), true));
        // The permit is taken before borrowing so queued requests never hold a browser while waiting.
        permit.acquire();
        WebDriver driver = null;
        var sessionDead = false;
        try {
            driver = driverPool.borrowDriver();
            var result = spec.factory().apply(driver).crawl(request);
            if (result instanceof WebCrawler.Result.Failure failure) {
                sessionDead = isSessionDead(failure.exception());
            }
            return new Outcome(result, sessionDead);
        } catch (RuntimeException e) {
            sessionDead = driver != null && isSessionDead(e);
            return new Outcome(new WebCrawler.Result.Failure(request.url(), e), sessionDead);
        } catch (LinkageError e) {
            return new Outcome(new WebCrawler.Result.Failure(request.url(), new IllegalStateException(e)), false);
        } finally {
            try {
                if (driver != null) {
                    if (sessionDead) {
                        driverPool.invalidateDriver(driver);
                    } else {
                        driverPool.returnDriver(driver);
                    }
                }
            } finally {
                permit.release();
            }
        }
    }

    // TimeoutException is deliberately excluded: a slow element does not mean the browser is dead.
    private static boolean isSessionDead(Throwable throwable) {
        for (var current = throwable; current != null; current = current.getCause()) {
            if (current instanceof NoSuchSessionException
                    || current instanceof SessionNotCreatedException
                    || current instanceof UnreachableBrowserException) {
                return true;
            }
        }
        return false;
    }

    private record Outcome(WebCrawler.Result result, boolean sessionDead) {}

    public record Options(int maxAttempts, Duration retryBackoff) {

        private static final int DEFAULT_MAX_ATTEMPTS = 2;
        private static final Duration DEFAULT_RETRY_BACKOFF = Duration.ofSeconds(2);

        public Options {
            if (maxAttempts <= 0) {
                throw new IllegalArgumentException("maxAttempts must be positive: " + maxAttempts);
            }
            Objects.requireNonNull(retryBackoff, "retryBackoff must not be null");
            if (retryBackoff.isNegative()) {
                throw new IllegalArgumentException("retryBackoff must not be negative: " + retryBackoff);
            }
        }

        public static Options defaults() {
            return new Options(DEFAULT_MAX_ATTEMPTS, DEFAULT_RETRY_BACKOFF);
        }
    }
}
