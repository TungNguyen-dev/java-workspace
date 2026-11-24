package tungnn.tutor.java.obsidian.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.obsidian.config.ObsidianConfig;

class ObsidianCliTest {

    private final FakeRunner runner = new FakeRunner();
    private final ObsidianCli cli =
            new ObsidianCli(new ObsidianConfig(Path.of("/bin/obsidian"), "TungNN", Duration.ofSeconds(1)), runner);

    @Test
    void putsVaultFirstAndEscapesContent() {
        cli.execute(ObsidianCommand.of("create")
                .param("path", "A/B.md")
                .content("x\\y\nz\tq")
                .flag("overwrite"));

        assertThat(runner.calls.getFirst())
                .containsExactly(
                        "/bin/obsidian", "vault=TungNN", "create", "path=A/B.md", "content=x\\y\\nz\\tq", "overwrite");
    }

    @Test
    void skipsNullParams() {
        assertThat(ObsidianCommand.of("read").param("file", null).toArgs(null)).containsExactly("read");
    }

    @Test
    void failsOnNonZeroExit() {
        runner.next = new CommandResult(1, "", "boom");
        assertThatThrownBy(() -> cli.execute(ObsidianCommand.of("read")))
                .isInstanceOf(ObsidianCliException.class)
                .hasMessageContaining("boom");
    }

    @Test
    void failsOnErrorOutput() {
        runner.next = new CommandResult(0, "Error: File not found", "");
        assertThatThrownBy(() -> cli.execute(ObsidianCommand.of("read"))).isInstanceOf(ObsidianCliException.class);
    }

    @Test
    void failsWhenAppIsNotRunningDespiteZeroExit() {
        runner.next = new CommandResult(
                0, "The CLI is unable to find Obsidian. Please make sure Obsidian is running and try again.\n", "");
        assertThatThrownBy(() -> cli.execute(ObsidianCommand.of("version"))).isInstanceOf(ObsidianCliException.class);
    }

    @Test
    void executeLinesDropsBlanks() {
        runner.next = new CommandResult(0, "a.md\n\n b.md \n", "");
        assertThat(cli.executeLines(ObsidianCommand.of("files"))).containsExactly("a.md", "b.md");
    }
}
