package tungnn.tutor.java.selenium.pool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WebDriverPoolConfigTest {

    @Test
    void emptyEnvironmentYieldsDefaults() {
        assertThat(WebDriverPoolConfig.fromEnv(Map.of())).isEqualTo(WebDriverPoolConfig.defaults());
    }

    @Test
    void parsesEveryVariable() {
        var config = WebDriverPoolConfig.fromEnv(Map.of(
                WebDriverPoolConfig.MAX_TOTAL_ENV, "6",
                WebDriverPoolConfig.MAX_IDLE_ENV, "5",
                WebDriverPoolConfig.MIN_IDLE_ENV, "1",
                WebDriverPoolConfig.MAX_WAIT_SECONDS_ENV, "30",
                WebDriverPoolConfig.TEST_ON_BORROW_ENV, "false",
                WebDriverPoolConfig.PROFILE_PREFIX_ENV, "worker_",
                WebDriverPoolConfig.HEADLESS_ENV, "TRUE"));

        assertThat(config)
                .isEqualTo(new WebDriverPoolConfig(6, 5, 1, Duration.ofSeconds(30), false, "worker_", true));
    }

    @Test
    void maxIdleDefaultsToMaxTotal() {
        var config = WebDriverPoolConfig.fromEnv(Map.of(WebDriverPoolConfig.MAX_TOTAL_ENV, "7"));

        assertThat(config.maxIdle()).isEqualTo(7);
    }

    @Test
    void blankValuesFallBackToDefaults() {
        var config = WebDriverPoolConfig.fromEnv(Map.of(WebDriverPoolConfig.MAX_TOTAL_ENV, "  "));

        assertThat(config.maxTotal()).isEqualTo(WebDriverPoolConfig.defaults().maxTotal());
    }

    @Test
    void invalidNumberNamesTheVariable() {
        assertThatThrownBy(() -> WebDriverPoolConfig.fromEnv(Map.of(WebDriverPoolConfig.MAX_TOTAL_ENV, "many")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(WebDriverPoolConfig.MAX_TOTAL_ENV);
    }

    @Test
    void invalidBooleanNamesTheVariable() {
        assertThatThrownBy(() -> WebDriverPoolConfig.fromEnv(Map.of(WebDriverPoolConfig.HEADLESS_ENV, "yes")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(WebDriverPoolConfig.HEADLESS_ENV);
    }

    @Test
    void rejectsInconsistentSizes() {
        assertThatThrownBy(() -> new WebDriverPoolConfig(0, 0, 0, Duration.ofSeconds(1), true, "p_", false))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new WebDriverPoolConfig(2, 3, 0, Duration.ofSeconds(1), true, "p_", false))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new WebDriverPoolConfig(2, 2, 3, Duration.ofSeconds(1), true, "p_", false))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void withMaxTotalKeepsIdleBoundsValid() {
        var config = WebDriverPoolConfig.defaults().withMaxTotal(2);

        assertThat(config.maxTotal()).isEqualTo(2);
        assertThat(config.maxIdle()).isEqualTo(2);
    }
}
