package tungnn.tutor.java.starter.infrastructure.filesystem;

import java.util.Locale;
import tungnn.tutor.java.starter.infrastructure.webpage.crawler.PageCrawlResult;

public class UnitPageFileNameStrategy implements FileNameStrategy<PageCrawlResult> {

  public static final String UNIT_PREFIX = "%03d-";

  private static final int MAX_LENGTH = 100;
  private static final String FALLBACK = "untitled";

  @Override
  public String buildFileName(int index, PageCrawlResult source) {
    String title = source == null ? null : source.title();
    String slug = slugify(title);
    return UNIT_PREFIX.formatted(index) + slug;
  }

  private String slugify(String raw) {
    if (raw == null || raw.isBlank()) {
      return FALLBACK;
    }
    String slug =
        raw.trim()
            .toLowerCase(Locale.ROOT)
            .replaceAll("[^\\p{Alnum}]+", "-") // ký tự không hợp lệ -> '-'
            .replaceAll("(^-+|-+$)", ""); // bỏ '-' ở đầu/cuối

    if (slug.length() > MAX_LENGTH) {
      slug = slug.substring(0, MAX_LENGTH).replaceAll("-+$", "");
    }
    return slug.isBlank() ? FALLBACK : slug;
  }
}
