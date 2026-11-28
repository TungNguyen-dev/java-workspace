package tungnn.tutor.java.tool.todo.service;

public class StaleTaskException extends RuntimeException {

    public StaleTaskException(String message) {
        super(message);
    }
}
