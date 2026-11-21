package tungnn.tutor.java.ai.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LlmOutputUtilsTest {

  @Test
  void shouldStripThinkBlock() {
    String input =
        """
        <think>
        suy luận bước 1
        suy luận bước 2
        </think>
        Xin chào
        """;

    assertThat(LlmOutputUtils.clean(input)).isEqualTo("Xin chào");
  }

  @Test
  void shouldUnwrapCodeFence() {
    assertThat(LlmOutputUtils.clean("```text\nXin chào\n```")).isEqualTo("Xin chào");
    assertThat(LlmOutputUtils.clean("```\nXin chào\n```")).isEqualTo("Xin chào");
  }

  @Test
  void shouldTrimAndHandleNullOrBlank() {
    assertThat(LlmOutputUtils.clean("  Xin chào  ")).isEqualTo("Xin chào");
    assertThat(LlmOutputUtils.clean("   ")).isEmpty();
    assertThat(LlmOutputUtils.clean("")).isEmpty();
    assertThat(LlmOutputUtils.clean(null)).isEmpty();
  }

  @Test
  void shouldKeepInnerCodeFences() {
    String text = "Use: ```java int x; ``` done";

    assertThat(LlmOutputUtils.clean(text)).isEqualTo(text);
  }
}
