package tungnn.tutor.java.obsidian;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.obsidian.config.ObsidianConfig;

class ObsidianConfigTest {

    @Test
    void usesDefaultsWhenEnvIsEmpty() {
        var config = ObsidianConfig.fromEnv(Map.of());

        assertThat(config.executable()).isEqualTo(ObsidianConfig.DEFAULT_EXECUTABLE);
        assertThat(config.vault()).isNull();
        assertThat(config.timeout()).isEqualTo(Duration.ofSeconds(60));
        assertThat(config.templatesFolder()).isNull();
        assertThat(config.reportsFolder()).isEqualTo("Reports");
    }

    @Test
    void readsEnvValues() {
        var config = ObsidianConfig.fromEnv(Map.of(
                "OBSIDIAN_CLI_PATH", "/opt/obsidian",
                "OBSIDIAN_VAULT", " TungNN ",
                "OBSIDIAN_TIMEOUT_SECONDS", "5",
                "OBSIDIAN_REPORTS_FOLDER", "Out",
                "OBSIDIAN_AUTHOR", "TungNN"));

        assertThat(config.executable()).isEqualTo(Path.of("/opt/obsidian"));
        assertThat(config.vault()).isEqualTo("TungNN");
        assertThat(config.timeout()).isEqualTo(Duration.ofSeconds(5));
        assertThat(config.reportsFolder()).isEqualTo("Out");
        assertThat(config.author()).isEqualTo("TungNN");
    }

    @Test
    void rejectsInvalidTimeout() {
        assertThatThrownBy(() -> ObsidianConfig.fromEnv(Map.of("OBSIDIAN_TIMEOUT_SECONDS", "abc")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ObsidianConfig.fromEnv(Map.of("OBSIDIAN_TIMEOUT_SECONDS", "0")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
