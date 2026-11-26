package tungnn.tutor.java.selenium.driver.chrome;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openqa.selenium.json.Json;

class ChromeProfileUtilsTest {

    @TempDir
    Path profile;

    @Test
    void dropsSavedTabsButKeepsLoginData() throws IOException {
        var defaultDir = Files.createDirectories(profile.resolve("Default"));
        Files.createDirectories(defaultDir.resolve("Sessions"));
        Files.writeString(defaultDir.resolve("Sessions").resolve("Session_1"), "tabs");
        Files.writeString(defaultDir.resolve("Current Tabs"), "legacy");
        Files.writeString(defaultDir.resolve("Cookies"), "cookie");
        Files.writeString(defaultDir.resolve("Login Data"), "login");
        Files.writeString(
                defaultDir.resolve("Preferences"),
                "{\"profile\":{\"exit_type\":\"Crashed\",\"name\":\"keep\"},"
                        + "\"session\":{\"restore_on_startup\":1,\"startup_urls\":[\"https://x\"]},"
                        + "\"other\":{\"a\":1}}");

        ChromeProfileUtils.resetSessionState(profile);

        assertThat(defaultDir.resolve("Sessions")).doesNotExist();
        assertThat(defaultDir.resolve("Current Tabs")).doesNotExist();
        assertThat(defaultDir.resolve("Cookies")).exists();
        assertThat(defaultDir.resolve("Login Data")).exists();

        java.util.Map<String, Object> preferences = new Json().toType(Files.readString(defaultDir.resolve("Preferences")), Json.MAP_TYPE);
        assertThat(preferences.get("profile").toString()).contains("exit_type=Normal", "name=keep");
        assertThat(preferences.get("session").toString())
                .contains("restore_on_startup=5")
                .doesNotContain("startup_urls");
        assertThat(preferences).containsKey("other");
    }

    @Test
    void newProfileWithoutFilesIsIgnored() {
        assertThatCode(() -> ChromeProfileUtils.resetSessionState(profile)).doesNotThrowAnyException();
    }

    @Test
    void corruptPreferencesAreLeftUntouched() throws IOException {
        var defaultDir = Files.createDirectories(profile.resolve("Default"));
        Files.writeString(defaultDir.resolve("Preferences"), "{not json");

        ChromeProfileUtils.resetSessionState(profile);

        assertThat(defaultDir.resolve("Preferences")).hasContent("{not json");
    }
}
