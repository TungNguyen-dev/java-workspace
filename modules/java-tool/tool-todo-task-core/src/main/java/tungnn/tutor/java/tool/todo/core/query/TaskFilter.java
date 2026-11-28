package tungnn.tutor.java.tool.todo.core.query;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Locale;
import tungnn.tutor.java.tool.todo.core.model.Task;

public record TaskFilter(
        Scope scope, LocalDate dueBefore, boolean overdue, String tag, String pathPrefix, String text) {

    public enum Scope {
        OPEN,
        DONE,
        ALL;

        public static Scope parse(String name) {
            try {
                return valueOf(name.strip().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown status: " + name + " (use todo, done or all)", e);
            }
        }
    }

    public static final Comparator<Task> ORDER = Comparator.comparing(
                    Task::due, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::priority)
            .thenComparing(task -> task.ref().path())
            .thenComparingInt(task -> task.ref().line());

    public static TaskFilter openTasks() {
        return new TaskFilter(Scope.OPEN, null, false, null, null, null);
    }

    public boolean matches(Task task, LocalDate today) {
        var scopeOk = switch (scope == null ? Scope.OPEN : scope) {
            case OPEN -> task.status().isOpen();
            case DONE -> !task.status().isOpen();
            case ALL -> true;
        };
        return scopeOk
                && (dueBefore == null || task.due() != null && task.due().isBefore(dueBefore))
                && (!overdue
                        || task.status().isOpen()
                                && task.due() != null
                                && task.due().isBefore(today))
                && (tag == null || task.tags().stream().anyMatch(tag.replaceFirst("^#", "")::equalsIgnoreCase))
                && (pathPrefix == null || task.ref().path().startsWith(pathPrefix))
                && (text == null
                        || task.description().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)));
    }
}
