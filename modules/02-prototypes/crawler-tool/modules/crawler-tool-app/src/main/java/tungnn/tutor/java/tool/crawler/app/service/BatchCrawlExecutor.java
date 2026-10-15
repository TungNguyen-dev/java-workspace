package tungnn.tutor.java.tool.crawler.app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tungnn.tutor.java.core.lib.multithread.ConcurrentUtils;
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
   * Accepts a list of crawling requests, processes them concurrently using virtual threads through
   * ConcurrentUtils, and collects the results into an insertion-ordered map.
   */
  public Map<CrawlRequest, CrawlResult> submitBatch(List<CrawlRequest.WebPage> batchWebRequests) {
    if (batchWebRequests == null || batchWebRequests.isEmpty()) {
      return Map.of();
    }

    List<CrawlResult> results =
        ConcurrentUtils.executeConcurrently(batchWebRequests, this::executeCrawl);

    Map<CrawlRequest, CrawlResult> crawlResults = new HashMap<>(batchWebRequests.size());

    for (int i = 0; i < batchWebRequests.size(); i++) {
      crawlResults.putIfAbsent(batchWebRequests.get(i), results.get(i));
    }

    return crawlResults;
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
