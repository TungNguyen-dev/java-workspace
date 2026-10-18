package tungnn.tutor.java.selenium.sample;

import java.util.ArrayList;
import java.util.Scanner;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.selenium.util.ChromeDriverUtils;

public class CreateProfileDriverSample {

  static void main() {
    var drivers = new ArrayList<WebDriver>();

    try {
      for (var profileIndex = 0; profileIndex < 4; profileIndex++) {
        var profileName = "profile_" + profileIndex;
        var driver = ChromeDriverUtils.createDriver(profileName);
        drivers.add(driver);
      }

      System.out.println("Press Enter to quit all browsers...");
      new Scanner(System.in).nextLine();
    } finally {
      drivers.forEach(WebDriver::quit);
    }
  }
}
