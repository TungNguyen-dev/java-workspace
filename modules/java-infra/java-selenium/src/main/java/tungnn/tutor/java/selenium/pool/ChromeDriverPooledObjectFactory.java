package tungnn.tutor.java.selenium.pool;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import tungnn.tutor.java.selenium.driver.chrome.ChromeDriverUtils;
import tungnn.tutor.java.selenium.driver.chrome.ChromeDriverUtils.ChromeDriverConfig;

public class ChromeDriverPooledObjectFactory extends BasePooledObjectFactory<WebDriver> {

    private final WebDriverPoolConfig config;
    private final BlockingQueue<String> availableProfiles = new LinkedBlockingQueue<>();
    private final Map<WebDriver, String> driverProfiles = new ConcurrentHashMap<>();

    public ChromeDriverPooledObjectFactory(WebDriverPoolConfig config) {
        this.config = config;
        for (var i = 0; i < config.maxTotal(); i++) {
            availableProfiles.add(config.profilePrefix() + i);
        }
    }

    @Override
    public WebDriver create() throws InterruptedException {
        // Bounded by the pool's own wait so a profile shortage can never block longer than borrowObject would.
        var profile = availableProfiles.poll(config.maxWait().toMillis(), TimeUnit.MILLISECONDS);
        if (profile == null) {
            throw new IllegalStateException("No free Chrome profile within " + config.maxWait());
        }

        try {
            var driver = new ChromeDriver(
                    ChromeDriverUtils.buildOptions(new ChromeDriverConfig(profile, false, false, config.headless())));
            driverProfiles.put(driver, profile);
            return driver;
        } catch (RuntimeException e) {
            availableProfiles.add(profile);
            throw e;
        }
    }

    @Override
    public PooledObject<WebDriver> wrap(WebDriver driver) {
        return new DefaultPooledObject<>(driver);
    }

    @Override
    public boolean validateObject(PooledObject<WebDriver> pooled) {
        if (!(pooled.getObject() instanceof RemoteWebDriver driver)) {
            return true;
        }
        try {
            return driver.getSessionId() != null && !driver.getWindowHandles().isEmpty();
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public void destroyObject(PooledObject<WebDriver> pooled) {
        var driver = pooled.getObject();
        var profile = driverProfiles.remove(driver);
        try {
            driver.quit();
        } catch (RuntimeException ignored) {
            // The browser may already be gone; the profile must still be released below.
        } finally {
            if (profile != null) {
                availableProfiles.add(profile);
            }
        }
    }
}
