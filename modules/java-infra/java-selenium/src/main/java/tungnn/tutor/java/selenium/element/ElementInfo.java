package tungnn.tutor.java.selenium.element;

import java.util.Objects;
import org.openqa.selenium.*;

public final class ElementInfo {

    private ElementInfo() {
        throw new UnsupportedOperationException("Utility class must not be instantiated");
    }

    public static boolean isDisplayed(SearchContext context, By locator) {
        return ElementFinder.findElementIfPresent(context, locator)
                .map(ElementInfo::isDisplayed)
                .orElse(false);
    }

    public static boolean isDisplayed(WebElement element) {
        try {
            return element != null && element.isDisplayed();
        } catch (StaleElementReferenceException | NoSuchElementException e) {
            return false;
        }
    }

    public static boolean isEnabled(SearchContext context, By locator) {
        return ElementFinder.findElementIfPresent(context, locator)
                .map(ElementInfo::isEnabled)
                .orElse(false);
    }

    public static boolean isEnabled(WebElement element) {
        try {
            return element != null && element.isEnabled();
        } catch (StaleElementReferenceException | NoSuchElementException e) {
            return false;
        }
    }

    public static boolean isSelected(SearchContext context, By locator) {
        return ElementFinder.findElementIfPresent(context, locator)
                .map(ElementInfo::isSelected)
                .orElse(false);
    }

    public static boolean isSelected(WebElement element) {
        try {
            return element != null && element.isSelected();
        } catch (StaleElementReferenceException | NoSuchElementException e) {
            return false;
        }
    }

    public static boolean isPresent(SearchContext context, By locator) {
        return ElementFinder.findElementIfPresent(context, locator).isPresent();
    }

    public static String getTagName(WebElement element) {
        Objects.requireNonNull(element, "element must not be null");
        return element.getTagName();
    }

    public static String getActiveElementTagName(WebDriver driver) {
        return ElementFinder.getActiveElement(driver).getTagName();
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
        return ElementFinder.findElementIfPresent(context, locator)
                .map(ElementInfo::getText)
                .orElse("");
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

    public static WebDriver unwrapDriver(WebElement element) {
        Objects.requireNonNull(element, "element must not be null");
        if (element instanceof WrapsDriver) {
            return ((WrapsDriver) element).getWrappedDriver();
        }
        throw new UnsupportedOperationException("Cannot resolve WebDriver from element: " + element);
    }
}
