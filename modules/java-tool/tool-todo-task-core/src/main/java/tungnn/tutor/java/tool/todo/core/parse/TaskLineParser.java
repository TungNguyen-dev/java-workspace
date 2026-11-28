package tungnn.tutor.java.tool.todo.core.parse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Optional;
import tungnn.tutor.java.tool.todo.core.model.DateField;
import tungnn.tutor.java.tool.todo.core.model.Priority;
import tungnn.tutor.java.tool.todo.core.model.Task;
import tungnn.tutor.java.tool.todo.core.model.TaskRef;
import tungnn.tutor.java.tool.todo.core.model.TaskStatus;

public final class TaskLineParser {

    private final String globalFilter;

    public TaskLineParser(String globalFilter) {
        this.globalFilter = globalFilter == null || globalFilter.isBlank() ? null : globalFilter.strip();
    }

    public Optional<Task> parse(String raw, String path, int lineNumber) {
        var line = TaskPatterns.LINE.matcher(raw);
        if (!line.matches() || globalFilter != null && !raw.contains(globalFilter)) {
            return Optional.empty();
        }
        var body = line.group("body");
        var symbol = line.group("status").charAt(0);

        var dates = new EnumMap<DateField, LocalDate>(DateField.class);
        var description = body;
        for (var field : DateField.values()) {
            var matcher = field.pattern().matcher(body);
            if (matcher.find()) {
                dates.put(field, LocalDate.parse(matcher.group(1)));
            }
            description = field.pattern().matcher(description).replaceAll("");
        }

        var priorityMatcher = Priority.SYMBOL_PATTERN.matcher(body);
        var priority = priorityMatcher.find() ? Priority.fromSymbol(priorityMatcher.group()) : Priority.NONE;
        description = Priority.SYMBOL_PATTERN.matcher(description).replaceAll("");

        var tags = new ArrayList<String>();
        var tagMatcher = TaskPatterns.TAG.matcher(body);
        while (tagMatcher.find()) {
            tags.add(tagMatcher.group(1));
        }

        return Optional.of(new Task(
                TaskRef.of(path, lineNumber, raw),
                symbol,
                TaskStatus.of(symbol),
                description.replaceAll("\\s{2,}", " ").strip(),
                priority,
                tags,
                dates.get(DateField.CREATED),
                dates.get(DateField.START),
                dates.get(DateField.SCHEDULED),
                dates.get(DateField.DUE),
                dates.get(DateField.DONE),
                raw));
    }
}
