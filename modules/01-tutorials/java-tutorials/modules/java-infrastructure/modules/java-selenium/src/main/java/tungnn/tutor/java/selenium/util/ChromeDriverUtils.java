package tungnn.tutor.java.selenium.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import tungnn.tutor.java.core.lib.multithread.ConcurrentUtils;

public final class ChromeDriverUtils {

  public static final String DRIVER_PATH_ENV = "WEBDRIVER_CHROME_DRIVER_PATH";
  public static final String BINARY_PATH_ENV = "WEBDRIVER_CHROME_BINARY_PATH";

  public static final String DEFAULT_DRIVER_PATH =
      Path.of(
              System.getProperty("user.home"),
              ".data",
              "selenium",
              "chrome",
              "chromedriver-mac-x64",
              "chromedriver")
          .toString();

  public static final String DEFAULT_BINARY_PATH =
      Path.of(
              System.getProperty("user.home"),
              ".data",
              "selenium",
              "chrome",
              "chrome-mac-x64",
              "Google Chrome for Testing.app",
              "Contents",
              "MacOS",
              "Google Chrome for Testing")
          .toString();

  public static final Path PROFILE_ROOT_DIR =
      Path.of(System.getProperty("user.home"), ".data", "selenium", "chrome", "chrome-profiles");

  private ChromeDriverUtils() {
    throw new UnsupportedOperationException("Cannot instantiate a utility class");
  }

  // =========================================================================
  // 1. BUILD OPTIONS (CORE METHOD)
  // =========================================================================

  public static ChromeOptions buildOptions(ChromeDriverConfig config) {
    Objects.requireNonNull(config, "config must not be null");

    var options = new ChromeOptions();

    // Configure Chrome binary path with environment variable override
    var binaryPath = System.getenv(BINARY_PATH_ENV);
    if (binaryPath == null || binaryPath.isBlank()) {
      options.setBinary(DEFAULT_BINARY_PATH);
    } else {
      options.setBinary(binaryPath);
    }

    // -------------------------------------------------------------------------
    // General
    // -------------------------------------------------------------------------

    // Skip first-run setup screens.
    options.addArguments("--no-first-run");

    // Disable default browser prompt.
    options.addArguments("--no-default-browser-check");

    // Automatically block notification popups.
    options.addArguments("--disable-notifications");

    // Disable popup blocker.
    options.addArguments("--disable-popup-blocking");

    // Disable extensions for better test isolation.
    options.addArguments("--disable-extensions");

    // Start browser window maximized.
    options.addArguments("--start-maximized");

    // Default browser language.
    options.addArguments("--lang=en-US");

    // INFO:
    // Container/CI environments may require additional Chrome arguments,
    // such as --no-sandbox and --disable-dev-shm-usage.

    // -------------------------------------------------------------------------
    // Headless
    // -------------------------------------------------------------------------

    if (config.headless()) {
      options.addArguments("--headless=new");
    }

    // -------------------------------------------------------------------------
    // Profile
    // -------------------------------------------------------------------------

    if (config.profileName() != null && !config.profileName().isBlank()) {
      var profilePath = PROFILE_ROOT_DIR.resolve(config.profileName()).normalize();

      if (!profilePath.startsWith(PROFILE_ROOT_DIR)) {
        throw new IllegalArgumentException("Invalid Chrome profile name: " + config.profileName());
      }

      try {
        Files.createDirectories(profilePath);
      } catch (IOException e) {
        throw new UncheckedIOException("Failed to create profile directory: " + profilePath, e);
      }

      options.addArguments("--user-data-dir=" + profilePath.toAbsolutePath());
      options.addArguments("--profile-directory=Default");
    }

    // -------------------------------------------------------------------------
    // WebDriver BiDi
    // -------------------------------------------------------------------------

    if (config.enableBidi()) {
      // Request ChromeDriver to expose a WebDriver BiDi WebSocket endpoint.
      options.setCapability("webSocketUrl", true);
    }

    // -------------------------------------------------------------------------
    // SSL
    // -------------------------------------------------------------------------

    if (config.acceptInsecureCerts()) {
      options.setAcceptInsecureCerts(true);
    }

    return options;
  }

  public static ChromeOptions buildOptions(String profileName) {
    return buildOptions(ChromeDriverConfig.profile(profileName));
  }

  public static ChromeOptions buildOptions(String profileName, boolean enableBidi) {
    return buildOptions(new ChromeDriverConfig(profileName, enableBidi, false, false));
  }

  public static ChromeDriver createDriver() {
    return new ChromeDriver(buildOptions(ChromeDriverConfig.defaults()));
  }

  // =========================================================================
  // 2. CREATE DRIVER
  // =========================================================================

  public static ChromeDriver createDriver(String profileName) {
    return new ChromeDriver(buildOptions(profileName, false));
  }

  public static ChromeDriver createDriver(String profileName, boolean enableBidiNetwork) {
    return new ChromeDriver(buildOptions(profileName, enableBidiNetwork));
  }

  public static List<ChromeDriver> createDriver(String... profileNames) {
    if (profileNames == null || profileNames.length == 0) {
      return List.of();
    }

    var validProfiles = Arrays.stream(profileNames).filter(Objects::nonNull).toList();

    if (validProfiles.isEmpty()) {
      return List.of();
    }

    // Delegate multithreaded execution to ConcurrentUtils
    return ConcurrentUtils.executeConcurrently(validProfiles, ChromeDriverUtils::createDriver);
  }

  public static void quitQuietly(List<ChromeDriver> drivers) {
    if (drivers == null || drivers.isEmpty()) {
      return;
    }

    for (ChromeDriver driver : drivers) {
      if (driver != null) {
        try {
          driver.quit();
        } catch (RuntimeException ignored) {
          // Best-effort cleanup without obscuring initial exception
        }
      }
    }
  }

  // =========================================================================
  // HELPERS
  // =========================================================================

  public record ChromeDriverConfig(
      String profileName, boolean enableBidi, boolean acceptInsecureCerts, boolean headless) {

    public static ChromeDriverConfig defaults() {
      return new ChromeDriverConfig(null, false, false, false);
    }

    public static ChromeDriverConfig profile(String profileName) {
      return new ChromeDriverConfig(profileName, false, false, false);
    }

    public static ChromeDriverConfig bidi(String profileName) {
      return new ChromeDriverConfig(profileName, true, false, false);
    }
  }
}
