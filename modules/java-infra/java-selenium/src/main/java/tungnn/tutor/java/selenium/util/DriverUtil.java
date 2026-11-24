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

  public static void navigateTo(WebDriver driver, String url) {
    driver.navigate().to(url);
  }

  public static void back(WebDriver driver) {
    driver.navigate().back();
  }

  public static void forward(WebDriver driver) {
    driver.navigate().forward();
  }

  public static void refresh(WebDriver driver) {
    driver.navigate().refresh();
  }

  // =========================================================================
  // ALERTS (Xử lý Alert/Confirm/Prompt/BeforeUnload)
  // =========================================================================

  public static Alert switchToAlert(WebDriver driver, Duration timeout) {
    WebDriverWait wait = new WebDriverWait(driver, timeout);
    return wait.until(ExpectedConditions.alertIsPresent());
  }

  public static void acceptAlert(WebDriver driver, Duration timeout) {
    switchToAlert(driver, timeout).accept();
  }

  public static void dismissAlert(WebDriver driver, Duration timeout) {
    switchToAlert(driver, timeout).dismiss();
  }

  public static String getAlertText(WebDriver driver, Duration timeout) {
    return switchToAlert(driver, timeout).getText();
  }

  public static void sendKeysToPrompt(WebDriver driver, String text, Duration timeout) {
    Alert alert = switchToAlert(driver, timeout);
    alert.sendKeys(text);
    alert.accept();
  }

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

  public static void addCookie(WebDriver driver, Cookie cookie) {
    driver.manage().addCookie(cookie);
  }

  public static Cookie getCookieNamed(WebDriver driver, String name) {
    return driver.manage().getCookieNamed(name);
  }

  public static Set<Cookie> getAllCookies(WebDriver driver) {
    return driver.manage().getCookies();
  }

  public static void deleteCookie(WebDriver driver, Cookie cookie) {
    driver.manage().deleteCookie(cookie);
  }

  public static void deleteCookieNamed(WebDriver driver, String name) {
    driver.manage().deleteCookieNamed(name);
  }

  public static void deleteAllCookies(WebDriver driver) {
    driver.manage().deleteAllCookies();
  }

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

  public static void addStrictCookie(WebDriver driver, String name, String value) {
    Cookie cookie = new Cookie.Builder(name, value).sameSite("Strict").build();
    driver.manage().addCookie(cookie);
  }

  public static void addLaxCookie(WebDriver driver, String name, String value) {
    Cookie cookie = new Cookie.Builder(name, value).sameSite("Lax").build();
    driver.manage().addCookie(cookie);
  }

  // =========================================================================
  // FRAMES
  // =========================================================================

  public static void switchToFrame(WebDriver driver, WebElement frameElement) {
    driver.switchTo().frame(frameElement);
  }

  public static void switchToFrame(WebDriver driver, String nameOrId) {
    driver.switchTo().frame(nameOrId);
  }

  public static void switchToFrame(WebDriver driver, int index) {
    driver.switchTo().frame(index);
  }

  public static void leaveFrame(WebDriver driver) {
    driver.switchTo().defaultContent();
  }

  public static void switchToParentFrame(WebDriver driver) {
    driver.switchTo().parentFrame();
  }
}
