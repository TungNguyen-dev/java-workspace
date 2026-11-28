package tungnn.tutor.java.tool.todo.core.parse;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.todo.core.model.Priority;
import tungnn.tutor.java.tool.todo.core.model.TaskStatus;

class TaskLineParserTest {

    private final TaskLineParser parser = new TaskLineParser(null);

    @Test
    void parsesAllFields() {
        var line = "- [ ] Pay rent ⏫ ➕ 2026-11-01 🛫 2026-11-20 ⏳ 2026-11-25 📅 2026-12-01 #bill #home/flat";

        var task = parser.parse(line, "Inbox.md", 3).orElseThrow();

        assertThat(task.description()).isEqualTo("Pay rent #bill #home/flat");
        assertThat(task.status()).isEqualTo(TaskStatus.TODO);
        assertThat(task.priority()).isEqualTo(Priority.HIGH);
        assertThat(task.created()).isEqualTo(LocalDate.of(2026, 11, 1));
        assertThat(task.start()).isEqualTo(LocalDate.of(2026, 11, 20));
        assertThat(task.scheduled()).isEqualTo(LocalDate.of(2026, 11, 25));
        assertThat(task.due()).isEqualTo(LocalDate.of(2026, 12, 1));
        assertThat(task.tags()).containsExactly("bill", "home/flat");
        assertThat(task.ref().toString()).startsWith("Inbox.md:3#");
    }

    @Test
    void acceptsAlternateEmojisAndVariationSelector() {
        var task =
                parser.parse("- [ ] A 🗓️ 2026-12-01 ⌛ 2026-12-02", "a.md", 1).orElseThrow();

        assertThat(task.due()).isEqualTo(LocalDate.of(2026, 12, 1));
        assertThat(task.scheduled()).isEqualTo(LocalDate.of(2026, 12, 2));
        assertThat(task.description()).isEqualTo("A");
    }

    @Test
    void parsesStatusesAndDoneDate() {
        assertThat(parser.parse("- [x] Done ✅ 2026-11-27", "a.md", 1)
                        .orElseThrow()
                        .done())
                .isEqualTo(LocalDate.of(2026, 11, 27));
        assertThat(parser.parse("- [-] Dropped ❌ 2026-11-27", "a.md", 1)
                        .orElseThrow()
                        .status())
                .isEqualTo(TaskStatus.CANCELLED);
        assertThat(parser.parse("- [/] Working", "a.md", 1).orElseThrow().status())
                .isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(parser.parse("- [?] Odd", "a.md", 1).orElseThrow().symbol()).isEqualTo('?');
    }

    @Test
    void parsesBlockquoteAndNumberedPrefixes() {
        assertThat(parser.parse("> - [ ] Quoted", "a.md", 1)).isPresent();
        assertThat(parser.parse("  1. [ ] Numbered", "a.md", 1)).isPresent();
    }

    @Test
    void ignoresNonTaskLines() {
        assertThat(parser.parse("- plain bullet", "a.md", 1)).isEmpty();
        assertThat(parser.parse("[ ] no bullet", "a.md", 1)).isEmpty();
        assertThat(parser.parse("", "a.md", 1)).isEmpty();
    }

    @Test
    void appliesGlobalFilter() {
        var filtered = new TaskLineParser("#task");

        assertThat(filtered.parse("- [ ] With tag #task", "a.md", 1)).isPresent();
        assertThat(filtered.parse("- [ ] Plain checklist", "a.md", 1)).isEmpty();
    }
}
