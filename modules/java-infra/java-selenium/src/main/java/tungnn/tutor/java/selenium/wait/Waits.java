package tungnn.tutor.java.selenium.wait;

import java.time.Duration;
import java.util.Objects;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class Waits {

  private Waits() {
    throw new UnsupportedOperationException("Utility class must not be instantiated");
  }

  public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

  public static final Duration DEFAULT_POLLING = Duration.ofMillis(500);

  public static WebDriverWait newWait(WebDriver driver, Duration timeout) {
    Objects.requireNonNull(driver, "driver must not be null");
    return new WebDriverWait(driver, timeout == null ? DEFAULT_TIMEOUT : timeout, DEFAULT_POLLING);
  }

  public static <T> T waitUntil(
      WebDriver driver, ExpectedCondition<T> condition, Duration timeout) {
    return newWait(driver, timeout).until(condition);
  }
}
