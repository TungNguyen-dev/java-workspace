package tungnn.tutor.java.tool.todo.core.parse;

import java.time.LocalDate;
import java.util.List;
import tungnn.tutor.java.tool.todo.core.model.DateField;
import tungnn.tutor.java.tool.todo.core.model.Priority;

public final class TaskLineFormatter {

    private TaskLineFormatter() {
        throw new UnsupportedOperationException("cannot instantiate utility class");
    }

    public static String format(
            String description, Priority priority, LocalDate created, LocalDate due, List<String> tags) {
        var sb = new StringBuilder("- [ ] ").append(description.strip());
        if (priority != null && priority != Priority.NONE) {
            sb.append(' ').append(priority.symbol());
        }
        if (created != null) {
            sb.append(' ').append(DateField.CREATED.emoji()).append(' ').append(created);
        }
        if (due != null) {
            sb.append(' ').append(DateField.DUE.emoji()).append(' ').append(due);
        }
        tags.forEach(tag -> sb.append(" #").append(stripHash(tag)));
        return sb.toString();
    }

    static String stripHash(String tag) {
        return tag.strip().replaceFirst("^#", "");
    }
}
