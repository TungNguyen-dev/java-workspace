package tungnn.tutor.java.huggingface.pipeline.embedding;

import tungnn.tutor.java.huggingface.config.HfConfig;
import tungnn.tutor.java.huggingface.hub.ModelFiles;
import tungnn.tutor.java.huggingface.hub.ModelRef;
import tungnn.tutor.java.huggingface.hub.ModelRepository;
import tungnn.tutor.java.huggingface.pipeline.Pipeline;
import tungnn.tutor.java.huggingface.pipeline.PipelineFactory;
import tungnn.tutor.java.huggingface.pipeline.Task;

public final class EmbeddingPipelineFactory implements PipelineFactory {

  @Override
  public boolean supports(Task task) {
    return task == Task.EMBEDDING;
  }

  @Override
  public Pipeline<?, ?> create(Task task, ModelRef model, HfConfig config) {
    var dir = new ModelRepository(config).resolve(model, ModelFiles.metadataAnd("model.onnx"));
    return new EmbeddingPipeline(model, dir, config);
  }
}
