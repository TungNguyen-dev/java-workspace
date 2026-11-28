package tungnn.tutor.java.tool.todo.core.model;

public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE,
    CANCELLED;

    public static TaskStatus of(char symbol) {
        return switch (symbol) {
            case 'x', 'X' -> DONE;
            case '/' -> IN_PROGRESS;
            case '-' -> CANCELLED;
            default -> TODO;
        };
    }

    public boolean isOpen() {
        return this == TODO || this == IN_PROGRESS;
    }
}
