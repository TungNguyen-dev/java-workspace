package tungnn.tutor.java.tool.crawler.core.infrastructure.pool;

import tungnn.tutor.java.tool.crawler.core.domain.WebCrawler;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawlerType;

public interface WebCrawlerPool extends AutoCloseable {

  WebCrawler borrowCrawler(WebCrawlerType type);

  void returnCrawler(WebCrawlerType type, WebCrawler webCrawler);
}
