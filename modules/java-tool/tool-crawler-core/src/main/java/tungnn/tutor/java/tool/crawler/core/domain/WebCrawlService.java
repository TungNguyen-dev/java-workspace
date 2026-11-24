package tungnn.tutor.java.tool.crawler.core.domain;

import java.util.List;

public interface WebCrawlService extends AutoCloseable {

    WebCrawlResult crawl(WebCrawlRequest request);

    List<WebCrawlResult> crawlAll(List<? extends WebCrawlRequest> requests);

    @Override
    void close();
}
