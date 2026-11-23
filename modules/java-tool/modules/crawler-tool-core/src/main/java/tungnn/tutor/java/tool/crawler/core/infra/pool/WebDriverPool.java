package tungnn.tutor.java.tool.crawler.core.infra.pool;

import org.openqa.selenium.WebDriver;

public interface WebDriverPool extends AutoCloseable {

  WebDriver borrowDriver();

  void returnDriver(WebDriver driver);
}
