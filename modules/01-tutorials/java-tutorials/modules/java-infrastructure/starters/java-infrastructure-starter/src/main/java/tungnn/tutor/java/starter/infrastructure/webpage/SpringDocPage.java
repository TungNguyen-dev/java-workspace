package tungnn.tutor.java.starter.infrastructure.webpage;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;
import org.openqa.selenium.*;
import tungnn.tutor.java.selenium.util.ElementUtil;
import tungnn.tutor.java.starter.infrastructure.webpage.crawler.PageCrawlResult;
import tungnn.tutor.java.starter.infrastructure.webpage.crawler.PageCrawler;

/**
 * Page Object cho Spring Framework Reference Documentation (Antora layout).
 *
 * <p>Cấu trúc DOM tham chiếu:
 *
 * <pre>{@code
 * <article class="doc">
 *   <div class="breadcrumbs-container">...</div>   <-- noise
 *   <h1 id="page-title" class="page">Spring Framework Overview</h1>
 *   <aside class="toc embedded">...</aside>        <-- noise
 *   ... nội dung chính ...
 *   <nav class="pagination">                       <-- noise (nhưng dùng để duyệt trang)
 *     <span class="prev"><a href="index.html">Spring Framework</a></span>
 *     <span class="next"><a href="core.html">Core Technologies</a></span>
 *   </nav>
 * </article>
 * }</pre>
 *
 * <p><b>Chiến lược crawl:</b> đi theo chuỗi liên kết {@code span.next > a} bắt đầu từ {@link
 * #homeUrl()} cho tới khi không còn trang kế tiếp. Có {@link #MAX_PAGES} và tập {@code visited} để
 * chặn vòng lặp vô hạn khi tài liệu bị link vòng.
 */
public class SpringDocPage extends BasePage implements PageCrawler {

  /** Vùng nội dung chính: thẻ article có class "doc". */
  private static final By ARTICLE_CONTENT_SELECTOR = By.cssSelector("article.doc");

  /** Tiêu đề trang: {@code <h1 id="page-title" class="page">}. */
  private static final By TITLE_SELECTOR =
      By.cssSelector("article.doc h1#page-title, h1#page-title");

  /** Liên kết "trang kế tiếp" trong khối phân trang. */
  private static final By NEXT_BUTTON_SELECTOR = By.cssSelector("nav.pagination span.next > a");

  /** Liên kết "trang trước đó" trong khối phân trang. */
  private static final By PREV_BUTTON_SELECTOR = By.cssSelector("nav.pagination span.prev > a");

  /** Các thành phần phụ trợ cần loại khỏi nội dung trích xuất. */
  private static final String NOISE_SELECTOR =
      ".breadcrumbs-container, aside.toc, nav.pagination, #copy-url, .edit-this-page";

  /** Giới hạn an toàn số trang duyệt trong một lần crawl. */
  private static final int MAX_PAGES = 1_000;

  /**
   * Ẩn tạm các node nhiễu để lấy đúng rendered text của phần nội dung, sau đó khôi phục lại style
   * ban đầu (non-destructive). Trả về {@code innerText} nên giữ được xuống dòng theo layout, khác
   * với {@code textContent}.
   */
  private static final String EXTRACT_CONTENT_SCRIPT =
      """
          const root = arguments[0];
          const noises = root.querySelectorAll(arguments[1]);
          const saved = [];
          noises.forEach(n => { saved.push([n, n.style.display]); n.style.display = 'none'; });
          const text = root.innerText;
          saved.forEach(entry => { entry[0].style.display = entry[1]; });
          return text;
          """;

  public SpringDocPage(WebDriver driver) {
    super(driver);
  }

  @Override
  protected Duration timeout() {
    return Duration.ofSeconds(30);
  }

  @Override
  protected String homeUrl() {
    return "https://docs.spring.io/spring-framework/reference/index.html";
  }

  // =====================================================================================
  // PageCrawler
  // =====================================================================================

  @Override
  public PageCrawlResult crawl(String url) {
    navigateTo(url);
    return capture(url);
  }

  /**
   * Duyệt và crawl toàn bộ tài liệu trong <b>một lượt đi duy nhất</b>: mỗi trang được mở đúng một
   * lần, vừa trích xuất nội dung vừa lấy liên kết kế tiếp.
   *
   * <p>Hiệu quả hơn việc gọi {@link #collectAllArticleUrls()} rồi {@link #crawl(String)} cho từng
   * URL (tránh mở lại toàn bộ trang lần thứ hai).
   */
  public List<PageCrawlResult> crawlAll() {
    List<PageCrawlResult> results = new ArrayList<>();
    traverse(
        (url, hasArticle) -> {
          if (hasArticle) {
            results.add(capture(url));
          }
        });
    return List.copyOf(results);
  }

  /**
   * Thu thập URL của tất cả trang tài liệu bằng cách đi theo chuỗi phân trang "next".
   *
   * @return danh sách URL tuyệt đối, giữ nguyên thứ tự đọc, đã loại trùng
   */
  public List<String> collectAllArticleUrls() {
    List<String> urls = new ArrayList<>();
    traverse((url, hasArticle) -> urls.add(url));
    return List.copyOf(urls);
  }

  // =====================================================================================
  // Traversal
  // =====================================================================================

  /**
   * Xương sống của cả {@link #crawlAll()} và {@link #collectAllArticleUrls()}: mở từng trang theo
   * chuỗi "next", gọi {@code visitor}, dừng khi hết trang, gặp URL đã duyệt (vòng lặp), hoặc chạm
   * {@link #MAX_PAGES}.
   */
  private void traverse(PageVisitor visitor) {
    Set<String> visited = new LinkedHashSet<>();
    String current = normalize(homeUrl());

    while (current != null && visited.size() < MAX_PAGES) {
      if (!visited.add(current)) {
        break; // đã ghé thăm -> phát hiện vòng lặp, dừng an toàn
      }
      navigateTo(current);
      boolean hasArticle = waitForArticle().isPresent();
      visitor.visit(current, hasArticle);
      current = findNextPageUrl().map(this::normalize).orElse(null);
    }
  }

  /** Chờ vùng nội dung chính xuất hiện; rỗng nếu trang không phải trang tài liệu. */
  private Optional<WebElement> waitForArticle() {
    try {
      return Optional.of(ElementUtil.findElement(driver, ARTICLE_CONTENT_SELECTOR, timeout()));
    } catch (WebDriverException e) {
      return Optional.empty();
    }
  }

  /** Lấy URL tuyệt đối của trang kế tiếp, rỗng nếu đang ở trang cuối. */
  public Optional<String> findNextPageUrl() {
    return findPaginationUrl(NEXT_BUTTON_SELECTOR);
  }

  /** Lấy URL tuyệt đối của trang trước đó, rỗng nếu đang ở trang đầu. */
  public Optional<String> findPreviousPageUrl() {
    return findPaginationUrl(PREV_BUTTON_SELECTOR);
  }

  /** Còn trang kế tiếp hay không. */
  public boolean hasNextPage() {
    return findNextPageUrl().isPresent();
  }

  /**
   * Đọc href từ liên kết phân trang.
   *
   * <p>Dùng {@code getDomProperty("href")} thay vì {@code getDomAttribute("href")}: attribute trả
   * về giá trị thô trong HTML ({@code "core.html"}) còn property được trình duyệt resolve thành URL
   * tuyệt đối ({@code "https://.../reference/core.html"}).
   */
  private Optional<String> findPaginationUrl(By locator) {
    return ElementUtil.findElementIfPresent(driver, locator)
        .map(
            link -> {
              String href = link.getDomProperty("href");
              return (href == null || href.isBlank()) ? link.getDomAttribute("href") : href;
            })
        .filter(href -> !href.isBlank())
        .map(this::toAbsolute);
  }

  /** Trích xuất kết quả của trang hiện tại (giả định đã navigate và article đã load). */
  private PageCrawlResult capture(String url) {
    return new PageCrawlResult(url, getTitle(), getContent());
  }

  // =====================================================================================
  // Extraction
  // =====================================================================================

  /**
   * Lấy tiêu đề từ {@code h1#page-title}; fallback về {@code <title>} của document nếu trang không
   * có heading (ví dụ trang redirect).
   */
  private String getTitle() {
    return ElementUtil.findElementIfPresent(driver, TITLE_SELECTOR)
        .map(ElementUtil::getText)
        .filter(title -> !title.isBlank())
        .orElseGet(
            () -> {
              String docTitle = driver.getTitle();
              return docTitle == null ? "" : docTitle.trim();
            });
  }

  /**
   * Lấy rendered text của {@code article.doc} sau khi loại bỏ breadcrumbs, TOC và pagination.
   *
   * <p>Ưu tiên chạy JavaScript (một round-trip duy nhất, giữ được cấu trúc dòng); nếu driver không
   * hỗ trợ JS hoặc script lỗi thì fallback về {@code WebElement#getText()}.
   */
  private String getContent() {
    Optional<WebElement> article =
        ElementUtil.findElementIfPresent(driver, ARTICLE_CONTENT_SELECTOR);
    if (article.isEmpty()) {
      return "";
    }
    WebElement root = article.get();
    if (driver instanceof JavascriptExecutor js) {
      try {
        Object text = js.executeScript(EXTRACT_CONTENT_SCRIPT, root, NOISE_SELECTOR);
        if (text instanceof String s && !s.isBlank()) {
          return s.strip();
        }
      } catch (WebDriverException ignored) {
        // fallback bên dưới
      }
    }
    return ElementUtil.getText(root);
  }

  /** Resolve href tương đối dựa trên URL hiện tại của trình duyệt. */
  private String toAbsolute(String href) {
    try {
      return URI.create(Objects.requireNonNull(driver.getCurrentUrl())).resolve(href).toString();
    } catch (IllegalArgumentException | NullPointerException e) {
      return href;
    }
  }

  // =====================================================================================
  // URL helpers
  // =====================================================================================

  /**
   * Chuẩn hoá URL để so sánh trùng lặp: bỏ phần fragment ({@code #overview-spring}) vì các anchor
   * cùng trỏ về một trang.
   */
  private String normalize(String url) {
    if (url == null || url.isBlank()) {
      return url;
    }
    try {
      URI uri = new URI(url);
      return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), uri.getQuery(), null)
          .toString();
    } catch (URISyntaxException e) {
      int hash = url.indexOf('#');
      return hash < 0 ? url : url.substring(0, hash);
    }
  }

  /** Callback cho mỗi trang được ghé thăm trong quá trình duyệt. */
  @FunctionalInterface
  private interface PageVisitor {
    void visit(String url, boolean hasArticle);
  }
}
