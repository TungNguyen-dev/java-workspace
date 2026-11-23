package tungnn.tutor.java.selenium.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import tungnn.tutor.java.selenium.driver.options.ChromeOptionsFactory;

public class ChromeWebDriverFactory implements WebDriverFactory {

  private final ChromeOptionsFactory optionsFactory;

  public ChromeWebDriverFactory(ChromeOptionsFactory optionsFactory) {
    this.optionsFactory = optionsFactory;
  }

  @Override
  public WebDriver getWebDriver(String profileName) {
    var options = optionsFactory.createChromeOptions(profileName);
    return new ChromeDriver(options);
  }
}
