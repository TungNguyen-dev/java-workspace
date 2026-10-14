package tungnn.tutor.java.selenium.pool;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.selenium.util.ChromeDriverUtils;

public class PooledWebDriverFactory extends BasePooledObjectFactory<WebDriver> {

  private final BlockingQueue<String> availableProfiles;
  private final Map<WebDriver, String> driverProfileMap;

  public PooledWebDriverFactory(int maxTotal) {
    this.availableProfiles = new LinkedBlockingQueue<>();
    this.driverProfileMap = new ConcurrentHashMap<>();

    for (int i = 0; i < maxTotal; i++) {
      this.availableProfiles.add("profile_" + i);
    }
  }

  @Override
  public WebDriver create() throws InterruptedException {
    String profileKey = availableProfiles.take();
    try {
      WebDriver driver = ChromeDriverUtils.createDriver(profileKey);
      driverProfileMap.put(driver, profileKey);
      return driver;
    } catch (Exception e) {
      availableProfiles.add(profileKey);
      throw e;
    }
  }

  @Override
  public PooledObject<WebDriver> wrap(WebDriver driver) {
    return new DefaultPooledObject<>(driver);
  }

  @Override
  public void destroyObject(PooledObject<WebDriver> p) {
    WebDriver driver = p.getObject();
    if (driver == null) {
      return;
    }

    String profileKey = driverProfileMap.remove(driver);
    try {
      driver.quit();
    } finally {
      if (profileKey != null) {
        availableProfiles.add(profileKey);
      }
    }
  }
}
