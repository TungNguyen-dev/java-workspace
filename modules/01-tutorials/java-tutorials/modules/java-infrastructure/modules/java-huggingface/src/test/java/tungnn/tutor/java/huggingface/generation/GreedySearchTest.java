package tungnn.tutor.java.huggingface.generation;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.FloatBuffer;
import java.util.BitSet;
import org.junit.jupiter.api.Test;

class GreedySearchTest {

  @Test
  void picksLargestLogitAtOffset() {
    var logits = FloatBuffer.wrap(new float[] {9f, 9f, 9f, 0.1f, 0.7f, 0.2f});
    assertThat(GreedySearch.argmax(logits, 3, 3, new BitSet())).isEqualTo(1);
  }

  @Test
  void skipsBannedTokens() {
    var logits = FloatBuffer.wrap(new float[] {0.1f, 0.9f, 0.5f});
    var banned = new BitSet();
    banned.set(1);
    assertThat(GreedySearch.argmax(logits, 0, 3, banned)).isEqualTo(2);
  }
}
