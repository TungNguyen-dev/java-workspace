package tungnn.tutor.java.huggingface.exception;

/** Thrown when a model repo does not contain what a pipeline needs (e.g. no ONNX export). */
public class UnsupportedModelException extends HfException {

  public UnsupportedModelException(String message) {
    super(message);
  }
}
