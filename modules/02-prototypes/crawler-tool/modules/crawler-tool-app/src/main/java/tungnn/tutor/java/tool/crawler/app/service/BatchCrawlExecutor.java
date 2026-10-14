package tungnn.tutor.java.tool.crawler.app.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import tungnn.tutor.java.tool.crawler.core.domain.CrawlRequest;
import tungnn.tutor.java.tool.crawler.core.domain.CrawlResult;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawler;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawlerType;
import tungnn.tutor.java.tool.crawler.core.infrastructure.pool.WebCrawlerPool;

public class BatchCrawlExecutor {

  private final WebCrawlerPool webCrawlerPool;

  public BatchCrawlExecutor(WebCrawlerPool webCrawlerPool) {
    this.webCrawlerPool = webCrawlerPool;
  }

  /**
   * Accepts a list of crawling requests, processes them in parallel using virtual threads and
   * WebCrawlerPool, and collects the results into a Map.
   */
  public Map<CrawlRequest, CrawlResult> submitBatch(List<CrawlRequest.WebPage> batchWebRequests) {
    if (batchWebRequests == null || batchWebRequests.isEmpty()) {
      return Map.of();
    }

    Map<CrawlRequest, CrawlResult> resultMap = new ConcurrentHashMap<>();

    // Use Virtual Threads ExecutorService for parallel execution
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      var futures =
          batchWebRequests.stream()
              .map(
                  request ->
                      CompletableFuture.runAsync(
                          () -> {
                            var result = executeCrawl(request);
                            resultMap.put(request, result);
                          },
                          executor))
              .toList();

      // Wait for all tasks to complete
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    return resultMap;
  }

  /**
   * Borrows an appropriate crawler from the Pool, performs crawling, and returns it to the Pool.
   */
  private CrawlResult executeCrawl(CrawlRequest.WebPage request) {
    var crawlerType = WebCrawlerType.resolveType(request);
    WebCrawler crawler = null;
    try {
      crawler = webCrawlerPool.borrowCrawler(crawlerType);
      return crawler.crawl(request);
    } catch (Exception e) {
      return new CrawlResult.Failure(e);
    } finally {
      if (crawler != null) {
        webCrawlerPool.returnCrawler(crawlerType, crawler);
      }
    }
  }
}
