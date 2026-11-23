package tungnn.tutor.java.tool.crawler.core.domain;

public enum WebCrawlerType {
  COURSERA,
  YOUTUBE,
  BAELDUNG,
  ANTHROPIC_PARTNER,
  GENERIC;

  /** Resolves the WebCrawlerType based on the request URL. */
  public static WebCrawlerType resolveType(CrawlRequest request) {
    if (request == null || request.url() == null) {
      return WebCrawlerType.GENERIC;
    }
    var url = request.url();

    if (url.contains("coursera.org")) {
      return WebCrawlerType.COURSERA;
    }
    if (url.contains("youtube.com") || url.contains("youtu.be")) {
      return WebCrawlerType.YOUTUBE;
    }
    if (url.contains("baeldung.com")) {
      return WebCrawlerType.BAELDUNG;
    }
    if (url.contains("anthropic-partners.skilljar.com")) {
      return WebCrawlerType.ANTHROPIC_PARTNER;
    }

    return WebCrawlerType.GENERIC;
  }
}
