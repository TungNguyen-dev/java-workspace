package tungnn.tutor.java.selenium.browser;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public final class ScriptUtils {

    private ScriptUtils() {}

    public static Object executeScript(WebDriver driver, String script, Object... args) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return js.executeScript(script, args);
    }

    public static Object executeAsyncScript(WebDriver driver, String script, Object... args) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return js.executeAsyncScript(script, args);
    }
}
