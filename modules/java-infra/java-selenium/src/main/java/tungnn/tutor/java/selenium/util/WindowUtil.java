package tungnn.tutor.java.selenium.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import org.openqa.selenium.*;
import org.openqa.selenium.print.PrintOptions;

public class WindowUtil {

  private WindowUtil() {
    // Private constructor để ngăn tạo instance
  }

  // =========================================================================
  // WINDOWS AND TABS
  // =========================================================================

  public static String getWindowHandle(WebDriver driver) {
    return driver.getWindowHandle();
  }

  public static Set<String> getWindowHandles(WebDriver driver) {
    return driver.getWindowHandles();
  }

  public static void switchToWindow(WebDriver driver, String windowHandle) {
    driver.switchTo().window(windowHandle);
  }

  public static void close(WebDriver driver) {
    driver.close();
  }

  public static void createNewWindowAndSwitch(WebDriver driver) {
    driver.switchTo().newWindow(WindowType.WINDOW);
  }

  public static void createNewTabAndSwitch(WebDriver driver) {
    driver.switchTo().newWindow(WindowType.TAB);
  }

  public static void quit(WebDriver driver) {
    if (driver != null) {
      driver.quit();
    }
  }

  // =========================================================================
  // WINDOW MANAGEMENT
  // =========================================================================

  public static Dimension getWindowSize(WebDriver driver) {
    return driver.manage().window().getSize();
  }

  public static void setWindowSize(WebDriver driver, int width, int height) {
    driver.manage().window().setSize(new Dimension(width, height));
  }

  public static Point getWindowPosition(WebDriver driver) {
    return driver.manage().window().getPosition();
  }

  public static void setWindowPosition(WebDriver driver, int x, int y) {
    driver.manage().window().setPosition(new Point(x, y));
  }

  public static void maximize(WebDriver driver) {
    driver.manage().window().maximize();
  }

  public static void minimize(WebDriver driver) {
    driver.manage().window().minimize();
  }

  public static void fullscreen(WebDriver driver) {
    driver.manage().window().fullscreen();
  }

  // =========================================================================
  // SCREENSHOTS
  // =========================================================================

  public static File takeScreenshot(WebDriver driver, String destinationPath) throws IOException {
    TakesScreenshot ts = (TakesScreenshot) driver;
    File source = ts.getScreenshotAs(OutputType.FILE);
    File destination = new File(destinationPath);

    // Tạo thư mục cha nếu chưa tồn tại
    if (destination.getParentFile() != null) {
      destination.getParentFile().mkdirs();
    }

    Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    return destination;
  }

  public static File takeElementScreenshot(WebElement element, String destinationPath)
      throws IOException {
    File source = element.getScreenshotAs(OutputType.FILE);
    File destination = new File(destinationPath);

    if (destination.getParentFile() != null) {
      destination.getParentFile().mkdirs();
    }

    Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
    return destination;
  }

  // =========================================================================
  // EXECUTE SCRIPT
  // =========================================================================

  public static Object executeScript(WebDriver driver, String script, Object... args) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    return js.executeScript(script, args);
  }

  public static Object executeAsyncScript(WebDriver driver, String script, Object... args) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    return js.executeAsyncScript(script, args);
  }

  // =========================================================================
  // PRINT PAGE
  // =========================================================================

  public static File printPage(WebDriver driver, String destinationPdfPath) throws IOException {
    PrintsPage printer = (PrintsPage) driver;
    PrintOptions printOptions = new PrintOptions();

    Pdf pdf = printer.print(printOptions);
    byte[] pdfBytes = java.util.Base64.getDecoder().decode(pdf.getContent());

    File destination = new File(destinationPdfPath);
    if (destination.getParentFile() != null) {
      destination.getParentFile().mkdirs();
    }

    Files.write(destination.toPath(), pdfBytes);
    return destination;
  }
}
