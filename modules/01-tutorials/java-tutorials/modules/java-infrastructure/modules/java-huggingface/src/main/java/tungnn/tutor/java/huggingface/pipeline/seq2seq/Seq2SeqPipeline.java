package tungnn.tutor.java.huggingface.pipeline.seq2seq;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import tungnn.tutor.java.huggingface.config.HfConfig;
import tungnn.tutor.java.huggingface.generation.GenerationConfig;
import tungnn.tutor.java.huggingface.generation.GreedySearch;
import tungnn.tutor.java.huggingface.hub.ModelFiles;
import tungnn.tutor.java.huggingface.hub.ModelRef;
import tungnn.tutor.java.huggingface.pipeline.Pipeline;
import tungnn.tutor.java.huggingface.pipeline.Task;
import tungnn.tutor.java.huggingface.runtime.OnnxModelRunner;
import tungnn.tutor.java.huggingface.runtime.TensorUtil;
import tungnn.tutor.java.huggingface.tokenizer.HfTokenizer;

/**
 * Encoder-decoder text-to-text (translation, summarization) with greedy decoding.
 *
 * <p>v1 limitations (deliberate, see README):
 *
 * <ul>
 *   <li>The decoder is re-run over the whole prefix at every step (no KV-cache): correct but
 *       O(n^2). KV-cache comes with Phase 5b.
 *   <li>Greedy only (no beam search), one sentence per run; {@code runBatch} is sequential.
 *   <li>Uses a single {@code tokenizer.json} for both encoding and decoding. Models with separate
 *       source/target tokenizers (original Marian repos) need a converted tokenizer.json.
 * </ul>
 *
 * Requires {@code encoder_model.onnx} and {@code decoder_model.onnx} (optimum export,
 * text2text-generation).
 */
public final class Seq2SeqPipeline implements Pipeline<String, String> {

  private static final int MAX_SOURCE_LENGTH = 512;

  private final Task task;
  private final String modelName;
  private final HfTokenizer tokenizer;
  private final OnnxModelRunner encoder;
  private final OnnxModelRunner decoder;
  private final GenerationConfig generation;

  Seq2SeqPipeline(Task task, ModelRef ref, Path dir, HfConfig config) {
    this.task = task;
    this.modelName = ref.displayName();
    this.generation = GenerationConfig.load(dir, config);
    this.tokenizer =
        HfTokenizer.load(
            ModelFiles.requireFile(dir, "tokenizer.json"),
            generation.padTokenId(),
            MAX_SOURCE_LENGTH);
    this.encoder = OnnxModelRunner.load(ModelFiles.findOnnx(dir, "encoder_model.onnx"), config);
    this.decoder = OnnxModelRunner.load(ModelFiles.findOnnx(dir, "decoder_model.onnx"), config);
  }

  @Override
  public String run(String input) {
    Objects.requireNonNull(input, "input must not be null");
    if (input.isBlank()) {
      return "";
    }
    var batch = tokenizer.encode(List.of(input));
    OrtEnvironment env = encoder.env();

    try (OnnxTensor ids = TensorUtil.longs(env, batch.inputIds());
        OnnxTensor mask = TensorUtil.longs(env, batch.attentionMask())) {

      var encoderInputs = new LinkedHashMap<String, OnnxTensor>();
      encoderInputs.put("input_ids", ids);
      encoderInputs.put("attention_mask", mask);

      try (var encoded = encoder.run(encoder.selectInputs(encoderInputs))) {
        OnnxTensor hidden = TensorUtil.output(encoded, 0);
        long[] generated = generate(env, mask, hidden);
        return tokenizer.decode(generated, true).strip();
      }
    }
  }

  private long[] generate(OrtEnvironment env, OnnxTensor encoderMask, OnnxTensor encoderHidden) {
    long[] sequence = new long[generation.maxNewTokens() + 1];
    int length = 0;
    sequence[length++] = generation.decoderStartTokenId();

    for (int step = 0; step < generation.maxNewTokens(); step++) {
      long next = nextToken(env, Arrays.copyOf(sequence, length), encoderMask, encoderHidden);
      if (next == generation.eosTokenId()) {
        break;
      }
      sequence[length++] = next;
    }
    return Arrays.copyOfRange(sequence, 1, length); // drop decoder start token
  }

  private long nextToken(
      OrtEnvironment env, long[] prefix, OnnxTensor encoderMask, OnnxTensor encoderHidden) {
    try (OnnxTensor decoderIds = TensorUtil.longs(env, new long[][] {prefix})) {
      var candidates = new LinkedHashMap<String, OnnxTensor>();
      candidates.put("input_ids", decoderIds);
      candidates.put("encoder_attention_mask", encoderMask);
      candidates.put("encoder_hidden_states", encoderHidden);

      try (var out = decoder.run(decoder.selectInputs(candidates))) {
        OnnxTensor logits = TensorUtil.output(out, 0);
        long[] shape = logits.getInfo().getShape(); // [1, seq, vocab]
        int seq = (int) shape[1];
        int vocab = (int) shape[2];
        return GreedySearch.argmax(
            logits.getFloatBuffer(), (seq - 1) * vocab, vocab, generation.bannedTokenIds());
      }
    }
  }

  @Override
  public Task task() {
    return task;
  }

  @Override
  public String modelName() {
    return modelName;
  }

  @Override
  public void close() {
    decoder.close();
    encoder.close();
    tokenizer.close();
  }
}
