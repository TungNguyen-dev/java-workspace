package tungnn.tutor.java.tool.doctrans.core.infra.translator.text;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;

class LlmTextTranslatorTest {

    private static TextTranslationRequest request(String content) {
        var textUnit = new TextTranslationRequest.TextUnit("unit-1", content);
        return new TextTranslationRequest.Standard(textUnit, LanguageCode.VI, null, null);
    }

    private static LlmClient llmClient() {
        var llmClient = mock(LlmClient.class);
        when(llmClient.provider()).thenReturn(LlmProvider.HUGGING_FACE);
        return llmClient;
    }

    private static LlmClient llmClientReturning(String content) {
        var llmClient = llmClient();
        when(llmClient.chat(anyString(), anyString())).thenReturn(content);
        return llmClient;
    }

    @Test
    void shouldTranslateSuccessfully() {
        var translator = new LlmTextTranslator(llmClientReturning("Thuốc này dùng để điều trị cao huyết áp."));

        var result = translator.translate(request("本品用于治疗高血压。"));

        assertThat(result).isInstanceOf(TextTranslationResult.Success.class);
        var translation = ((TextTranslationResult.Success) result).translation();
        assertThat(translation.id()).isEqualTo("unit-1");
        assertThat(translation.original()).isEqualTo("本品用于治疗高血压。");
        assertThat(translation.translation()).isEqualTo("Thuốc này dùng để điều trị cao huyết áp.");
        assertThat(translation.languageCode()).isEqualTo(LanguageCode.VI);
    }

    @Test
    void shouldSendSystemPromptFromResource() {
        var llmClient = llmClientReturning("Xin chào");

        new LlmTextTranslator(llmClient).translate(request("Hello"));

        verify(llmClient).chat(contains("Phương tễ học"), anyString());
    }

    @Test
    void shouldStripThinkBlockAndCodeFence() {
        var translator =
                new LlmTextTranslator(llmClientReturning("<think>suy luận...</think>\n```text\nXin chào\n```"));

        var result = translator.translate(request("Hello"));

        var translation = ((TextTranslationResult.Success) result).translation();
        assertThat(translation.translation()).isEqualTo("Xin chào");
    }

    @Test
    void shouldReturnFailureWhenModelCallThrows() {
        var llmClient = llmClient();
        when(llmClient.chat(anyString(), anyString())).thenThrow(new RuntimeException("HTTP 429"));

        var result = new LlmTextTranslator(llmClient).translate(request("Hello"));

        assertThat(result).isInstanceOf(TextTranslationResult.Failure.class);
        assertThat(((TextTranslationResult.Failure) result).error().message()).contains("429");
    }

    @Test
    void shouldReturnFailureWhenTranslationIsEmpty() {
        var translator = new LlmTextTranslator(llmClientReturning("   "));

        var result = translator.translate(request("Hello"));

        assertThat(result).isInstanceOf(TextTranslationResult.Failure.class);
    }

    @Test
    void shouldRejectInvalidConcurrency() {
        assertThatThrownBy(() -> new LlmTextTranslator(llmClient(), 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void defaultConcurrencyShouldDependOnProvider() {
        assertThat(LlmTextTranslator.defaultConcurrency(LlmProvider.OLLAMA)).isEqualTo(1);
        assertThat(LlmTextTranslator.defaultConcurrency(LlmProvider.GOOGLE_GENAI))
                .isEqualTo(10);
        assertThat(LlmTextTranslator.defaultConcurrency(LlmProvider.DEEPSEEK)).isEqualTo(10);
    }
}
