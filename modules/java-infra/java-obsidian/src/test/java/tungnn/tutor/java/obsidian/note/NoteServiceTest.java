package tungnn.tutor.java.obsidian.note;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.obsidian.cli.FakeRunner;
import tungnn.tutor.java.obsidian.cli.ObsidianCli;
import tungnn.tutor.java.obsidian.config.ObsidianConfig;

class NoteServiceTest {

    private final FakeRunner runner = new FakeRunner();
    private final NoteService notes =
            new NoteService(new ObsidianCli(new ObsidianConfig(Path.of("obs"), "V", Duration.ofSeconds(1)), runner));

    @Test
    void createsSingleCall() {
        notes.create(new NoteDraft("N.md", "Title", Frontmatter.create().put("title", "Title"), "body"), true);

        assertThat(runner.calls).hasSize(1);
        assertThat(runner.calls.getFirst()).contains("create", "path=N.md", "overwrite");
        assertThat(runner.calls.getFirst().stream()
                        .filter(a -> a.startsWith("content="))
                        .findFirst()
                        .orElseThrow())
                .isEqualTo("content=---\\ntitle: Title\\n---\\n\\n# Title\\n\\nbody\\n");
    }

    @Test
    void chunksLargeContent() {
        var body = "x".repeat(NoteService.MAX_SINGLE_CALL_CHARS * 2 + 10);
        notes.create(new NoteDraft("N.md", null, null, body), false);

        assertThat(runner.calls).hasSize(3);
        assertThat(runner.calls.get(1)).contains("append", "path=N.md", "inline");
    }

    @Test
    void referencesByNameOrPath() {
        notes.read(NoteRef.byName("Foo"));
        notes.delete(NoteRef.byPath("a/b.md"), true);

        assertThat(runner.calls.get(0)).contains("read", "file=Foo");
        assertThat(runner.calls.get(1)).contains("delete", "path=a/b.md", "permanent");
    }
}
