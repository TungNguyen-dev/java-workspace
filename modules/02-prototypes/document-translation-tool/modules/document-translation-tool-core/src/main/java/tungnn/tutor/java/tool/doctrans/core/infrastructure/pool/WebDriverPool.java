package tungnn.tutor.java.tool.doctrans.core.infrastructure.pool;

import org.openqa.selenium.WebDriver;

public interface WebDriverPool extends AutoCloseable {

  WebDriver borrowDriver();

  void returnDriver(WebDriver driver);
}
