package tungnn.tutor.java.selenium.driver.options;

import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeOptionsFactory {

  public ChromeOptions createChromeOptions(String profileName) {
    ChromeOptionUtil.initializeDriverProperty();

    var options = new ChromeOptions();
    ChromeOptionUtil.configureChromeBinary(options);
    ChromeOptionUtil.configureArguments(options);

    if (profileName != null) {
      ChromeOptionUtil.configureProfile(options, profileName);
    }

    return options;
  }

  public ChromeOptions createChromeOptions() {
    return createChromeOptions(null);
  }

  public ChromeOptions createChromeOptionsWithBiDiEnabled(String profileName) {
    var options = createChromeOptions(profileName);

    // Enable WebDriver BiDi Features
    options.setCapability("webSocketUrl", true);

    return options;
  }
}
