package tungnn.tutor.java.huggingface.runtime;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import tungnn.tutor.java.huggingface.config.HfConfig;
import tungnn.tutor.java.huggingface.exception.HfException;

/**
 * Generic ONNX model runner (tier 1 of the module): named input tensors in, named outputs out.
 * Doubles as the "escape hatch" for models that have no pipeline yet.
 *
 * <p>The returned {@link OrtSession.Result} and the input tensors are native resources: close them.
 */
public final class OnnxModelRunner implements AutoCloseable {

  private final OrtEnvironment env;
  private final OrtSession session;
  private final Path modelPath;

  private OnnxModelRunner(OrtEnvironment env, OrtSession session, Path modelPath) {
    this.env = env;
    this.session = session;
    this.modelPath = modelPath;
  }

  public static OnnxModelRunner load(Path onnxFile, HfConfig config) {
    var env = OrtEnvironment.getEnvironment();
    try (var options = new OrtSession.SessionOptions()) {
      if (config.intraOpThreads() > 0) {
        options.setIntraOpNumThreads(config.intraOpThreads());
      }
      return new OnnxModelRunner(env, env.createSession(onnxFile.toString(), options), onnxFile);
    } catch (OrtException e) {
      throw new HfException("Cannot load ONNX model: " + onnxFile, e);
    }
  }

  public OrtEnvironment env() {
    return env;
  }

  public Set<String> inputNames() {
    return session.getInputNames();
  }

  public Set<String> outputNames() {
    return session.getOutputNames();
  }

  public OrtSession.Result run(Map<String, OnnxTensor> inputs) {
    try {
      return session.run(inputs);
    } catch (OrtException e) {
      throw new HfException(
          "Inference failed for %s (model inputs=%s, given=%s)"
              .formatted(modelPath.getFileName(), inputNames(), inputs.keySet()),
          e);
    }
  }

  /** Keeps only the candidates the model actually declares as inputs. */
  public Map<String, OnnxTensor> selectInputs(Map<String, OnnxTensor> candidates) {
    var selected = new LinkedHashMap<String, OnnxTensor>();
    var names = inputNames();
    candidates.forEach(
        (name, tensor) -> {
          if (names.contains(name)) {
            selected.put(name, tensor);
          }
        });
    return selected;
  }

  @Override
  public void close() {
    try {
      session.close();
    } catch (OrtException e) {
      throw new HfException("Cannot close ONNX session", e);
    }
  }
}
