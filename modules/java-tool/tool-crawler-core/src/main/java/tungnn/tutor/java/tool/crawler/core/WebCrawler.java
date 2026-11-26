package tungnn.tutor.java.tool.crawler.core;

public interface WebCrawler {

    Result crawl(Request crawlRequest);

    sealed interface Request {

        String url();

        record PageUrl(String url) implements Request {}
    }

    sealed interface Result {

        record Success(String url, String title, String content) implements Result {}

        record Failure(String url, Exception exception) implements Result {}
    }
}
