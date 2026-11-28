package tungnn.tutor.java.tool.todo.core.model;

import java.time.LocalDate;
import java.util.List;

public record Task(
        TaskRef ref,
        char symbol,
        TaskStatus status,
        String description,
        Priority priority,
        List<String> tags,
        LocalDate created,
        LocalDate start,
        LocalDate scheduled,
        LocalDate due,
        LocalDate done,
        String raw) {}
