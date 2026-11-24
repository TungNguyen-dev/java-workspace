package tungnn.tutor.java.tool.crawler.core.service;

import java.util.List;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawlRequest;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawlResult;

public interface WebCrawlService extends AutoCloseable {

    WebCrawlResult crawl(WebCrawlRequest request);

    List<WebCrawlResult> crawlAll(List<? extends WebCrawlRequest> requests);

    @Override
    void close();
}
