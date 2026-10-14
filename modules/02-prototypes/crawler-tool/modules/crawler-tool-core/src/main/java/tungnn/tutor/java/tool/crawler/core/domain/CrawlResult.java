package tungnn.tutor.java.tool.crawler.core.domain;

public sealed interface CrawlResult {

  record Success(String url, String title, String content) implements CrawlResult {}

  record Failure(Exception exception) implements CrawlResult {}
}
