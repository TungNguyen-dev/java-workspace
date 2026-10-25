package tungnn.tutor.java.huggingface.pipeline;

import tungnn.tutor.java.huggingface.config.HfConfig;
import tungnn.tutor.java.huggingface.hub.ModelRef;

/** SPI: registered in {@code META-INF/services}, discovered by {@link HfPipelines}. */
public interface PipelineFactory {

  boolean supports(Task task);

  Pipeline<?, ?> create(Task task, ModelRef model, HfConfig config);
}
