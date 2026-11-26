package tungnn.tutor.java.selenium.driver.chrome;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ChromeProcessLauncherTest {

    @Test
    void commandHasNoAutomationFlagsAndEndsWithUrls() {
        var command = ChromeProcessLauncher.buildCommand("/bin/chrome", "/profiles/p0", List.of("--window-size=1,2"), List.of("https://a", "https://b"));

        assertThat(command)
                .startsWith("/bin/chrome", "--user-data-dir=/profiles/p0", "--profile-directory=Default")
                .containsSequence("--window-size=1,2", "https://a", "https://b")
                .contains("--hide-crash-restore-bubble")
                .endsWith("https://a", "https://b")
                .noneMatch(arg -> arg.contains("remote-debugging") || arg.contains("enable-automation"));
    }
}
