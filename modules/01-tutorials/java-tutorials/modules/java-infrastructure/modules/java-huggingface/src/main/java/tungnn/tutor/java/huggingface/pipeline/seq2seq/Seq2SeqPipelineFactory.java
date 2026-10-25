package tungnn.tutor.java.huggingface.pipeline.seq2seq;

import tungnn.tutor.java.huggingface.config.HfConfig;
import tungnn.tutor.java.huggingface.hub.ModelFiles;
import tungnn.tutor.java.huggingface.hub.ModelRef;
import tungnn.tutor.java.huggingface.hub.ModelRepository;
import tungnn.tutor.java.huggingface.pipeline.Pipeline;
import tungnn.tutor.java.huggingface.pipeline.PipelineFactory;
import tungnn.tutor.java.huggingface.pipeline.Task;

public final class Seq2SeqPipelineFactory implements PipelineFactory {

  @Override
  public boolean supports(Task task) {
    return task == Task.TRANSLATION || task == Task.SUMMARIZATION;
  }

  @Override
  public Pipeline<?, ?> create(Task task, ModelRef model, HfConfig config) {
    var dir =
        new ModelRepository(config)
            .resolve(model, ModelFiles.metadataAnd("encoder_model.onnx", "decoder_model.onnx"));
    return new Seq2SeqPipeline(task, model, dir, config);
  }
}
