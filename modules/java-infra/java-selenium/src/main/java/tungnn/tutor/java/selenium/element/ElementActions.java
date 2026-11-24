package tungnn.tutor.java.selenium.element;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.openqa.selenium.*;
import tungnn.tutor.java.selenium.wait.Waits;

public final class ElementActions {

    private ElementActions() {
        throw new UnsupportedOperationException("Utility class must not be instantiated");
    }

    public static void click(WebDriver driver, By locator) {
        click(driver, locator, Waits.DEFAULT_TIMEOUT);
    }

    public static void click(WebDriver driver, By locator, Duration timeout) {
        ElementFinder.findClickableElement(driver, locator, timeout).click();
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
                .executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
    }

    public static void sendKeys(WebDriver driver, By locator, CharSequence... keysToSend) {
        ElementFinder.findVisibleElement(driver, locator).sendKeys(keysToSend);
    }

    public static void sendKeys(WebElement element, CharSequence... keysToSend) {
        Objects.requireNonNull(element, "element must not be null");
        element.sendKeys(keysToSend);
    }

    public static void type(WebDriver driver, By locator, String value) {
        WebElement element = ElementFinder.findVisibleElement(driver, locator);
        element.clear();
        element.sendKeys(value);
    }

    public static void type(WebElement element, String value) {
        Objects.requireNonNull(element, "element must not be null");
        element.clear();
        element.sendKeys(value);
    }

    public static void clear(WebDriver driver, By locator) {
        ElementFinder.findVisibleElement(driver, locator).clear();
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

    public static void uploadFile(WebDriver driver, By fileInputLocator, Path file) {
        uploadFile(ElementFinder.findElement(driver, fileInputLocator, Waits.DEFAULT_TIMEOUT), file);
    }

    public static void uploadFile(WebElement fileInput, Path file) {
        Objects.requireNonNull(fileInput, "fileInput must not be null");
        Objects.requireNonNull(file, "file must not be null");

        File absolute = file.toAbsolutePath().normalize().toFile();
        if (!absolute.isFile()) {
            throw new IllegalArgumentException("File does not exist: " + absolute);
        }
        if (!"input".equalsIgnoreCase(fileInput.getTagName())
                || !"file".equalsIgnoreCase(String.valueOf(fileInput.getDomAttribute("type")))) {
            throw new IllegalArgumentException("Element must be <input type=\"file\">");
        }
        fileInput.sendKeys(absolute.getAbsolutePath());
    }

    public static void uploadFiles(WebElement fileInput, List<Path> files) {
        Objects.requireNonNull(files, "files must not be null");
        String joined = files.stream()
                .map(p -> p.toAbsolutePath().normalize().toString())
                .collect(Collectors.joining("\n"));
        fileInput.sendKeys(joined);
    }

    public static void uploadHiddenFile(WebDriver driver, By fileInputLocator, Path file) {
        WebElement fileInput = ElementFinder.findElement(driver, fileInputLocator, Waits.DEFAULT_TIMEOUT);
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

    private static JavascriptExecutor javascriptExecutor(WebDriver driver) {
        Objects.requireNonNull(driver, "driver must not be null");
        if (!(driver instanceof JavascriptExecutor)) {
            throw new UnsupportedOperationException("Driver does not support JavascriptExecutor");
        }
        return (JavascriptExecutor) driver;
    }
}
