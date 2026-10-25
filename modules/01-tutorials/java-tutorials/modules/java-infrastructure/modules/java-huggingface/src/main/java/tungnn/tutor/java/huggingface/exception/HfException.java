package tungnn.tutor.java.huggingface.exception;

/** Base unchecked exception of the java-huggingface module. */
public class HfException extends RuntimeException {

  public HfException(String message) {
    super(message);
  }

  public HfException(String message, Throwable cause) {
    super(message, cause);
  }
}
