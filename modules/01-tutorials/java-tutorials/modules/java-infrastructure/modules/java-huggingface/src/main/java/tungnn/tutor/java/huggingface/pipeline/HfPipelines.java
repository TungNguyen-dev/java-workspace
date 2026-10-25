package tungnn.tutor.java.huggingface.pipeline;

import java.util.ServiceLoader;
import tungnn.tutor.java.huggingface.config.HfConfig;
import tungnn.tutor.java.huggingface.exception.UnsupportedModelException;
import tungnn.tutor.java.huggingface.hub.ModelRef;

/** Entry point: {@code HfPipelines.load(Task.TRANSLATION, ModelRef.of("org/model"))}. */
public final class HfPipelines {

  private HfPipelines() {}

  public static <I, O> Pipeline<I, O> load(Task task, ModelRef model) {
    return load(task, model, HfConfig.fromEnv());
  }

  @SuppressWarnings("unchecked")
  public static <I, O> Pipeline<I, O> load(Task task, ModelRef model, HfConfig config) {
    for (PipelineFactory factory : ServiceLoader.load(PipelineFactory.class)) {
      if (factory.supports(task)) {
        return (Pipeline<I, O>) factory.create(task, model, config);
      }
    }
    throw new UnsupportedModelException("No pipeline registered for task " + task);
  }
}
