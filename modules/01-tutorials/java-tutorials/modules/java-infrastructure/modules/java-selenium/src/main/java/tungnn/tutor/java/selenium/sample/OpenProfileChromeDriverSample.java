package tungnn.tutor.java.selenium.sample;

import java.util.ArrayList;
import java.util.Scanner;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.selenium.util.ChromeDriverUtils;

public class OpenProfileChromeDriverSample {

  static void main() {
    var drivers = new ArrayList<WebDriver>();
    for (int i = 0; i < 8; i++) {
      // Add the created driver instance to the list
      drivers.add(ChromeDriverUtils.createDriver("profile_" + i));
    }

    // Scanner user enter
    System.out.println("All 8 browser profiles are open. Press Enter to quit all...");
    try (var scanner = new Scanner(System.in)) {
      scanner.nextLine();
    }

    // Quit all drivers
    System.out.println("Closing all browsers...");
    for (WebDriver driver : drivers) {
      if (driver != null) {
        driver.quit();
      }
    }
    System.out.println("All browsers closed successfully.");
  }
}
