package tungnn.tutor.java.huggingface.runtime;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OnnxValue;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import tungnn.tutor.java.huggingface.exception.HfException;

public final class TensorUtil {

  private TensorUtil() {}

  /** Creates an int64 tensor. The caller must close it. */
  public static OnnxTensor longs(OrtEnvironment env, long[][] data) {
    try {
      return OnnxTensor.createTensor(env, data);
    } catch (OrtException e) {
      throw new HfException("Cannot create int64 tensor", e);
    }
  }

  /** Returns the i-th output as tensor (owned by {@code result}; do not close separately). */
  public static OnnxTensor output(OrtSession.Result result, int index) {
    OnnxValue value = result.get(index);
    if (value instanceof OnnxTensor tensor) {
      return tensor;
    }
    throw new HfException("Output #" + index + " is not a tensor: " + value);
  }
}
