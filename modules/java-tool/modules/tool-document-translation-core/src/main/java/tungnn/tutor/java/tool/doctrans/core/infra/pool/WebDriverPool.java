package tungnn.tutor.java.tool.doctrans.core.infra.pool;

import org.openqa.selenium.WebDriver;

public interface WebDriverPool extends AutoCloseable {
  WebDriver borrowDriver();

  void returnDriver(WebDriver driver);
}
