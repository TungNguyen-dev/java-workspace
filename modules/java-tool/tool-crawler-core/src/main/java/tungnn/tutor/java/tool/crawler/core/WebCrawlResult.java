package tungnn.tutor.java.tool.crawler.core;

public sealed interface WebCrawlResult {

    record Success(String url, String title, String content) implements WebCrawlResult {}

    record Failure(String url, Exception exception) implements WebCrawlResult {}
}
