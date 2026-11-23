package tungnn.tutor.java.tool.crawler.core.domain;

public interface WebCrawler {

  boolean supports(CrawlRequest request);

  CrawlResult crawl(CrawlRequest crawlRequest);
}
