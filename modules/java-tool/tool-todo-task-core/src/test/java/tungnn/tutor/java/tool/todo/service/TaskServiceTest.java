package tungnn.tutor.java.tool.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.tool.todo.config.TodoConfig;
import tungnn.tutor.java.tool.todo.core.model.Priority;
import tungnn.tutor.java.tool.todo.core.model.TaskRef;
import tungnn.tutor.java.tool.todo.core.query.TaskFilter;

class TaskServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-28T08:00:00Z"), ZoneOffset.UTC);

    @TempDir
    Path vault;

    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService(new TodoConfig(vault, null, null, List.of("Archive"), null), CLOCK);
    }

    private void write(String name, String content) throws IOException {
        var file = vault.resolve(name);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
    }

    private String read(String name) throws IOException {
        return Files.readString(vault.resolve(name));
    }

    @Test
    void listsOpenTasksSortedByDueAndSkipsCodeBlocksHiddenAndExcluded() throws IOException {
        write(
                "a.md",
                "- [ ] Later 📅 2026-12-10\n- [ ] Sooner 📅 2026-12-01\n- [x] Finished\n```\n- [ ] In code\n```\n");
        write("sub/b.md", "- [ ] No date\n");
        write(".obsidian/c.md", "- [ ] Hidden\n");
        write("Archive/d.md", "- [ ] Excluded\n");

        var tasks = service.list(TaskFilter.openTasks());

        assertThat(tasks).extracting("description").containsExactly("Sooner", "Later", "No date");
    }

    @Test
    void filtersByScopeOverdueTagAndText() throws IOException {
        write("a.md", "- [ ] Old 📅 2026-11-01 #home\n- [ ] Future 📅 2027-01-01 #work\n- [x] Done #home\n");

        assertThat(service.list(new TaskFilter(TaskFilter.Scope.OPEN, null, true, null, null, null)))
                .extracting("description")
                .containsExactly("Old #home");
        assertThat(service.list(new TaskFilter(TaskFilter.Scope.ALL, null, false, "home", null, null)))
                .hasSize(2);
        assertThat(service.list(new TaskFilter(TaskFilter.Scope.DONE, null, false, null, null, "done")))
                .hasSize(1);
    }

    @Test
    void addCreatesInboxFile() throws IOException {
        var ref = service.add(
                new NewTask("Buy milk", null, null, Priority.HIGH, LocalDate.of(2026, 12, 1), List.of("home")));

        assertThat(read("Inbox.md")).isEqualTo("- [ ] Buy milk ⏫ ➕ 2026-11-28 📅 2026-12-01 #home\n");
        assertThat(ref.path()).isEqualTo("Inbox.md");
        assertThat(ref.line()).isEqualTo(1);
    }

    @Test
    void addExpandsDatePlaceholderInNotePath() throws IOException {
        var daily = new TaskService(new TodoConfig(vault, "Daily/{yyyy-MM-dd}.md", null, List.of(), null), CLOCK);

        var ref = daily.add(new NewTask("Buy milk", null, null, null, null, null));

        assertThat(read("Daily/2026-11-28.md")).isEqualTo("- [ ] Buy milk ➕ 2026-11-28\n");
        assertThat(ref.path()).isEqualTo("Daily/2026-11-28.md");
    }

    @Test
    void addInsertsAtEndOfExistingHeadingSection() throws IOException {
        write("n.md", "# Title\n## Todo\n- [ ] One\n\n## Other\ntext\n");

        service.add(new NewTask("Two", "n.md", "Todo", null, null, null));

        assertThat(read("n.md")).isEqualTo("# Title\n## Todo\n- [ ] One\n- [ ] Two ➕ 2026-11-28\n\n## Other\ntext\n");
    }

    @Test
    void addAppendsMissingHeading() throws IOException {
        write("n.md", "text\n");

        service.add(new NewTask("Two", "n.md", "Todo", null, null, null));

        assertThat(read("n.md")).isEqualTo("text\n\n## Todo\n- [ ] Two ➕ 2026-11-28\n");
    }

    @Test
    void completeAndReopenReturnFreshIds() throws IOException {
        write("a.md", "keep\n- [ ] Task 📅 2026-12-01 #t\nkeep\n");
        var ref = service.list(TaskFilter.openTasks()).getFirst().ref();

        var doneRef = service.complete(ref);

        assertThat(read("a.md")).isEqualTo("keep\n- [x] Task 📅 2026-12-01 ✅ 2026-11-28 #t\nkeep\n");
        assertThat(doneRef).isNotEqualTo(ref);

        service.reopen(doneRef);
        assertThat(read("a.md")).isEqualTo("keep\n- [ ] Task 📅 2026-12-01 #t\nkeep\n");
    }

    @Test
    void updateChangesOnlyRequestedFields() throws IOException {
        write("a.md", "- [ ] Task 📅 2026-12-01 #t\n");
        var ref = service.list(TaskFilter.openTasks()).getFirst().ref();

        service.update(
                ref,
                new TaskPatch("Renamed", LocalDate.of(2027, 1, 2), false, Priority.LOW, List.of("new"), List.of("t")));

        assertThat(read("a.md")).isEqualTo("- [ ] Renamed 📅 2027-01-02 🔽 #new\n");
    }

    @Test
    void staleRefIsRejected() throws IOException {
        write("a.md", "- [ ] Task\n");
        var ref = service.list(TaskFilter.openTasks()).getFirst().ref();
        write("a.md", "- [ ] Task edited by hand\n");

        assertThatThrownBy(() -> service.complete(ref)).isInstanceOf(StaleTaskException.class);
        assertThatThrownBy(() -> service.complete(new TaskRef("missing.md", 1, "abcdef")))
                .isInstanceOf(StaleTaskException.class);
    }

    @Test
    void deleteRefusesChildrenUnlessAsked() throws IOException {
        write("a.md", "- [ ] Parent\n  - [ ] Child\n- [ ] Sibling\n");
        var parent = service.list(TaskFilter.openTasks()).getFirst().ref();

        assertThatThrownBy(() -> service.delete(parent, false)).isInstanceOf(IllegalStateException.class);

        service.delete(parent, true);
        assertThat(read("a.md")).isEqualTo("- [ ] Sibling\n");
    }

    @Test
    void preservesWindowsLineEndings() throws IOException {
        write("a.md", "- [ ] Task\r\n- [ ] Other\r\n");
        var ref = service.list(TaskFilter.openTasks()).getFirst().ref();

        service.complete(ref);

        assertThat(read("a.md")).isEqualTo("- [x] Task ✅ 2026-11-28\r\n- [ ] Other\r\n");
    }

    @Test
    void rejectsPathOutsideVault() {
        assertThatThrownBy(() -> service.complete(new TaskRef("../x.md", 1, "abcdef")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
