package tungnn.tutor.java.huggingface.pipeline.embedding;

import ai.onnxruntime.OnnxTensor;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import tungnn.tutor.java.huggingface.config.HfConfig;
import tungnn.tutor.java.huggingface.exception.HfException;
import tungnn.tutor.java.huggingface.hub.ModelFiles;
import tungnn.tutor.java.huggingface.hub.ModelRef;
import tungnn.tutor.java.huggingface.pipeline.Pipeline;
import tungnn.tutor.java.huggingface.pipeline.Task;
import tungnn.tutor.java.huggingface.runtime.OnnxModelRunner;
import tungnn.tutor.java.huggingface.runtime.TensorUtil;
import tungnn.tutor.java.huggingface.tokenizer.HfTokenizer;

/**
 * Sentence embeddings: tokenize, run encoder, mean-pool over the attention mask, L2-normalize.
 * If the model already outputs a pooled 2-D tensor (e.g. {@code sentence_embedding}) it is used
 * as-is (then normalized).
 */
public final class EmbeddingPipeline implements Pipeline<String, float[]> {

  private static final int MAX_LENGTH = 512;

  private final String modelName;
  private final HfTokenizer tokenizer;
  private final OnnxModelRunner runner;

  EmbeddingPipeline(ModelRef ref, Path dir, HfConfig config) {
    this.modelName = ref.displayName();
    long padId = ModelFiles.readJson(dir, "config.json").path("pad_token_id").asLong(0);
    this.tokenizer = HfTokenizer.load(ModelFiles.requireFile(dir, "tokenizer.json"), padId, MAX_LENGTH);
    this.runner = OnnxModelRunner.load(ModelFiles.findOnnx(dir, "model.onnx"), config);
  }

  @Override
  public float[] run(String input) {
    return runBatch(List.of(input)).getFirst();
  }

  @Override
  public List<float[]> runBatch(List<String> inputs) {
    if (inputs.isEmpty()) {
      return List.of();
    }
    var batch = tokenizer.encode(inputs);
    long[][] zeros = new long[batch.inputIds().length][batch.inputIds()[0].length];
    var env = runner.env();

    try (OnnxTensor ids = TensorUtil.longs(env, batch.inputIds());
        OnnxTensor mask = TensorUtil.longs(env, batch.attentionMask());
        OnnxTensor types = TensorUtil.longs(env, zeros)) {

      var candidates = new LinkedHashMap<String, OnnxTensor>();
      candidates.put("input_ids", ids);
      candidates.put("attention_mask", mask);
      candidates.put("token_type_ids", types); // only used if the model declares it

      try (var result = runner.run(runner.selectInputs(candidates))) {
        Object value = TensorUtil.output(result, 0).getValue();
        return switch (value) {
          case float[][][] hidden -> pool(hidden, batch.attentionMask());
          case float[][] pooled -> java.util.Arrays.stream(pooled).map(EmbeddingPipeline::normalize).toList();
          default -> throw new HfException("Unexpected output type: " + value.getClass());
        };
      } catch (ai.onnxruntime.OrtException e) {
        throw new HfException("Cannot read embedding output", e);
      }
    }
  }

  private static List<float[]> pool(float[][][] hidden, long[][] mask) {
    var out = new java.util.ArrayList<float[]>(hidden.length);
    for (int b = 0; b < hidden.length; b++) {
      int dim = hidden[b][0].length;
      float[] sum = new float[dim];
      float count = 0;
      for (int t = 0; t < hidden[b].length; t++) {
        if (mask[b][t] == 0) {
          continue;
        }
        count++;
        for (int d = 0; d < dim; d++) {
          sum[d] += hidden[b][t][d];
        }
      }
      for (int d = 0; d < dim; d++) {
        sum[d] /= Math.max(count, 1f);
      }
      out.add(normalize(sum));
    }
    return out;
  }

  static float[] normalize(float[] vector) {
    double norm = 0;
    for (float v : vector) {
      norm += (double) v * v;
    }
    norm = Math.sqrt(norm);
    if (norm == 0) {
      return vector;
    }
    float[] result = new float[vector.length];
    for (int i = 0; i < vector.length; i++) {
      result[i] = (float) (vector[i] / norm);
    }
    return result;
  }

  @Override
  public Task task() {
    return Task.EMBEDDING;
  }

  @Override
  public String modelName() {
    return modelName;
  }

  @Override
  public void close() {
    runner.close();
    tokenizer.close();
  }
}
