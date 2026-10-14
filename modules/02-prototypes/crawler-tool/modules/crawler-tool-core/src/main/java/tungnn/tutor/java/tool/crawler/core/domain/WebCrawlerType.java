package tungnn.tutor.java.tool.crawler.core.domain;

public enum WebCrawlerType {
  COURSERA,
  YOUTUBE,
  GENERIC;

  /** Resolves the PageCrawlerType based on the request URL. */
  public static WebCrawlerType resolveType(CrawlRequest request) {
    var url = request.url();
    if (url == null) {
      return WebCrawlerType.GENERIC;
    }
    if (url.contains("coursera.org")) {
      return WebCrawlerType.COURSERA;
    }
    if (url.contains("youtube.com") || url.contains("youtu.be")) {
      return WebCrawlerType.YOUTUBE;
    }
    return WebCrawlerType.GENERIC;
  }
}
