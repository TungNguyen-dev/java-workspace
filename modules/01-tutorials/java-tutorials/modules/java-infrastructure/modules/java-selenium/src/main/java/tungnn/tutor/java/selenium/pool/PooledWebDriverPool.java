package tungnn.tutor.java.selenium.pool;

import java.time.Duration;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import tungnn.tutor.java.selenium.driver.options.ChromeOptionUtil;

public class PooledWebDriverPool implements WebDriverPool {

  private final GenericObjectPool<WebDriver> pool;

  public PooledWebDriverPool(int maxTotal) {
    PooledWebDriverFactory factory = new PooledWebDriverFactory(maxTotal);

    GenericObjectPoolConfig<WebDriver> config = new GenericObjectPoolConfig<>();
    config.setMaxTotal(maxTotal);
    config.setMaxIdle(maxTotal);
    config.setTestOnBorrow(true);
    config.setBlockWhenExhausted(true);
    config.setMaxWait(Duration.ofMinutes(10));

    this.pool = new GenericObjectPool<>(factory, config);
  }

  @Override
  public WebDriver borrowDriver() {
    try {
      WebDriver driver = pool.borrowObject();
      if (driver instanceof ChromeDriver chromeDriver) {
        ChromeOptionUtil.removeWebdriverAttribute(chromeDriver);
      }
      return driver;
    } catch (Exception e) {
      throw new RuntimeException("Không thể lấy WebDriver từ Pool", e);
    }
  }

  @Override
  public void returnDriver(WebDriver driver) {
    if (driver == null) {
      return;
    }

    try {
      pool.returnObject(driver);
    } catch (Exception e) {
      try {
        pool.invalidateObject(driver);
      } catch (Exception ignored) {
        safelyQuitDriver(driver);
      }
    }
  }

  private void safelyQuitDriver(WebDriver driver) {
    try {
      driver.quit();
    } catch (Exception ignored) {
    }
  }

  @Override
  public void close() {
    if (!pool.isClosed()) {
      pool.close();
    }
  }
}
