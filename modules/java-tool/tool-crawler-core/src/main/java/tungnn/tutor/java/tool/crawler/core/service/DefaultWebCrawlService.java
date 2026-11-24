package tungnn.tutor.java.tool.crawler.core.service;

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
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawlRequest;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawlResult;
import tungnn.tutor.java.tool.crawler.core.infra.registry.WebCrawlerRegistry;
import tungnn.tutor.java.tool.crawler.core.infra.registry.WebCrawlerSpec;

public class DefaultWebCrawlService implements WebCrawlService {

    private static final System.Logger LOGGER = System.getLogger(DefaultWebCrawlService.class.getName());

    private final WebDriverPool driverPool;
    private final WebCrawlerRegistry registry;
    private final WebCrawlOptions options;
    private final Map<WebCrawlerSpec, Semaphore> permits = new ConcurrentHashMap<>();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final ReentrantReadWriteLock inFlight = new ReentrantReadWriteLock();

    public DefaultWebCrawlService(WebDriverPool driverPool, WebCrawlerRegistry registry, WebCrawlOptions options) {
        this.driverPool = Objects.requireNonNull(driverPool, "driverPool must not be null");
        this.registry = Objects.requireNonNull(registry, "registry must not be null");
        this.options = Objects.requireNonNull(options, "options must not be null");
        for (var spec : registry.all()) {
            permits.put(spec, new Semaphore(spec.maxConcurrency(), true));
        }
    }

    @Override
    public WebCrawlResult crawl(WebCrawlRequest request) {
        if (request == null || request.url() == null) {
            return new WebCrawlResult.Failure(
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
            return new WebCrawlResult.Failure(request.url(), e);
        } catch (RuntimeException e) {
            return new WebCrawlResult.Failure(request.url(), e);
        } catch (LinkageError e) {
            return new WebCrawlResult.Failure(request.url(), new IllegalStateException(e));
        } finally {
            inFlight.readLock().unlock();
        }
    }

    @Override
    public List<WebCrawlResult> crawlAll(List<? extends WebCrawlRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }
        var safeRequests = new ArrayList<WebCrawlRequest>(requests.size());
        for (var request : requests) {
            safeRequests.add(request != null ? request : new WebCrawlRequest.PageUrl(null));
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

    private static WebCrawlResult closedFailure(WebCrawlRequest request) {
        return new WebCrawlResult.Failure(request.url(), new IllegalStateException("WebCrawlService is closed"));
    }

    private Outcome attemptCrawl(WebCrawlerSpec spec, WebCrawlRequest request) throws InterruptedException {
        var permit = permits.computeIfAbsent(spec, key -> new Semaphore(key.maxConcurrency(), true));
        // The permit is taken before borrowing so queued requests never hold a browser while waiting.
        permit.acquire();
        WebDriver driver = null;
        var sessionDead = false;
        try {
            driver = driverPool.borrowDriver();
            var result = spec.factory().apply(driver).crawl(request);
            if (result instanceof WebCrawlResult.Failure failure) {
                sessionDead = isSessionDead(failure.exception());
            }
            return new Outcome(result, sessionDead);
        } catch (RuntimeException e) {
            sessionDead = driver != null && isSessionDead(e);
            return new Outcome(new WebCrawlResult.Failure(request.url(), e), sessionDead);
        } catch (LinkageError e) {
            return new Outcome(new WebCrawlResult.Failure(request.url(), new IllegalStateException(e)), false);
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

    private record Outcome(WebCrawlResult result, boolean sessionDead) {}
}
