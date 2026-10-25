package tungnn.tutor.java.huggingface.pipeline;

import java.util.List;

/**
 * Local inference pipeline, similar to Python's {@code pipeline(task, model)}.
 *
 * <p>Owns native resources: always close it (try-with-resources). Instances are not guaranteed to
 * be thread-safe yet; use one pipeline per thread or a pool until Phase 6 documents otherwise.
 */
public interface Pipeline<I, O> extends AutoCloseable {

  O run(I input);

  default List<O> runBatch(List<I> inputs) {
    return inputs.stream().map(this::run).toList();
  }

  Task task();

  String modelName();

  @Override
  void close();
}
