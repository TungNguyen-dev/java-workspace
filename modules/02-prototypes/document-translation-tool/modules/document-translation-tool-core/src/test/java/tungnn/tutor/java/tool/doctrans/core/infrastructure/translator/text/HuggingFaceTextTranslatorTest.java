package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.huggingface.HuggingFaceTextTranslator;

class HuggingFaceTextTranslatorTest {

  private static TextTranslationRequest request(String content) {
    var textUnit = new TextTranslationRequest.TextUnit("unit-1", content);
    return new TextTranslationRequest.Standard(textUnit, LanguageCode.VI, null, null);
  }

  private static ChatClient chatClientReturning(String content) {
    var chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
    when(chatClient.prompt().system(anyString()).user(anyString()).call().content())
        .thenReturn(content);
    return chatClient;
  }

  @Test
  void shouldTranslateSuccessfully() {
    var translator =
        new HuggingFaceTextTranslator(
            chatClientReturning("Thuốc này dùng để điều trị cao huyết áp."));

    var result = translator.translate(request("本品用于治疗高血压。"));

    assertThat(result).isInstanceOf(TextTranslationResult.Success.class);
    var translation = ((TextTranslationResult.Success) result).translation();
    assertThat(translation.id()).isEqualTo("unit-1");
    assertThat(translation.original()).isEqualTo("本品用于治疗高血压。");
    assertThat(translation.translation()).isEqualTo("Thuốc này dùng để điều trị cao huyết áp.");
    assertThat(translation.languageCode()).isEqualTo(LanguageCode.VI);
  }

  @Test
  void shouldStripThinkBlock() {
    var translator =
        new HuggingFaceTextTranslator(chatClientReturning("<think>suy luận...</think>\nXin chào"));

    var result = translator.translate(request("Hello"));

    var translation = ((TextTranslationResult.Success) result).translation();
    assertThat(translation.translation()).isEqualTo("Xin chào");
  }

  @Test
  void shouldReturnFailureWhenModelCallThrows() {
    var chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
    when(chatClient.prompt().system(anyString()).user(anyString()).call().content())
        .thenThrow(new RuntimeException("HTTP 429"));

    var result = new HuggingFaceTextTranslator(chatClient).translate(request("Hello"));

    assertThat(result).isInstanceOf(TextTranslationResult.Failure.class);
    assertThat(((TextTranslationResult.Failure) result).error().message()).contains("429");
  }

  @Test
  void shouldReturnFailureWhenTranslationIsEmpty() {
    var translator = new HuggingFaceTextTranslator(chatClientReturning("   "));

    var result = translator.translate(request("Hello"));

    assertThat(result).isInstanceOf(TextTranslationResult.Failure.class);
  }

  @Test
  void cleanOutputShouldHandleFencesAndNull() {
    assertThat(HuggingFaceTextTranslator.cleanOutput("```text\nXin chào\n```"))
        .isEqualTo("Xin chào");
    assertThat(HuggingFaceTextTranslator.cleanOutput("  Xin chào  ")).isEqualTo("Xin chào");
    assertThat(HuggingFaceTextTranslator.cleanOutput(null)).isEmpty();
  }
}
