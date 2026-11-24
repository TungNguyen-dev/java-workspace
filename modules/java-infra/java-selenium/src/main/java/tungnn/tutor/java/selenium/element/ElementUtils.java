package tungnn.tutor.java.selenium.element;

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

public final class ElementUtils {

  public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

  public static final Duration DEFAULT_POLLING = Duration.ofMillis(500);

  private ElementUtils() {
    throw new UnsupportedOperationException("Utility class - khong duoc phep khoi tao");
  }

  // =================================================================================
  // 1. FINDING WEB ELEMENTS
  // =================================================================================

  // --- 1.1 First matching element - Evaluating entire DOM -------------------------

  public static WebElement findElement(WebDriver driver, By locator) {
    requireNonNull(driver, locator);
    return driver.findElement(locator);
  }

  public static WebElement findElement(WebDriver driver, By locator, Duration timeout) {
    requireNonNull(driver, locator);
    return newWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(locator));
  }

  public static WebElement findVisibleElement(WebDriver driver, By locator) {
    return findVisibleElement(driver, locator, DEFAULT_TIMEOUT);
  }

  public static WebElement findVisibleElement(WebDriver driver, By locator, Duration timeout) {
    requireNonNull(driver, locator);
    return newWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
  }

  public static WebElement findClickableElement(WebDriver driver, By locator) {
    return findClickableElement(driver, locator, DEFAULT_TIMEOUT);
  }

  public static WebElement findClickableElement(WebDriver driver, By locator, Duration timeout) {
    requireNonNull(driver, locator);
    return newWait(driver, timeout).until(ExpectedConditions.elementToBeClickable(locator));
  }

  public static Optional<WebElement> findElementIfPresent(SearchContext context, By locator) {
    requireNonNull(context, locator);
    List<WebElement> elements = context.findElements(locator);
    return elements.isEmpty() ? Optional.empty() : Optional.of(elements.getFirst());
  }

  // --- 1.2 Evaluating a subset of the DOM ----------------------------------------

  public static WebElement findElement(SearchContext context, By locator) {
    requireNonNull(context, locator);
    return context.findElement(locator);
  }

  public static WebElement findElementByCss(SearchContext context, String cssSelector) {
    return findElement(context, By.cssSelector(cssSelector));
  }

  public static WebElement findElementByXpath(SearchContext context, String xpath) {
    return findElement(context, By.xpath(xpath));
  }

  // --- 1.3 Evaluating the Shadow DOM (Selenium 4.0+) ------------------------------

  public static SearchContext getShadowRoot(WebElement host) {
    Objects.requireNonNull(host, "host must not be null");
    return host.getShadowRoot();
  }

  public static WebElement findElementInShadowRoot(WebElement host, String cssSelector) {
    return getShadowRoot(host).findElement(By.cssSelector(cssSelector));
  }

  public static List<WebElement> findElementsInShadowRoot(WebElement host, String cssSelector) {
    return getShadowRoot(host).findElements(By.cssSelector(cssSelector));
  }

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

  public static List<WebElement> findElements(SearchContext context, By locator) {
    requireNonNull(context, locator);
    List<WebElement> elements = context.findElements(locator);
    return elements.isEmpty() ? Collections.emptyList() : elements;
  }

  public static List<WebElement> findElements(WebDriver driver, By locator, Duration timeout) {
    requireNonNull(driver, locator);
    return newWait(driver, timeout)
        .until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
  }

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

  public static Optional<WebElement> findFirstMatching(
      SearchContext context, By locator, Predicate<WebElement> predicate) {
    Objects.requireNonNull(predicate, "predicate must not be null");
    return findElements(context, locator).stream().filter(predicate).findFirst();
  }

  public static Optional<WebElement> findFirstByExactText(
      SearchContext context, By locator, String text) {
    return findFirstMatching(context, locator, e -> text != null && text.equals(getText(e)));
  }

  public static Optional<WebElement> findFirstByPartialText(
      SearchContext context, By locator, String partialText) {
    return findFirstMatching(
        context, locator, e -> partialText != null && getText(e).contains(partialText));
  }

  public static List<String> getTexts(SearchContext context, By locator) {
    return findElements(context, locator).stream()
        .map(ElementUtils::getText)
        .collect(Collectors.toList());
  }

  public static int countElements(SearchContext context, By locator) {
    return findElements(context, locator).size();
  }

  // --- 1.5 Find Elements From Element ---------------------------------------------

  public static List<WebElement> findChildElements(WebElement parent, By locator) {
    Objects.requireNonNull(parent, "parent must not be null");
    return findElements(parent, locator);
  }

  public static WebElement findChildElement(WebElement parent, By locator) {
    Objects.requireNonNull(parent, "parent must not be null");
    return parent.findElement(locator);
  }

  // --- 1.6 Get Active Element ------------------------------------------------------

  public static WebElement getActiveElement(WebDriver driver) {
    Objects.requireNonNull(driver, "driver must not be null");
    return driver.switchTo().activeElement();
  }

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

  public static void click(WebDriver driver, By locator) {
    click(driver, locator, DEFAULT_TIMEOUT);
  }

  public static void click(WebDriver driver, By locator, Duration timeout) {
    findClickableElement(driver, locator, timeout).click();
  }

  public static void click(WebElement element) {
    Objects.requireNonNull(element, "element must not be null");
    element.click();
  }

  public static void jsClick(WebDriver driver, WebElement element) {
    javascriptExecutor(driver).executeScript("arguments[0].click();", element);
  }

  public static void scrollIntoView(WebDriver driver, WebElement element) {
    javascriptExecutor(driver)
        .executeScript(
            "arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
  }

  public static void sendKeys(WebDriver driver, By locator, CharSequence... keysToSend) {
    findVisibleElement(driver, locator).sendKeys(keysToSend);
  }

  public static void sendKeys(WebElement element, CharSequence... keysToSend) {
    Objects.requireNonNull(element, "element must not be null");
    element.sendKeys(keysToSend);
  }

  public static void type(WebDriver driver, By locator, String value) {
    WebElement element = findVisibleElement(driver, locator);
    element.clear();
    element.sendKeys(value);
  }

  public static void type(WebElement element, String value) {
    Objects.requireNonNull(element, "element must not be null");
    element.clear();
    element.sendKeys(value);
  }

  public static void clear(WebDriver driver, By locator) {
    findVisibleElement(driver, locator).clear();
  }

  public static void clear(WebElement element) {
    Objects.requireNonNull(element, "element must not be null");
    element.clear();
  }

  public static void submit(WebElement form, By submitButton) {
    Objects.requireNonNull(form, "form must not be null");
    form.findElement(submitButton).click();
  }

  public static void submit(WebElement form) {
    submit(form, By.cssSelector("button[type='submit'], input[type='submit']"));
  }

  // =================================================================================
  // 3. INFORMATION ABOUT WEB ELEMENTS
  // =================================================================================

  public static boolean isDisplayed(SearchContext context, By locator) {
    return findElementIfPresent(context, locator).map(ElementUtils::isDisplayed).orElse(false);
  }

  public static boolean isDisplayed(WebElement element) {
    try {
      return element != null && element.isDisplayed();
    } catch (StaleElementReferenceException | NoSuchElementException e) {
      return false;
    }
  }

  public static boolean isEnabled(SearchContext context, By locator) {
    return findElementIfPresent(context, locator).map(ElementUtils::isEnabled).orElse(false);
  }

  public static boolean isEnabled(WebElement element) {
    try {
      return element != null && element.isEnabled();
    } catch (StaleElementReferenceException | NoSuchElementException e) {
      return false;
    }
  }

  public static boolean isSelected(SearchContext context, By locator) {
    return findElementIfPresent(context, locator).map(ElementUtils::isSelected).orElse(false);
  }

  public static boolean isSelected(WebElement element) {
    try {
      return element != null && element.isSelected();
    } catch (StaleElementReferenceException | NoSuchElementException e) {
      return false;
    }
  }

  public static boolean isPresent(SearchContext context, By locator) {
    return findElementIfPresent(context, locator).isPresent();
  }

  public static String getTagName(WebElement element) {
    Objects.requireNonNull(element, "element must not be null");
    return element.getTagName();
  }

  public static String getActiveElementTagName(WebDriver driver) {
    return getActiveElement(driver).getTagName();
  }

  public static Rectangle getRect(WebElement element) {
    Objects.requireNonNull(element, "element must not be null");
    return element.getRect();
  }

  public static Dimension getSize(WebElement element) {
    return getRect(element).getDimension();
  }

  public static Point getLocation(WebElement element) {
    return getRect(element).getPoint();
  }

  public static String getCssValue(WebElement element, String propertyName) {
    Objects.requireNonNull(element, "element must not be null");
    return element.getCssValue(propertyName);
  }

  public static String getText(WebElement element) {
    try {
      String text = element == null ? null : element.getText();
      return text == null ? "" : text.trim();
    } catch (StaleElementReferenceException e) {
      return "";
    }
  }

  public static String getText(SearchContext context, By locator) {
    return findElementIfPresent(context, locator).map(ElementUtils::getText).orElse("");
  }

  public static String getAttribute(WebElement element, String name) {
    Objects.requireNonNull(element, "element must not be null");
    return element.getAttribute(name);
  }

  public static String getDomAttribute(WebElement element, String name) {
    Objects.requireNonNull(element, "element must not be null");
    return element.getDomAttribute(name);
  }

  public static String getDomProperty(WebElement element, String name) {
    Objects.requireNonNull(element, "element must not be null");
    return element.getDomProperty(name);
  }

  public static String getValue(WebElement element) {
    return getDomProperty(element, "value");
  }

  // =================================================================================
  // 4. FILE UPLOAD
  // =================================================================================

  public static void uploadFile(WebDriver driver, By fileInputLocator, Path file) {
    uploadFile(findElement(driver, fileInputLocator, DEFAULT_TIMEOUT), file);
  }

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

  public static void uploadFiles(WebElement fileInput, List<Path> files) {
    Objects.requireNonNull(files, "files must not be null");
    String joined =
        files.stream()
            .map(p -> p.toAbsolutePath().normalize().toString())
            .collect(Collectors.joining("\n"));
    fileInput.sendKeys(joined);
  }

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

  public static WebDriverWait newWait(WebDriver driver, Duration timeout) {
    Objects.requireNonNull(driver, "driver must not be null");
    return new WebDriverWait(driver, timeout == null ? DEFAULT_TIMEOUT : timeout, DEFAULT_POLLING);
  }

  public static <T> T waitUntil(
      WebDriver driver, ExpectedCondition<T> condition, Duration timeout) {
    return newWait(driver, timeout).until(condition);
  }

  private static JavascriptExecutor javascriptExecutor(WebDriver driver) {
    Objects.requireNonNull(driver, "driver must not be null");
    if (!(driver instanceof JavascriptExecutor)) {
      throw new UnsupportedOperationException("Driver khong ho tro JavascriptExecutor");
    }
    return (JavascriptExecutor) driver;
  }

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
