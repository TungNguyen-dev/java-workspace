package tungnn.tutor.java.selenium.pool;

import org.openqa.selenium.WebDriver;

public interface WebDriverPool extends AutoCloseable {

    WebDriver borrowDriver();

    void returnDriver(WebDriver driver);

    void invalidateDriver(WebDriver driver);

    int size();

    @Override
    void close();
}
