package tungnn.tutor.java.selenium.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

class ChromeDriverUtilsTest {

  @SuppressWarnings("unchecked")
  private static Map<String, Object> chromeOptions(ChromeOptions options) {

    return (Map<String, Object>) options.asMap().get("goog:chromeOptions");
  }

  private static void deleteDirectory(Path directory) {
    if (!Files.exists(directory)) {
      return;
    }

    try (var paths = Files.walk(directory)) {
      paths
          .sorted(Comparator.reverseOrder())
          .forEach(
              path -> {
                try {
                  Files.deleteIfExists(path);
                } catch (Exception e) {
                  throw new RuntimeException("Failed to delete: " + path, e);
                }
              });
    } catch (Exception e) {
      throw new RuntimeException("Failed to delete directory: " + directory, e);
    }
  }

  @Test
  void shouldBuildDefaultOptions() {
    var options = ChromeDriverUtils.buildOptions(ChromeDriverUtils.ChromeDriverConfig.defaults());

    var chromeOptions = chromeOptions(options);

    assertThat(chromeOptions.get("args"))
        .asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.list(String.class))
        .contains(
            "--no-first-run",
            "--no-default-browser-check",
            "--disable-notifications",
            "--disable-popup-blocking",
            "--disable-extensions",
            "--start-maximized",
            "--lang=en-US")
        .doesNotContain("--headless=new");
  }

  @Test
  void shouldUseDefaultChromeBinary() {
    var options = ChromeDriverUtils.buildOptions(ChromeDriverUtils.ChromeDriverConfig.defaults());

    var chromeOptions = chromeOptions(options);

    assertThat(chromeOptions).containsEntry("binary", ChromeDriverUtils.DEFAULT_BINARY_PATH);
  }

  @Test
  void shouldEnableHeadlessMode() {
    var config = new ChromeDriverUtils.ChromeDriverConfig(null, false, false, true);

    var options = ChromeDriverUtils.buildOptions(config);

    var chromeOptions = chromeOptions(options);

    assertThat(chromeOptions.get("args"))
        .asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.list(String.class))
        .contains("--headless=new");
  }

  @Test
  void shouldNotEnableHeadlessModeByDefault() {
    var options = ChromeDriverUtils.buildOptions(ChromeDriverUtils.ChromeDriverConfig.defaults());

    var chromeOptions = chromeOptions(options);

    assertThat(chromeOptions.get("args"))
        .asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.list(String.class))
        .doesNotContain("--headless=new");
  }

  @Test
  void shouldEnableBiDi() {
    var config = new ChromeDriverUtils.ChromeDriverConfig(null, true, false, false);

    var options = ChromeDriverUtils.buildOptions(config);

    assertThat(options.getCapability("webSocketUrl")).isEqualTo(true);
  }

  @Test
  void shouldNotEnableBiDiByDefault() {
    var options = ChromeDriverUtils.buildOptions(ChromeDriverUtils.ChromeDriverConfig.defaults());

    assertThat(options.getCapability("webSocketUrl")).isNull();
  }

  @Test
  void shouldAcceptInsecureCertificates() {
    var config = new ChromeDriverUtils.ChromeDriverConfig(null, false, true, false);

    var options = ChromeDriverUtils.buildOptions(config);

    assertThat(options.asMap()).containsEntry("acceptInsecureCerts", true);
  }

  @Test
  void shouldNotConfigureInsecureCertificatesByDefault() {
    var options = ChromeDriverUtils.buildOptions(ChromeDriverUtils.ChromeDriverConfig.defaults());

    assertThat(options.asMap()).doesNotContainKey("acceptInsecureCerts");
  }

  @Test
  void shouldConfigureChromeProfile() {
    var profileName = "test-" + UUID.randomUUID();

    try {
      var config = new ChromeDriverUtils.ChromeDriverConfig(profileName, false, false, false);

      var options = ChromeDriverUtils.buildOptions(config);

      var profilePath = ChromeDriverUtils.PROFILE_ROOT_DIR.resolve(profileName).normalize();

      assertThat(Files.isDirectory(profilePath)).isTrue();

      var chromeOptions = chromeOptions(options);

      assertThat(chromeOptions.get("args"))
          .asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.list(String.class))
          .contains(
              "--user-data-dir=" + profilePath.toAbsolutePath(), "--profile-directory=Default");
    } finally {
      deleteDirectory(ChromeDriverUtils.PROFILE_ROOT_DIR.resolve(profileName).normalize());
    }
  }

  @Test
  void shouldIgnoreBlankProfileName() {
    var config = new ChromeDriverUtils.ChromeDriverConfig("   ", false, false, false);

    var options = ChromeDriverUtils.buildOptions(config);

    var chromeOptions = chromeOptions(options);

    assertThat(chromeOptions.get("args"))
        .asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.list(String.class))
        .noneMatch(argument -> argument.startsWith("--user-data-dir="))
        .doesNotContain("--profile-directory=Default");
  }

  @Test
  void shouldRejectNullConfig() {
    assertThatThrownBy(
            () -> ChromeDriverUtils.buildOptions((ChromeDriverUtils.ChromeDriverConfig) null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("config must not be null");
  }

  @Test
  void shouldRejectProfilePathTraversal() {
    var config =
        new ChromeDriverUtils.ChromeDriverConfig("../outside-profile", false, false, false);

    assertThatThrownBy(() -> ChromeDriverUtils.buildOptions(config))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid Chrome profile name");
  }

  @Test
  void shouldRejectAbsoluteProfilePath() {
    var config =
        new ChromeDriverUtils.ChromeDriverConfig("/tmp/outside-profile", false, false, false);

    assertThatThrownBy(() -> ChromeDriverUtils.buildOptions(config))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid Chrome profile name");
  }

  @Test
  void shouldOpenDriver() {
    var driver = ChromeDriverUtils.createDriver();
    driver.get("https://www.google.com");

    assertThat(driver.getCurrentUrl()).isEqualTo("https://www.google.com/");

    driver.quit();
  }
}
