package tungnn.tutor.java.selenium.util;

import java.time.Duration;
import java.util.Set;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class DriverUtil {

  private DriverUtil() {
    // Private constructor để ngăn việc khởi tạo đối tượng
  }

  // =========================================================================
  // NAVIGATION
  // =========================================================================

  /** Navigate to specified URL */
  public static void navigateTo(WebDriver driver, String url) {
    driver.navigate().to(url);
  }

  /** Navigate Back */
  public static void back(WebDriver driver) {
    driver.navigate().back();
  }

  /** Navigate Forward */
  public static void forward(WebDriver driver) {
    driver.navigate().forward();
  }

  /** Refresh page */
  public static void refresh(WebDriver driver) {
    driver.navigate().refresh();
  }

  // =========================================================================
  // ALERTS (Xử lý Alert/Confirm/Prompt/BeforeUnload)
  // =========================================================================

  /**
   * Chờ và chuyển hướng điều khiển sang Alert (dùng cho cả Alert, Confirm, Prompt, BeforeUnload)
   */
  public static Alert switchToAlert(WebDriver driver, Duration timeout) {
    WebDriverWait wait = new WebDriverWait(driver, timeout);
    return wait.until(ExpectedConditions.alertIsPresent());
  }

  /** Đồng ý Alert / Confirm / Prompt */
  public static void acceptAlert(WebDriver driver, Duration timeout) {
    switchToAlert(driver, timeout).accept();
  }

  /** Bỏ qua / Hủy Alert / Confirm / Prompt */
  public static void dismissAlert(WebDriver driver, Duration timeout) {
    switchToAlert(driver, timeout).dismiss();
  }

  /** Lấy văn bản hiển thị trên Alert */
  public static String getAlertText(WebDriver driver, Duration timeout) {
    return switchToAlert(driver, timeout).getText();
  }

  /** Điền thông tin vào Prompt Alert và ấn OK */
  public static void sendKeysToPrompt(WebDriver driver, String text, Duration timeout) {
    Alert alert = switchToAlert(driver, timeout);
    alert.sendKeys(text);
    alert.accept();
  }

  /**
   * Xử lý dialog 'beforeunload' khi rời trang. Lưu ý: Mọi dialog xác nhận rời trang về bản chất đều
   * quản lý qua giao diện Alert tiêu chuẩn.
   */
  public static void handleBeforeUnload(WebDriver driver, boolean accept, Duration timeout) {
    Alert alert = switchToAlert(driver, timeout);
    if (accept) {
      alert.accept(); // Rời khỏi trang
    } else {
      alert.dismiss(); // Ở lại trang
    }
  }

  // =========================================================================
  // COOKIES
  // =========================================================================

  /** Add Cookie */
  public static void addCookie(WebDriver driver, Cookie cookie) {
    driver.manage().addCookie(cookie);
  }

  /** Get Named Cookie */
  public static Cookie getCookieNamed(WebDriver driver, String name) {
    return driver.manage().getCookieNamed(name);
  }

  /** Get All Cookies */
  public static Set<Cookie> getAllCookies(WebDriver driver) {
    return driver.manage().getCookies();
  }

  /** Delete Cookie Object */
  public static void deleteCookie(WebDriver driver, Cookie cookie) {
    driver.manage().deleteCookie(cookie);
  }

  /** Delete Cookie by Name */
  public static void deleteCookieNamed(WebDriver driver, String name) {
    driver.manage().deleteCookieNamed(name);
  }

  /** Delete All Cookies */
  public static void deleteAllCookies(WebDriver driver) {
    driver.manage().deleteAllCookies();
  }

  /**
   * Tạo và thêm Same-Site Cookie (Yêu cầu Selenium 4+)
   *
   * @param sameSite "Strict", "Lax", hoặc "None"
   */
  public static void addSameSiteCookie(
      WebDriver driver, String name, String value, String domain, String path, String sameSite) {
    Cookie cookie =
        new Cookie.Builder(name, value)
            .domain(domain)
            .path(path)
            .sameSite(sameSite) // Strict hoặc Lax
            .build();
    driver.manage().addCookie(cookie);
  }

  /** Tạo Cookie với thuộc tính SameSite = Strict */
  public static void addStrictCookie(WebDriver driver, String name, String value) {
    Cookie cookie = new Cookie.Builder(name, value).sameSite("Strict").build();
    driver.manage().addCookie(cookie);
  }

  /** Tạo Cookie với thuộc tính SameSite = Lax */
  public static void addLaxCookie(WebDriver driver, String name, String value) {
    Cookie cookie = new Cookie.Builder(name, value).sameSite("Lax").build();
    driver.manage().addCookie(cookie);
  }

  // =========================================================================
  // FRAMES
  // =========================================================================

  /** Switch Frame bằng WebElement */
  public static void switchToFrame(WebDriver driver, WebElement frameElement) {
    driver.switchTo().frame(frameElement);
  }

  /** Switch Frame bằng name hoặc ID */
  public static void switchToFrame(WebDriver driver, String nameOrId) {
    driver.switchTo().frame(nameOrId);
  }

  /** Switch Frame bằng chỉ số Index (0-based) */
  public static void switchToFrame(WebDriver driver, int index) {
    driver.switchTo().frame(index);
  }

  /** Rời khỏi Frame hiện tại để quay về Main HTML / Page Content */
  public static void leaveFrame(WebDriver driver) {
    driver.switchTo().defaultContent();
  }

  /** Chuyển về Frame cha trực tiếp (dùng khi có nested frames) */
  public static void switchToParentFrame(WebDriver driver) {
    driver.switchTo().parentFrame();
  }
}
