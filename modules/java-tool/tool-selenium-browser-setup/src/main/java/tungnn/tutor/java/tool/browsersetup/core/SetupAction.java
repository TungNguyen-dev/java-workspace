package tungnn.tutor.java.tool.browsersetup.core;

import org.openqa.selenium.WebDriver;

@FunctionalInterface
public interface SetupAction {

    SetupAction NO_OP = driver -> {};

    void run(WebDriver driver);
}
