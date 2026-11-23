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

  /** Lấy ID handle của window/tab hiện tại */
  public static String getWindowHandle(WebDriver driver) {
    return driver.getWindowHandle();
  }

  /** Lấy danh sách ID handles của tất cả window/tab đang mở */
  public static Set<String> getWindowHandles(WebDriver driver) {
    return driver.getWindowHandles();
  }

  /** Chuyển sang window/tab theo Handle ID */
  public static void switchToWindow(WebDriver driver, String windowHandle) {
    driver.switchTo().window(windowHandle);
  }

  /** Đóng window/tab hiện tại */
  public static void close(WebDriver driver) {
    driver.close();
  }

  /** Tạo một Window mới và tự động chuyển sang (Yêu cầu Selenium 4) */
  public static void createNewWindowAndSwitch(WebDriver driver) {
    driver.switchTo().newWindow(WindowType.WINDOW);
  }

  /** Tạo một Tab mới và tự động chuyển sang (Yêu cầu Selenium 4) */
  public static void createNewTabAndSwitch(WebDriver driver) {
    driver.switchTo().newWindow(WindowType.TAB);
  }

  /** Đóng tất cả windows/tabs và kết thúc phiên làm việc Selenium */
  public static void quit(WebDriver driver) {
    if (driver != null) {
      driver.quit();
    }
  }

  // =========================================================================
  // WINDOW MANAGEMENT
  // =========================================================================

  /** Lấy kích thước (Width, Height) của window hiện tại */
  public static Dimension getWindowSize(WebDriver driver) {
    return driver.manage().window().getSize();
  }

  /** Thiết lập kích thước cho window */
  public static void setWindowSize(WebDriver driver, int width, int height) {
    driver.manage().window().setSize(new Dimension(width, height));
  }

  /** Lấy tọa độ góc trên-bên trái (X, Y) của window trên màn hình */
  public static Point getWindowPosition(WebDriver driver) {
    return driver.manage().window().getPosition();
  }

  /** Thiết lập vị trí góc trên-bên trái (X, Y) cho window */
  public static void setWindowPosition(WebDriver driver, int x, int y) {
    driver.manage().window().setPosition(new Point(x, y));
  }

  /** Phóng to cửa sổ trình duyệt (Maximize) */
  public static void maximize(WebDriver driver) {
    driver.manage().window().maximize();
  }

  /** Thu nhỏ cửa sổ trình duyệt xuống taskbar (Minimize) */
  public static void minimize(WebDriver driver) {
    driver.manage().window().minimize();
  }

  /** Bật chế độ Toàn màn hình (Fullscreen - tương đương F11) */
  public static void fullscreen(WebDriver driver) {
    driver.manage().window().fullscreen();
  }

  // =========================================================================
  // SCREENSHOTS
  // =========================================================================

  /**
   * Chụp ảnh toàn bộ Viewport hiện tại và lưu vào đường dẫn đích
   *
   * @param destinationPath Đường dẫn lưu file (VD: "screenshots/page.png")
   */
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

  /**
   * Chụp ảnh một WebElement cụ thể (Yêu cầu Selenium 4)
   *
   * @param element WebElement cần chụp
   * @param destinationPath Đường dẫn lưu file
   */
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

  /** Thực thi JavaScript synchronously */
  public static Object executeScript(WebDriver driver, String script, Object... args) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    return js.executeScript(script, args);
  }

  /** Thực thi JavaScript asynchronously */
  public static Object executeAsyncScript(WebDriver driver, String script, Object... args) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    return js.executeAsyncScript(script, args);
  }

  // =========================================================================
  // PRINT PAGE
  // =========================================================================

  /**
   * In trang hiện tại ra file PDF (Chỉ áp dụng với Chế độ Headless Chromium/Firefox trong Selenium
   * 4)
   *
   * @param destinationPdfPath Đường dẫn lưu file PDF
   */
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
