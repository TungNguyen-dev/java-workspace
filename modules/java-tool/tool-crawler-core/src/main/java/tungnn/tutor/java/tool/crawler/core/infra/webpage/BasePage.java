package tungnn.tutor.java.tool.crawler.core.infra.webpage;

import java.time.Duration;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import tungnn.tutor.java.core.lib.multithread.ThreadUtils;
import tungnn.tutor.java.selenium.element.ElementUtils;

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

  protected void navigateTo(String url) {
    driver.get(url);
    ThreadUtils.sleep(1000);
  }

  protected void refreshPage() {
    driver.navigate().refresh();
  }

  protected void back() {
    driver.navigate().back();
  }

  protected void forward() {
    driver.navigate().forward();
  }

  protected String getPageTitle() {
    return driver.getTitle();
  }

  protected String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  // =========================================================
  // ==================== WAITS HELPERS ======================
  // =========================================================

  protected void waitTitleContains() {
    ElementUtils.waitUntil(
        driver, ExpectedConditions.titleContains("YouTube Video Summarizer"), timeout());
  }

  protected boolean waitUrlContains(String urlPart) {
    return ElementUtils.waitUntil(driver, ExpectedConditions.urlContains(urlPart), timeout());
  }

  // =========================================================
  // ================== JAVASCRIPT HELPERS ===================
  // =========================================================

  protected Object executeJs(String script, Object... args) {
    return ((JavascriptExecutor) driver).executeScript(script, args);
  }

  protected void waitPageLoadComplete() {
    ElementUtils.waitUntil(
        driver, ExpectedConditions.jsReturnsValue("return document.readyState"), timeout());
  }

  // =========================================================
  // ================= WINDOWS & ALERTS ======================
  // =========================================================

  protected void acceptAlert() {
    ElementUtils.waitUntil(driver, ExpectedConditions.alertIsPresent(), timeout());
    driver.switchTo().alert().accept();
  }

  protected void dismissAlert() {
    ElementUtils.waitUntil(driver, ExpectedConditions.alertIsPresent(), timeout());
    driver.switchTo().alert().dismiss();
  }

  public WebDriver webDriver() {
    return driver;
  }
}
