package tungnn.tutor.java.selenium.pool;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public record WebDriverPoolConfig(
        int maxTotal,
        int maxIdle,
        int minIdle,
        Duration maxWait,
        boolean testOnBorrow,
        String profilePrefix,
        boolean headless) {

    public static final String MAX_TOTAL_ENV = "WEBDRIVER_POOL_MAX_TOTAL";
    public static final String MAX_IDLE_ENV = "WEBDRIVER_POOL_MAX_IDLE";
    public static final String MIN_IDLE_ENV = "WEBDRIVER_POOL_MIN_IDLE";
    public static final String MAX_WAIT_SECONDS_ENV = "WEBDRIVER_POOL_MAX_WAIT_SECONDS";
    public static final String TEST_ON_BORROW_ENV = "WEBDRIVER_POOL_TEST_ON_BORROW";
    public static final String PROFILE_PREFIX_ENV = "WEBDRIVER_POOL_PROFILE_PREFIX";
    public static final String HEADLESS_ENV = "WEBDRIVER_POOL_HEADLESS";

    private static final int DEFAULT_MAX_TOTAL = 4;
    private static final Duration DEFAULT_MAX_WAIT = Duration.ofMinutes(10);
    private static final String DEFAULT_PROFILE_PREFIX = "profile_";

    public WebDriverPoolConfig {
        if (maxTotal <= 0) {
            throw new IllegalArgumentException("maxTotal must be positive: " + maxTotal);
        }
        if (minIdle < 0 || minIdle > maxIdle || maxIdle > maxTotal) {
            throw new IllegalArgumentException(
                    "Expected 0 <= minIdle <= maxIdle <= maxTotal but got %d, %d, %d".formatted(minIdle, maxIdle, maxTotal));
        }
        Objects.requireNonNull(maxWait, "maxWait must not be null");
        if (maxWait.isNegative()) {
            throw new IllegalArgumentException("maxWait must not be negative: " + maxWait);
        }
        if (profilePrefix == null || profilePrefix.isBlank()) {
            throw new IllegalArgumentException("profilePrefix must not be blank");
        }
    }

    public static WebDriverPoolConfig defaults() {
        return new WebDriverPoolConfig(
                DEFAULT_MAX_TOTAL, DEFAULT_MAX_TOTAL, 0, DEFAULT_MAX_WAIT, true, DEFAULT_PROFILE_PREFIX, false);
    }

    public static WebDriverPoolConfig fromEnv() {
        return fromEnv(System.getenv());
    }

    public static WebDriverPoolConfig fromEnv(Map<String, String> env) {
        var maxTotal = intValue(env, MAX_TOTAL_ENV, DEFAULT_MAX_TOTAL);
        var maxIdle = intValue(env, MAX_IDLE_ENV, maxTotal);
        var minIdle = intValue(env, MIN_IDLE_ENV, 0);
        var maxWaitSeconds = intValue(env, MAX_WAIT_SECONDS_ENV, (int) DEFAULT_MAX_WAIT.toSeconds());
        var testOnBorrow = booleanValue(env, TEST_ON_BORROW_ENV, true);
        var profilePrefix = value(env, PROFILE_PREFIX_ENV, Function.identity(), DEFAULT_PROFILE_PREFIX);
        var headless = booleanValue(env, HEADLESS_ENV, false);

        return new WebDriverPoolConfig(
                maxTotal, maxIdle, minIdle, Duration.ofSeconds(maxWaitSeconds), testOnBorrow, profilePrefix, headless);
    }

    public WebDriverPoolConfig withMaxTotal(int newMaxTotal) {
        return new WebDriverPoolConfig(
                newMaxTotal,
                Math.min(maxIdle, newMaxTotal),
                Math.min(minIdle, newMaxTotal),
                maxWait,
                testOnBorrow,
                profilePrefix,
                headless);
    }

    private static int intValue(Map<String, String> env, String name, int defaultValue) {
        return value(env, name, Integer::parseInt, defaultValue);
    }

    private static boolean booleanValue(Map<String, String> env, String name, boolean defaultValue) {
        return value(env, name, WebDriverPoolConfig::parseBoolean, defaultValue);
    }

    private static boolean parseBoolean(String raw) {
        if (raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(raw);
        }
        throw new IllegalArgumentException("Expected true or false but got '" + raw + "'");
    }

    private static <T> T value(Map<String, String> env, String name, Function<String, T> parser, T defaultValue) {
        var raw = env.get(name);
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            return parser.apply(raw.strip());
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid value for " + name + ": '" + raw + "'", e);
        }
    }
}
