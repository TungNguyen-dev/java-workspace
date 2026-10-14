package tungnn.tutor.java.selenium;

import tungnn.tutor.java.selenium.driver.ChromeWebDriverFactory;
import tungnn.tutor.java.selenium.driver.options.ChromeOptionsFactory;

public class ChromeWebDriverFactoryTests {

  static void main() {
    var optionsFactory = new ChromeOptionsFactory();
    var driverFactory = new ChromeWebDriverFactory(optionsFactory);

    driverFactory.getWebDriver("profile_0");
    driverFactory.getWebDrivers("profile_1", "profile_2");
  }
}
