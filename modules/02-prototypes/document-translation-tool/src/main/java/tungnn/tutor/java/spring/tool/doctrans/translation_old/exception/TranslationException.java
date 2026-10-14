package tungnn.tutor.java.spring.tool.doctrans.translation_old.exception;

public class TranslationException extends RuntimeException {

  public TranslationException(String message) {
    super(message);
  }

  public TranslationException(String message, Throwable cause) {
    super(message, cause);
  }

  public TranslationException(Throwable cause) {
    super(cause);
  }
}
