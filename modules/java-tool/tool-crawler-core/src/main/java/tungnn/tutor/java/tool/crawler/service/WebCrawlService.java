package tungnn.tutor.java.tool.crawler.service;

import java.util.List;
import tungnn.tutor.java.tool.crawler.core.WebCrawler;

public interface WebCrawlService extends AutoCloseable {

    WebCrawler.Result crawl(WebCrawler.Request request);

    List<WebCrawler.Result> crawlAll(List<? extends WebCrawler.Request> requests);

    @Override
    void close();
}
