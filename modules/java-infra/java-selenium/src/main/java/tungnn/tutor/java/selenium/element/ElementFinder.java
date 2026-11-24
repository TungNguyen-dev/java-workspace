package tungnn.tutor.java.selenium.element;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import tungnn.tutor.java.selenium.wait.Waits;

public final class ElementFinder {

    private ElementFinder() {
        throw new UnsupportedOperationException("Utility class must not be instantiated");
    }

    // --- 1.1 First matching element - Evaluating entire DOM -------------------------

    public static WebElement findElement(WebDriver driver, By locator) {
        requireNonNull(driver, locator);
        return driver.findElement(locator);
    }

    public static WebElement findElement(WebDriver driver, By locator, Duration timeout) {
        requireNonNull(driver, locator);
        return Waits.newWait(driver, timeout).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static WebElement findVisibleElement(WebDriver driver, By locator) {
        return findVisibleElement(driver, locator, Waits.DEFAULT_TIMEOUT);
    }

    public static WebElement findVisibleElement(WebDriver driver, By locator, Duration timeout) {
        requireNonNull(driver, locator);
        return Waits.newWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement findClickableElement(WebDriver driver, By locator) {
        return findClickableElement(driver, locator, Waits.DEFAULT_TIMEOUT);
    }

    public static WebElement findClickableElement(WebDriver driver, By locator, Duration timeout) {
        requireNonNull(driver, locator);
        return Waits.newWait(driver, timeout).until(ExpectedConditions.elementToBeClickable(locator));
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
        return Waits.newWait(driver, timeout).until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public static WebElement findElementAt(SearchContext context, By locator, int index) {
        List<WebElement> elements = findElements(context, locator);
        if (index < 0 || index >= elements.size()) {
            throw new NoSuchElementException("No element at index "
                    + index
                    + " for locator "
                    + locator
                    + " (found "
                    + elements.size()
                    + " elements)");
        }
        return elements.get(index);
    }

    public static Optional<WebElement> findFirstMatching(
            SearchContext context, By locator, Predicate<WebElement> predicate) {
        Objects.requireNonNull(predicate, "predicate must not be null");
        return findElements(context, locator).stream().filter(predicate).findFirst();
    }

    public static Optional<WebElement> findFirstByExactText(SearchContext context, By locator, String text) {
        return findFirstMatching(context, locator, e -> text != null && text.equals(ElementInfo.getText(e)));
    }

    public static Optional<WebElement> findFirstByPartialText(SearchContext context, By locator, String partialText) {
        return findFirstMatching(
                context,
                locator,
                e -> partialText != null && ElementInfo.getText(e).contains(partialText));
    }

    public static List<String> getTexts(SearchContext context, By locator) {
        return findElements(context, locator).stream().map(ElementInfo::getText).collect(Collectors.toList());
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

    private static void requireNonNull(SearchContext context, By locator) {
        Objects.requireNonNull(context, "searchContext must not be null");
        Objects.requireNonNull(locator, "locator must not be null");
    }
}
