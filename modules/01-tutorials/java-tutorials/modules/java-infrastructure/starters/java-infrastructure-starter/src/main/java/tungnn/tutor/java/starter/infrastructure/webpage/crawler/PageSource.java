package tungnn.tutor.java.starter.infrastructure.webpage.crawler;

import java.net.URI;
import java.util.Arrays;

public enum PageSource {
  COURSERA("coursera.org"),
  YOUTUBE("youtube.com", "youtu.be");

  private final String[] hostPatterns;

  PageSource(String... hostPatterns) {
    this.hostPatterns = hostPatterns;
  }

  public static PageSource fromUrl(URI url) {
    String host = url.getHost();
    if (host == null) {
      throw new IllegalArgumentException("URL has no host: " + url);
    }
    String lower = host.toLowerCase();
    return Arrays.stream(values())
        .filter(source -> lower.contains(source.name().toLowerCase()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unsupported URL host: " + host));
  }

  private boolean matches(String host) {
    return Arrays.stream(hostPatterns).anyMatch(host::contains);
  }
}
