package tungnn.tutor.java.tool.todo.service;

import java.time.LocalDate;
import java.util.List;
import tungnn.tutor.java.tool.todo.core.model.Priority;

public record NewTask(
        String description, String note, String heading, Priority priority, LocalDate due, List<String> tags) {

    public NewTask {
        tags = tags == null ? List.of() : tags;
    }
}
