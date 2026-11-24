package tungnn.tutor.java.tool.crawler.core.domain;

public sealed interface WebCrawlRequest {

    String url();

    record PageUrl(String url) implements WebCrawlRequest {}
}
