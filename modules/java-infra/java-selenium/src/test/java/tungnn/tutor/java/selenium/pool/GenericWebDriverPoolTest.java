package tungnn.tutor.java.selenium.pool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

class GenericWebDriverPoolTest {

    private final List<WebDriver> created = new ArrayList<>();
    private GenericWebDriverPool pool;

    @BeforeEach
    void setUp() {
        var config = new WebDriverPoolConfig(2, 2, 0, Duration.ofMillis(200), false, "p_", false);
        pool = new GenericWebDriverPool(config, new BasePooledObjectFactory<>() {
            @Override
            public WebDriver create() {
                var driver = mock(WebDriver.class);
                created.add(driver);
                return driver;
            }

            @Override
            public PooledObject<WebDriver> wrap(WebDriver driver) {
                return new DefaultPooledObject<>(driver);
            }

            @Override
            public void destroyObject(PooledObject<WebDriver> pooled) {
                pooled.getObject().quit();
            }
        });
    }

    @AfterEach
    void tearDown() {
        pool.close();
    }

    @Test
    void returnedDriverIsReused() {
        var first = pool.borrowDriver();
        pool.returnDriver(first);

        assertThat(pool.borrowDriver()).isSameAs(first);
        assertThat(created).hasSize(1);
    }

    @Test
    void exhaustedPoolTimesOut() {
        pool.borrowDriver();
        pool.borrowDriver();

        assertThatThrownBy(pool::borrowDriver).isInstanceOf(IllegalStateException.class);
        assertThat(pool.getNumActive()).isEqualTo(2);
    }

    @Test
    void invalidatedDriverIsQuitAndReplaced() {
        var first = pool.borrowDriver();
        pool.invalidateDriver(first);

        verify(first).quit();
        assertThat(pool.borrowDriver()).isNotSameAs(first);
    }

    @Test
    void closeQuitsIdleDriversAndIsIdempotent() {
        var driver = pool.borrowDriver();
        pool.returnDriver(driver);

        pool.close();
        pool.close();

        verify(driver).quit();
    }
}
