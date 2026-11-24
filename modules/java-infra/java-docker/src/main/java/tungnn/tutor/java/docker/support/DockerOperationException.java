package tungnn.tutor.java.docker.support;

public class DockerOperationException extends RuntimeException {
    public DockerOperationException(String message) {
        super(message);
    }

    public DockerOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
