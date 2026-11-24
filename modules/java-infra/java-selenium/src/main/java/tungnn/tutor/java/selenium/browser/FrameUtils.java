package tungnn.tutor.java.selenium.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public final class FrameUtils {

    private FrameUtils() {}

    public static void switchToFrame(WebDriver driver, WebElement frameElement) {
        driver.switchTo().frame(frameElement);
    }

    public static void switchToFrame(WebDriver driver, String nameOrId) {
        driver.switchTo().frame(nameOrId);
    }

    public static void switchToFrame(WebDriver driver, int index) {
        driver.switchTo().frame(index);
    }

    public static void leaveFrame(WebDriver driver) {
        driver.switchTo().defaultContent();
    }

    public static void switchToParentFrame(WebDriver driver) {
        driver.switchTo().parentFrame();
    }
}
