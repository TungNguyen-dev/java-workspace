package tungnn.tutor.java.starter.infrastructure.webpage;

import java.time.Duration;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import tungnn.tutor.java.core.lib.multithread.ThreadUtil;
import tungnn.tutor.java.selenium.util.ElementUtil;

public abstract class BasePage {

  protected WebDriver driver;

  public BasePage(WebDriver driver) {
    this.driver = driver;
  }

  protected abstract Duration timeout();

  protected abstract String homeUrl();

  // =========================================================
  // ================= NAVIGATION HELPERS ====================
  // =========================================================

  /** Điều hướng đến một URL bất kỳ. */
  protected void navigateTo(String url) {
    driver.get(url);
    ThreadUtil.sleep(1000);
  }

  /** Làm mới (Refresh) trang hiện tại. */
  protected void refreshPage() {
    driver.navigate().refresh();
  }

  /** Quay lại (Back) trang trước đó. */
  protected void back() {
    driver.navigate().back();
  }

  /** Đi tới (Forward) trang kế tiếp trong lịch sử trình duyệt. */
  protected void forward() {
    driver.navigate().forward();
  }

  /** Lấy Title của trang hiện tại. */
  protected String getPageTitle() {
    return driver.getTitle();
  }

  /** Lấy URL hiện tại của trang. */
  protected String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  // =========================================================
  // ==================== WAITS HELPERS ======================
  // =========================================================

  /** Chờ cho Title của trang chứa một đoạn text mong muốn. */
  protected void waitTitleContains() {
    ElementUtil.waitUntil(
        driver, ExpectedConditions.titleContains("YouTube Video Summarizer"), timeout());
  }

  /** Chờ cho URL của trang chứa một đoạn text mong muốn (Hữu ích khi verify chuyển trang). */
  protected boolean waitUrlContains(String urlPart) {
    return ElementUtil.waitUntil(driver, ExpectedConditions.urlContains(urlPart), timeout());
  }

  // =========================================================
  // ================== JAVASCRIPT HELPERS ===================
  // =========================================================

  /** Thực thi một đoạn JavaScript bất kỳ. */
  protected Object executeJs(String script, Object... args) {
    return ((JavascriptExecutor) driver).executeScript(script, args);
  }

  /** Chờ cho trang và tất cả các request Ajax/Network (nếu có) được load xong hoàn toàn. */
  protected void waitPageLoadComplete() {
    ElementUtil.waitUntil(
        driver, ExpectedConditions.jsReturnsValue("return document.readyState"), timeout());
  }

  // =========================================================
  // ================= WINDOWS & ALERTS ======================
  // =========================================================

  /** Chấp nhận (Accept) một cửa sổ Alert Pop-up. */
  protected void acceptAlert() {
    ElementUtil.waitUntil(driver, ExpectedConditions.alertIsPresent(), timeout());
    driver.switchTo().alert().accept();
  }

  /** Hủy (Dismiss) một cửa sổ Alert Pop-up. */
  protected void dismissAlert() {
    ElementUtil.waitUntil(driver, ExpectedConditions.alertIsPresent(), timeout());
    driver.switchTo().alert().dismiss();
  }

  public WebDriver webDriver() {
    return driver;
  }
}
