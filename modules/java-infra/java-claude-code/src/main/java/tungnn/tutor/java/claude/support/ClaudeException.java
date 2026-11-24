package tungnn.tutor.java.claude.support;

public class ClaudeException extends RuntimeException {
    public ClaudeException(String message) {
        super(message);
    }

    public ClaudeException(String message, Throwable cause) {
        super(message, cause);
    }
}
