package tungnn.tutor.java.tool.todo.service;

import java.time.LocalDate;
import java.util.List;
import tungnn.tutor.java.tool.todo.core.model.Priority;

public record TaskPatch(
        String text,
        LocalDate due,
        boolean clearDue,
        Priority priority,
        List<String> addTags,
        List<String> removeTags) {

    public TaskPatch {
        addTags = addTags == null ? List.of() : addTags;
        removeTags = removeTags == null ? List.of() : removeTags;
    }

    public boolean isEmpty() {
        return text == null
                && due == null
                && !clearDue
                && priority == null
                && addTags.isEmpty()
                && removeTags.isEmpty();
    }
}
