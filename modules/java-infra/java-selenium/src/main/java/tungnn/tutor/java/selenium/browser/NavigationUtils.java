package tungnn.tutor.java.selenium.browser;

import org.openqa.selenium.WebDriver;

public final class NavigationUtils {

    private NavigationUtils() {}

    public static void navigateTo(WebDriver driver, String url) {
        driver.navigate().to(url);
    }

    public static void back(WebDriver driver) {
        driver.navigate().back();
    }

    public static void forward(WebDriver driver) {
        driver.navigate().forward();
    }

    public static void refresh(WebDriver driver) {
        driver.navigate().refresh();
    }
}
