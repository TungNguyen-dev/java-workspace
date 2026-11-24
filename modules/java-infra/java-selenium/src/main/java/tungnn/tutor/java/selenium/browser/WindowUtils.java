package tungnn.tutor.java.selenium.browser;

import java.util.Set;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

public final class WindowUtils {

    private WindowUtils() {
        // Private constructor to prevent instantiation
    }

    // =========================================================================
    // WINDOWS AND TABS
    // =========================================================================

    public static String getWindowHandle(WebDriver driver) {
        return driver.getWindowHandle();
    }

    public static Set<String> getWindowHandles(WebDriver driver) {
        return driver.getWindowHandles();
    }

    public static void switchToWindow(WebDriver driver, String windowHandle) {
        driver.switchTo().window(windowHandle);
    }

    public static void close(WebDriver driver) {
        driver.close();
    }

    public static void createNewWindowAndSwitch(WebDriver driver) {
        driver.switchTo().newWindow(WindowType.WINDOW);
    }

    public static void createNewTabAndSwitch(WebDriver driver) {
        driver.switchTo().newWindow(WindowType.TAB);
    }

    public static void quit(WebDriver driver) {
        if (driver != null) {
            driver.quit();
        }
    }

    // =========================================================================
    // WINDOW MANAGEMENT
    // =========================================================================

    public static Dimension getWindowSize(WebDriver driver) {
        return driver.manage().window().getSize();
    }

    public static void setWindowSize(WebDriver driver, int width, int height) {
        driver.manage().window().setSize(new Dimension(width, height));
    }

    public static Point getWindowPosition(WebDriver driver) {
        return driver.manage().window().getPosition();
    }

    public static void setWindowPosition(WebDriver driver, int x, int y) {
        driver.manage().window().setPosition(new Point(x, y));
    }

    public static void maximize(WebDriver driver) {
        driver.manage().window().maximize();
    }

    public static void minimize(WebDriver driver) {
        driver.manage().window().minimize();
    }

    public static void fullscreen(WebDriver driver) {
        driver.manage().window().fullscreen();
    }
}
