package tungnn.tutor.java.selenium.browser;

import java.time.Duration;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class AlertUtils {

  private AlertUtils() {}

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
      alert.accept(); // Leave the page
    } else {
      alert.dismiss(); // Stay on the page
    }
  }
}
