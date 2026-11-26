package tungnn.tutor.java.tool.crawler.service;

import java.util.List;
import tungnn.tutor.java.tool.crawler.core.WebCrawlRequest;
import tungnn.tutor.java.tool.crawler.core.WebCrawlResult;

public interface WebCrawlService extends AutoCloseable {

    WebCrawlResult crawl(WebCrawlRequest request);

    List<WebCrawlResult> crawlAll(List<? extends WebCrawlRequest> requests);

    @Override
    void close();
}
