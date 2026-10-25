package tungnn.tutor.java.huggingface.hub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ModelRefTest {

  @Test
  void acceptsValidId() {
    var ref = ModelRef.of("Helsinki-NLP/opus-mt-zh-vi");
    assertThat(ref.revision()).isEqualTo("main");
    assertThat(ref.displayName()).isEqualTo("Helsinki-NLP/opus-mt-zh-vi@main");
  }

  @Test
  void rejectsPathTraversalInId() {
    assertThatThrownBy(() -> ModelRef.of("../etc/passwd")).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> ModelRef.of("a/b/c")).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void rejectsPathTraversalInRevision() {
    assertThatThrownBy(() -> ModelRef.of("org/name", "../../x")).isInstanceOf(IllegalArgumentException.class);
  }
}
