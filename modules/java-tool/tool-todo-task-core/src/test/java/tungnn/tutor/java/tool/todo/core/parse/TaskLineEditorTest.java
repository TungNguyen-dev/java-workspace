package tungnn.tutor.java.tool.todo.core.parse;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.todo.core.model.DateField;
import tungnn.tutor.java.tool.todo.core.model.Priority;

class TaskLineEditorTest {

    private static final String RECURRING = "- [ ] Pay rent 🔁 every month 📅 2026-12-01 #bill";

    @Test
    void statusChangeLeavesEverythingElseUntouched() {
        var done = TaskLineEditor.setStatus(RECURRING, 'x');

        assertThat(done).isEqualTo("- [x] Pay rent 🔁 every month 📅 2026-12-01 #bill");
    }

    @Test
    void addsDoneDateBeforeTrailingTags() {
        var line = TaskLineEditor.setDate(RECURRING, DateField.DONE, LocalDate.of(2026, 11, 28));

        assertThat(line).isEqualTo("- [ ] Pay rent 🔁 every month 📅 2026-12-01 ✅ 2026-11-28 #bill");
    }

    @Test
    void removesDateWithItsLeadingSpace() {
        var line = TaskLineEditor.setDate("- [x] A ✅ 2026-11-28 #t", DateField.DONE, null);

        assertThat(line).isEqualTo("- [x] A #t");
    }

    @Test
    void replacesDueDateInPlace() {
        var line = TaskLineEditor.setDate(RECURRING, DateField.DUE, LocalDate.of(2027, 1, 5));

        assertThat(line).isEqualTo("- [ ] Pay rent 🔁 every month 📅 2027-01-05 #bill");
    }

    @Test
    void setsAndClearsPriority() {
        var high = TaskLineEditor.setPriority("- [ ] A 📅 2026-12-01", Priority.HIGH);
        assertThat(high).isEqualTo("- [ ] A 📅 2026-12-01 ⏫");

        assertThat(TaskLineEditor.setPriority(high, Priority.LOW)).isEqualTo("- [ ] A 📅 2026-12-01 🔽");
        assertThat(TaskLineEditor.setPriority(high, Priority.NONE)).isEqualTo("- [ ] A 📅 2026-12-01");
    }

    @Test
    void replacesDescriptionButKeepsMetadataAndUnknownTokens() {
        var line = TaskLineEditor.setDescription(RECURRING, "Pay flat rent");

        assertThat(line).isEqualTo("- [ ] Pay flat rent 🔁 every month 📅 2026-12-01 #bill");
    }

    @Test
    void addsAndRemovesTags() {
        var added = TaskLineEditor.addTag("- [ ] A #x", "#y");
        assertThat(added).isEqualTo("- [ ] A #x #y");
        assertThat(TaskLineEditor.addTag(added, "x")).isEqualTo(added);
        assertThat(TaskLineEditor.removeTag(added, "x")).isEqualTo("- [ ] A #y");
    }
}
