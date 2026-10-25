package tungnn.tutor.java.huggingface.tokenizer;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import tungnn.tutor.java.huggingface.exception.HfException;

/**
 * Thin wrapper over DJL's HuggingFace tokenizer ({@code tokenizer.json}). Padding is done here so
 * the result shape is under our control.
 */
public final class HfTokenizer implements AutoCloseable {

  private final HuggingFaceTokenizer tokenizer;
  private final long padId;

  private HfTokenizer(HuggingFaceTokenizer tokenizer, long padId) {
    this.tokenizer = tokenizer;
    this.padId = padId;
  }

  public static HfTokenizer load(Path tokenizerJson, long padId, int maxLength) {
    try {
      var options = Map.of("padding", "false", "truncation", "true", "maxLength", String.valueOf(maxLength));
      return new HfTokenizer(HuggingFaceTokenizer.newInstance(tokenizerJson, options), padId);
    } catch (IOException e) {
      throw new HfException("Cannot load tokenizer: " + tokenizerJson, e);
    }
  }

  /** Encodes and right-pads to the longest sequence of the batch. */
  public EncodedBatch encode(List<String> texts) {
    Encoding[] encodings = tokenizer.batchEncode(texts.toArray(new String[0]));
    int max = Arrays.stream(encodings).mapToInt(e -> e.getIds().length).max().orElse(0);

    long[][] ids = new long[encodings.length][max];
    long[][] mask = new long[encodings.length][max];
    for (int i = 0; i < encodings.length; i++) {
      long[] src = encodings[i].getIds();
      Arrays.fill(ids[i], padId);
      System.arraycopy(src, 0, ids[i], 0, src.length);
      Arrays.fill(mask[i], 0, src.length, 1L);
    }
    return new EncodedBatch(ids, mask);
  }

  public String decode(long[] ids, boolean skipSpecialTokens) {
    return tokenizer.decode(ids, skipSpecialTokens);
  }

  @Override
  public void close() {
    tokenizer.close();
  }

  /** Padded token ids and attention mask, shape {@code [batch][seq]}. */
  public record EncodedBatch(long[][] inputIds, long[][] attentionMask) {}
}
