package tungnn.tutor.java.selenium.pool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.openqa.selenium.WebDriver;

public class GenericWebDriverPool implements WebDriverPool {

    private final GenericObjectPool<WebDriver> pool;

    public GenericWebDriverPool(WebDriverPoolConfig config) {
        this(config, new ChromeDriverPooledObjectFactory(config));
    }

    GenericWebDriverPool(WebDriverPoolConfig config, PooledObjectFactory<WebDriver> factory) {
        this.pool = new GenericObjectPool<>(factory, toPoolConfig(config));
    }

    public static GenericWebDriverPool fromEnv() {
        return new GenericWebDriverPool(WebDriverPoolConfig.fromEnv());
    }

    private static GenericObjectPoolConfig<WebDriver> toPoolConfig(WebDriverPoolConfig config) {
        var poolConfig = new GenericObjectPoolConfig<WebDriver>();
        poolConfig.setMaxTotal(config.maxTotal());
        poolConfig.setMaxIdle(config.maxIdle());
        poolConfig.setMinIdle(config.minIdle());
        poolConfig.setMaxWait(config.maxWait());
        poolConfig.setTestOnBorrow(config.testOnBorrow());
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setJmxEnabled(false);
        return poolConfig;
    }

    @Override
    public WebDriver borrowDriver() {
        try {
            return pool.borrowObject();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to borrow a WebDriver from the pool", e);
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
            invalidateDriver(driver);
        }
    }

    @Override
    public void invalidateDriver(WebDriver driver) {
        if (driver == null) {
            return;
        }
        try {
            pool.invalidateObject(driver);
        } catch (Exception e) {
            try {
                driver.quit();
            } catch (RuntimeException ignored) {
                // Nothing left to clean up if the browser is already gone.
            }
        }
    }

    public int getNumActive() {
        return pool.getNumActive();
    }

    public int getNumIdle() {
        return pool.getNumIdle();
    }

    @Override
    public void close() {
        if (!pool.isClosed()) {
            pool.close();
        }
    }
}
