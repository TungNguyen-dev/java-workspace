package tungnn.tutor.java.huggingface.generation;

import java.nio.FloatBuffer;
import java.util.BitSet;

/** Greedy decoding helpers. Beam search is planned (see README, Phase 5b). */
public final class GreedySearch {

  private GreedySearch() {}

  /**
   * Index of the largest logit in {@code logits[offset .. offset+vocabSize)}, ignoring banned ids.
   * Reads directly from the (native) buffer to avoid copying {@code [seq * vocab]} floats per step.
   */
  public static int argmax(FloatBuffer logits, int offset, int vocabSize, BitSet banned) {
    int best = -1;
    float bestValue = Float.NEGATIVE_INFINITY;
    for (int i = 0; i < vocabSize; i++) {
      if (banned != null && banned.get(i)) {
        continue;
      }
      float value = logits.get(offset + i);
      if (best < 0 || value > bestValue) {
        best = i;
        bestValue = value;
      }
    }
    if (best < 0) {
      throw new IllegalStateException("All tokens are banned");
    }
    return best;
  }
}
