package tungnn.tutor.java.tool.crawler.core.domain;

public sealed interface CrawlRequest {

  String url();

  record WebPage(String url) implements CrawlRequest {}
}
