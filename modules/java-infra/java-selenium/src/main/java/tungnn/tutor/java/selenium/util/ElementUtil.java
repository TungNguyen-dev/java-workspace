package tungnn.tutor.java.selenium.util;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Utility class cung cấp các thao tác thường dùng với {@link WebElement} trong Selenium 4.
 *
 * <p>Class được tổ chức theo 4 nhóm concept:
 *
 * <ol>
 *   <li>Finding web elements (bao gồm subset DOM, Shadow DOM, active element)
 *   <li>Interacting with web elements (click, sendKeys, clear, submit)
 *   <li>Information about web elements (state, tag, rect, css, text, attribute/property)
 *   <li>File upload
 * </ol>
 *
 * <p><b>Quy ước thiết kế</b>
 *
 * <ul>
 *   <li>Stateless, thread-safe: không giữ {@code WebDriver} ở field, driver luôn được truyền vào.
 *   <li>Các method {@code findXxx} nhận {@link SearchContext} để dùng chung cho cả {@code
 *       WebDriver} (toàn bộ DOM) và {@code WebElement} (subset DOM).
 *   <li>Các method {@code isXxx} là fail-safe: trả về {@code false} thay vì ném exception khi
 *       element không tồn tại / stale.
 *   <li>Overload có {@link Duration} luôn áp dụng explicit wait; overload không có Duration dùng
 *       {@link #DEFAULT_TIMEOUT}.
 * </ul>
 */
public final class ElementUtil {

  /** Timeout mặc định cho các explicit wait. */
  public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

  /** Chu kỳ polling mặc định của {@link WebDriverWait}. */
  public static final Duration DEFAULT_POLLING = Duration.ofMillis(500);

  private ElementUtil() {
    throw new UnsupportedOperationException("Utility class - khong duoc phep khoi tao");
  }

  // =================================================================================
  // 1. FINDING WEB ELEMENTS
  // =================================================================================

  // --- 1.1 First matching element - Evaluating entire DOM -------------------------

  /**
   * Tìm phần tử khớp đầu tiên trên toàn bộ DOM (không wait).
   *
   * @throws NoSuchElementException nếu không tìm thấy
   */
  public static WebElement findElement(WebDriver driver, By locator) {
    requireNonNull(driver, locator);
    return driver.findElement(locator);
  }

  /** Tìm phần tử khớp đầu tiên, có explicit wait cho tới khi element hiện diện trong DOM. */
  public static WebElement findElement(WebDriver driver, By locator, Duration timeout) {
    requireNonNull(driver, locator);
    return newWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(locator));
  }

  /** Tìm phần tử và chờ tới khi nó thực sự hiển thị (visible) trên viewport. */
  public static WebElement findVisibleElement(WebDriver driver, By locator) {
    return findVisibleElement(driver, locator, DEFAULT_TIMEOUT);
  }

  /** Tìm phần tử và chờ tới khi nó thực sự hiển thị (visible) trên viewport. */
  public static WebElement findVisibleElement(WebDriver driver, By locator, Duration timeout) {
    requireNonNull(driver, locator);
    return newWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
  }

  /** Tìm phần tử và chờ tới khi nó clickable (visible + enabled). */
  public static WebElement findClickableElement(WebDriver driver, By locator) {
    return findClickableElement(driver, locator, DEFAULT_TIMEOUT);
  }

  /** Tìm phần tử và chờ tới khi nó clickable (visible + enabled). */
  public static WebElement findClickableElement(WebDriver driver, By locator, Duration timeout) {
    requireNonNull(driver, locator);
    return newWait(driver, timeout).until(ExpectedConditions.elementToBeClickable(locator));
  }

  /**
   * Biến thể không ném exception: trả về {@link Optional#empty()} nếu không tìm thấy. Phù hợp cho
   * các luồng điều kiện (optional banner, popup, ...).
   */
  public static Optional<WebElement> findElementIfPresent(SearchContext context, By locator) {
    requireNonNull(context, locator);
    List<WebElement> elements = context.findElements(locator);
    return elements.isEmpty() ? Optional.empty() : Optional.of(elements.getFirst());
  }

  // --- 1.2 Evaluating a subset of the DOM ----------------------------------------

  /**
   * Tìm phần tử con trong ngữ cảnh của một phần tử cha (subset DOM).
   *
   * <p><b>Lưu ý hiệu năng:</b> nested lookup tốn thêm một vòng round-trip tới driver. Nếu có thể,
   * hãy dùng {@link #findElementByCss(SearchContext, String)} hoặc XPath để gộp thành một lệnh duy
   * nhất (optimized locator).
   */
  public static WebElement findElement(SearchContext context, By locator) {
    requireNonNull(context, locator);
    return context.findElement(locator);
  }

  /** Optimized locator: tìm một lệnh duy nhất bằng CSS Selector. */
  public static WebElement findElementByCss(SearchContext context, String cssSelector) {
    return findElement(context, By.cssSelector(cssSelector));
  }

  /** Optimized locator: tìm một lệnh duy nhất bằng XPath. */
  public static WebElement findElementByXpath(SearchContext context, String xpath) {
    return findElement(context, By.xpath(xpath));
  }

  // --- 1.3 Evaluating the Shadow DOM (Selenium 4.0+) ------------------------------

  /**
   * Lấy shadow root của một host element.
   *
   * @return {@link SearchContext} đại diện cho cây Shadow DOM được đóng gói
   */
  public static SearchContext getShadowRoot(WebElement host) {
    Objects.requireNonNull(host, "host must not be null");
    return host.getShadowRoot();
  }

  /**
   * Tìm phần tử bên trong Shadow DOM của {@code host}.
   *
   * <p><b>Ràng buộc:</b> WebDriver spec chỉ hỗ trợ {@code css selector} khi truy vấn từ shadow root
   * (không dùng được XPath).
   */
  public static WebElement findElementInShadowRoot(WebElement host, String cssSelector) {
    return getShadowRoot(host).findElement(By.cssSelector(cssSelector));
  }

  /** Tìm tất cả phần tử khớp bên trong Shadow DOM của {@code host}. */
  public static List<WebElement> findElementsInShadowRoot(WebElement host, String cssSelector) {
    return getShadowRoot(host).findElements(By.cssSelector(cssSelector));
  }

  /** Tìm phần tử trong Shadow DOM lồng nhau, đi lần lượt theo chuỗi CSS selector. */
  public static WebElement findElementInNestedShadowRoot(WebElement host, String... cssSelectors) {
    Objects.requireNonNull(host, "host must not be null");
    if (cssSelectors == null || cssSelectors.length == 0) {
      throw new IllegalArgumentException("cssSelectors must not be empty");
    }
    WebElement current = host;
    for (String cssSelector : cssSelectors) {
      current = getShadowRoot(current).findElement(By.cssSelector(cssSelector));
    }
    return current;
  }

  // --- 1.4 All matching elements --------------------------------------------------

  /**
   * Trả về tất cả phần tử khớp trong ngữ cảnh chỉ định. Không tìm thấy thì trả về danh sách rỗng
   * (không ném exception).
   */
  public static List<WebElement> findElements(SearchContext context, By locator) {
    requireNonNull(context, locator);
    List<WebElement> elements = context.findElements(locator);
    return elements.isEmpty() ? Collections.emptyList() : elements;
  }

  /** Chờ tới khi có ít nhất một phần tử khớp rồi trả về toàn bộ danh sách. */
  public static List<WebElement> findElements(WebDriver driver, By locator, Duration timeout) {
    requireNonNull(driver, locator);
    return newWait(driver, timeout)
        .until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
  }

  /** Get element: lấy phần tử tại vị trí {@code index} trong collection kết quả. */
  public static WebElement findElementAt(SearchContext context, By locator, int index) {
    List<WebElement> elements = findElements(context, locator);
    if (index < 0 || index >= elements.size()) {
      throw new NoSuchElementException(
          "Khong ton tai element tai index "
              + index
              + " cho locator "
              + locator
              + " (tim thay "
              + elements.size()
              + " element)");
    }
    return elements.get(index);
  }

  /** Get element: lặp qua collection và lấy phần tử đầu tiên thoả điều kiện. */
  public static Optional<WebElement> findFirstMatching(
      SearchContext context, By locator, Predicate<WebElement> predicate) {
    Objects.requireNonNull(predicate, "predicate must not be null");
    return findElements(context, locator).stream().filter(predicate).findFirst();
  }

  /** Get element: lấy phần tử đầu tiên có text hiển thị khớp chính xác (đã trim). */
  public static Optional<WebElement> findFirstByExactText(
      SearchContext context, By locator, String text) {
    return findFirstMatching(context, locator, e -> text != null && text.equals(getText(e)));
  }

  /** Get element: lấy phần tử đầu tiên có text hiển thị chứa {@code partialText}. */
  public static Optional<WebElement> findFirstByPartialText(
      SearchContext context, By locator, String partialText) {
    return findFirstMatching(
        context, locator, e -> partialText != null && getText(e).contains(partialText));
  }

  /** Tiện ích: trích danh sách text hiển thị của toàn bộ phần tử khớp. */
  public static List<String> getTexts(SearchContext context, By locator) {
    return findElements(context, locator).stream()
        .map(ElementUtil::getText)
        .collect(Collectors.toList());
  }

  /** Đếm số phần tử khớp (0 nếu không có). */
  public static int countElements(SearchContext context, By locator) {
    return findElements(context, locator).size();
  }

  // --- 1.5 Find Elements From Element ---------------------------------------------

  /** Tìm danh sách phần tử con nằm trong ngữ cảnh của một phần tử cha. */
  public static List<WebElement> findChildElements(WebElement parent, By locator) {
    Objects.requireNonNull(parent, "parent must not be null");
    return findElements(parent, locator);
  }

  /** Tìm phần tử con đầu tiên trong ngữ cảnh của một phần tử cha. */
  public static WebElement findChildElement(WebElement parent, By locator) {
    Objects.requireNonNull(parent, "parent must not be null");
    return parent.findElement(locator);
  }

  // --- 1.6 Get Active Element ------------------------------------------------------

  /** Lấy phần tử DOM đang có focus trong browsing context hiện tại. */
  public static WebElement getActiveElement(WebDriver driver) {
    Objects.requireNonNull(driver, "driver must not be null");
    return driver.switchTo().activeElement();
  }

  /** Kiểm tra {@code element} có đang là active element (đang focus) hay không. */
  public static boolean isFocused(WebDriver driver, WebElement element) {
    try {
      return getActiveElement(driver).equals(element);
    } catch (StaleElementReferenceException | NoSuchElementException e) {
      return false;
    }
  }

  // =================================================================================
  // 2. INTERACTING WITH WEB ELEMENTS
  // =================================================================================

  /**
   * Click vào tâm phần tử sau khi đã chờ trạng thái clickable. Selenium tự scroll element vào
   * viewport và kiểm tra khả năng tương tác.
   */
  public static void click(WebDriver driver, By locator) {
    click(driver, locator, DEFAULT_TIMEOUT);
  }

  /** Click vào tâm phần tử sau khi đã chờ trạng thái clickable trong {@code timeout}. */
  public static void click(WebDriver driver, By locator, Duration timeout) {
    findClickableElement(driver, locator, timeout).click();
  }

  /** Click trực tiếp trên element đã có tham chiếu. */
  public static void click(WebElement element) {
    Objects.requireNonNull(element, "element must not be null");
    element.click();
  }

  /**
   * Click bằng JavaScript - dùng làm phương án dự phòng khi tâm phần tử bị che khuất (overlay,
   * sticky header...) khiến native click ném {@code ElementClickInterceptedException}.
   */
  public static void jsClick(WebDriver driver, WebElement element) {
    javascriptExecutor(driver).executeScript("arguments[0].click();", element);
  }

  /** Cuộn phần tử vào giữa viewport (hữu ích trước khi assert hoặc chụp ảnh). */
  public static void scrollIntoView(WebDriver driver, WebElement element) {
    javascriptExecutor(driver)
        .executeScript(
            "arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
  }

  /** Gõ chuỗi ký tự vào phần tử (không xoá nội dung cũ). */
  public static void sendKeys(WebDriver driver, By locator, CharSequence... keysToSend) {
    findVisibleElement(driver, locator).sendKeys(keysToSend);
  }

  /** Gõ chuỗi ký tự vào phần tử đã có tham chiếu. */
  public static void sendKeys(WebElement element, CharSequence... keysToSend) {
    Objects.requireNonNull(element, "element must not be null");
    element.sendKeys(keysToSend);
  }

  /** Xoá nội dung cũ rồi gõ giá trị mới - pattern phổ biến nhất khi nhập form. */
  public static void type(WebDriver driver, By locator, String value) {
    WebElement element = findVisibleElement(driver, locator);
    element.clear();
    element.sendKeys(value);
  }

  /** Xoá nội dung cũ rồi gõ giá trị mới trên element đã có tham chiếu. */
  public static void type(WebElement element, String value) {
    Objects.requireNonNull(element, "element must not be null");
    element.clear();
    element.sendKeys(value);
  }

  /** Reset nội dung của phần tử (yêu cầu element editable và resettable). */
  public static void clear(WebDriver driver, By locator) {
    findVisibleElement(driver, locator).clear();
  }

  /** Reset nội dung của phần tử đã có tham chiếu. */
  public static void clear(WebElement element) {
    Objects.requireNonNull(element, "element must not be null");
    element.clear();
  }

  /**
   * Submit form theo cách được khuyến nghị ở Selenium 4: click trực tiếp vào submit button. Không
   * dùng {@code WebElement#submit()} (đã bị deprecated về mặt thực hành).
   *
   * @param form form chứa nút submit
   * @param submitButton locator của nút submit trong phạm vi form
   */
  public static void submit(WebElement form, By submitButton) {
    Objects.requireNonNull(form, "form must not be null");
    form.findElement(submitButton).click();
  }

  /** Submit form bằng cách click nút {@code button[type='submit']} nằm trong form. */
  public static void submit(WebElement form) {
    submit(form, By.cssSelector("button[type='submit'], input[type='submit']"));
  }

  // =================================================================================
  // 3. INFORMATION ABOUT WEB ELEMENTS
  // =================================================================================

  /** Fail-safe: phần tử có đang hiển thị hay không (false nếu không tồn tại / stale). */
  public static boolean isDisplayed(SearchContext context, By locator) {
    return findElementIfPresent(context, locator).map(ElementUtil::isDisplayed).orElse(false);
  }

  /** Fail-safe: phần tử có đang hiển thị hay không. */
  public static boolean isDisplayed(WebElement element) {
    try {
      return element != null && element.isDisplayed();
    } catch (StaleElementReferenceException | NoSuchElementException e) {
      return false;
    }
  }

  /** Fail-safe: phần tử đang enabled hay bị disabled. */
  public static boolean isEnabled(SearchContext context, By locator) {
    return findElementIfPresent(context, locator).map(ElementUtil::isEnabled).orElse(false);
  }

  /** Fail-safe: phần tử đang enabled hay bị disabled. */
  public static boolean isEnabled(WebElement element) {
    try {
      return element != null && element.isEnabled();
    } catch (StaleElementReferenceException | NoSuchElementException e) {
      return false;
    }
  }

  /** Fail-safe: checkbox / radio / option có đang được chọn hay không. */
  public static boolean isSelected(SearchContext context, By locator) {
    return findElementIfPresent(context, locator).map(ElementUtil::isSelected).orElse(false);
  }

  /** Fail-safe: checkbox / radio / option có đang được chọn hay không. */
  public static boolean isSelected(WebElement element) {
    try {
      return element != null && element.isSelected();
    } catch (StaleElementReferenceException | NoSuchElementException e) {
      return false;
    }
  }

  /** Kiểm tra phần tử có tồn tại trong DOM hay không (không quan tâm visibility). */
  public static boolean isPresent(SearchContext context, By locator) {
    return findElementIfPresent(context, locator).isPresent();
  }

  /** Lấy tên thẻ HTML của phần tử. */
  public static String getTagName(WebElement element) {
    Objects.requireNonNull(element, "element must not be null");
    return element.getTagName();
  }

  /** Lấy tên thẻ HTML của phần tử đang có focus. */
  public static String getActiveElementTagName(WebDriver driver) {
    return getActiveElement(driver).getTagName();
  }

  /** Lấy đồng thời kích thước và toạ độ của phần tử. */
  public static Rectangle getRect(WebElement element) {
    Objects.requireNonNull(element, "element must not be null");
    return element.getRect();
  }

  /** Lấy kích thước (width, height) của phần tử. */
  public static Dimension getSize(WebElement element) {
    return getRect(element).getDimension();
  }

  /** Lấy toạ độ góc trên bên trái (x, y) của phần tử. */
  public static Point getLocation(WebElement element) {
    return getRect(element).getPoint();
  }

  /** Lấy giá trị computed style của một CSS property. */
  public static String getCssValue(WebElement element, String propertyName) {
    Objects.requireNonNull(element, "element must not be null");
    return element.getCssValue(propertyName);
  }

  /** Lấy rendered text của phần tử, đã trim; trả về chuỗi rỗng nếu element stale. */
  public static String getText(WebElement element) {
    try {
      String text = element == null ? null : element.getText();
      return text == null ? "" : text.trim();
    } catch (StaleElementReferenceException e) {
      return "";
    }
  }

  /** Lấy rendered text của phần tử khớp locator, chuỗi rỗng nếu không tìm thấy. */
  public static String getText(SearchContext context, By locator) {
    return findElementIfPresent(context, locator).map(ElementUtil::getText).orElse("");
  }

  /**
   * Lấy giá trị attribute/property theo cơ chế hợp nhất của Selenium (ưu tiên property, fallback về
   * attribute).
   */
  public static String getAttribute(WebElement element, String name) {
    Objects.requireNonNull(element, "element must not be null");
    return element.getAttribute(name);
  }

  /** Lấy đúng giá trị attribute khai báo trong HTML (không bị JS làm thay đổi). */
  public static String getDomAttribute(WebElement element, String name) {
    Objects.requireNonNull(element, "element must not be null");
    return element.getDomAttribute(name);
  }

  /** Lấy giá trị property thời gian thực của DOM node (ví dụ {@code value} sau khi user gõ). */
  public static String getDomProperty(WebElement element, String name) {
    Objects.requireNonNull(element, "element must not be null");
    return element.getDomProperty(name);
  }

  /** Lấy giá trị hiện tại của input (dùng DOM property để đảm bảo realtime). */
  public static String getValue(WebElement element) {
    return getDomProperty(element, "value");
  }

  // =================================================================================
  // 4. FILE UPLOAD
  // =================================================================================

  /**
   * Upload file bằng cách gửi <b>đường dẫn tuyệt đối</b> tới input {@code type="file"}. Selenium
   * không thao tác được với hộp thoại chọn file của OS nên đây là cách chuẩn.
   *
   * @throws IllegalArgumentException nếu file không tồn tại hoặc element không phải input file
   */
  public static void uploadFile(WebDriver driver, By fileInputLocator, Path file) {
    uploadFile(findElement(driver, fileInputLocator, DEFAULT_TIMEOUT), file);
  }

  /** Upload file lên input đã có tham chiếu. */
  public static void uploadFile(WebElement fileInput, Path file) {
    Objects.requireNonNull(fileInput, "fileInput must not be null");
    Objects.requireNonNull(file, "file must not be null");

    File absolute = file.toAbsolutePath().normalize().toFile();
    if (!absolute.isFile()) {
      throw new IllegalArgumentException("File khong ton tai: " + absolute);
    }
    if (!"input".equalsIgnoreCase(fileInput.getTagName())
        || !"file".equalsIgnoreCase(String.valueOf(fileInput.getDomAttribute("type")))) {
      throw new IllegalArgumentException("Element phai la <input type=\"file\">");
    }
    fileInput.sendKeys(absolute.getAbsolutePath());
  }

  /** Upload nhiều file cùng lúc (input phải có thuộc tính {@code multiple}). */
  public static void uploadFiles(WebElement fileInput, List<Path> files) {
    Objects.requireNonNull(files, "files must not be null");
    String joined =
        files.stream()
            .map(p -> p.toAbsolutePath().normalize().toString())
            .collect(Collectors.joining("\n"));
    fileInput.sendKeys(joined);
  }

  /**
   * Hiện input file đang bị ẩn (pattern phổ biến: input bị {@code display:none}, UI chỉ show một
   * button custom) rồi mới sendKeys.
   */
  public static void uploadHiddenFile(WebDriver driver, By fileInputLocator, Path file) {
    WebElement fileInput = findElement(driver, fileInputLocator, DEFAULT_TIMEOUT);
    javascriptExecutor(driver)
        .executeScript(
            "arguments[0].style.display='block';"
                + "arguments[0].style.visibility='visible';"
                + "arguments[0].style.height='1px';"
                + "arguments[0].style.width='1px';"
                + "arguments[0].style.opacity=1;",
            fileInput);
    uploadFile(fileInput, file);
  }

  // =================================================================================
  // INTERNAL HELPERS
  // =================================================================================

  /** Tạo {@link WebDriverWait} với polling mặc định. */
  public static WebDriverWait newWait(WebDriver driver, Duration timeout) {
    Objects.requireNonNull(driver, "driver must not be null");
    return new WebDriverWait(driver, timeout == null ? DEFAULT_TIMEOUT : timeout, DEFAULT_POLLING);
  }

  /** Chờ một điều kiện tuỳ biến, dùng khi các ExpectedConditions có sẵn không đủ. */
  public static <T> T waitUntil(
      WebDriver driver, ExpectedCondition<T> condition, Duration timeout) {
    return newWait(driver, timeout).until(condition);
  }

  /** Lấy {@link JavascriptExecutor} từ driver; suy ra driver từ element nếu cần. */
  private static JavascriptExecutor javascriptExecutor(WebDriver driver) {
    Objects.requireNonNull(driver, "driver must not be null");
    if (!(driver instanceof JavascriptExecutor)) {
      throw new UnsupportedOperationException("Driver khong ho tro JavascriptExecutor");
    }
    return (JavascriptExecutor) driver;
  }

  /** Suy ra {@link WebDriver} gốc từ một {@link WebElement} (hữu ích khi API chỉ nhận element). */
  public static WebDriver unwrapDriver(WebElement element) {
    Objects.requireNonNull(element, "element must not be null");
    if (element instanceof WrapsDriver) {
      return ((WrapsDriver) element).getWrappedDriver();
    }
    throw new UnsupportedOperationException("Khong the suy ra WebDriver tu element: " + element);
  }

  private static void requireNonNull(SearchContext context, By locator) {
    Objects.requireNonNull(context, "searchContext must not be null");
    Objects.requireNonNull(locator, "locator must not be null");
  }
}
