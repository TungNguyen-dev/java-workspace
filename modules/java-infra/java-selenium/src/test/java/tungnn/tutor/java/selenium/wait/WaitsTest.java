package tungnn.tutor.java.selenium.wait;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

class WaitsTest {

  private final WebDriver driver = mock(WebDriver.class);

  @Test
  void waitUntilReturnsConditionResult() {
    String result = Waits.waitUntil(driver, d -> "done", Duration.ofMillis(200));

    assertThat(result).isEqualTo("done");
  }

  @Test
  void waitUntilTimesOutWhenConditionNeverHolds() {
    assertThatThrownBy(() -> Waits.waitUntil(driver, d -> null, Duration.ofMillis(100)))
        .isInstanceOf(TimeoutException.class);
  }

  @Test
  void newWaitRequiresDriver() {
    assertThatThrownBy(() -> Waits.newWait(null, Waits.DEFAULT_TIMEOUT))
        .isInstanceOf(NullPointerException.class);
  }
}
