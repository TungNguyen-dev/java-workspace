package tungnn.tutor.java.tool.doctrans.core.infra.translator.text;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.tool.doctrans.core.domain.text.LanguageCode;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchRequest;
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

    private static TextTranslationBatchRequest batchRequest(String... contents) {
        var units = new java.util.ArrayList<TextTranslationRequest.TextUnit>();
        for (int i = 0; i < contents.length; i++) {
            units.add(new TextTranslationRequest.TextUnit("u" + i, contents[i]));
        }
        return new TextTranslationBatchRequest(units, LanguageCode.VI, null, null);
    }

    private static String translationOf(TextTranslationResult result) {
        return ((TextTranslationResult.Success) result).translation().translation();
    }

    @Test
    void shouldTranslateBatchWithSingleLlmCall() {
        var llmClient = llmClient();
        when(llmClient.chat(contains("MỘT mảng JSON"), anyString()))
                .thenReturn("```json\n"
                        + "[{\"id\":\"1\",\"translation\":\"Một\"},{\"id\":\"2\",\"translation\":\"Hai\"}]\n"
                        + "```");

        var results = new LlmTextTranslator(llmClient).translateBatch(batchRequest("一", "二"));

        assertThat(results.stream().map(LlmTextTranslatorTest::translationOf)).containsExactly("Một", "Hai");
        verify(llmClient, times(1)).chat(anyString(), anyString());
    }

    @Test
    void shouldFallBackToSingleCallsWhenBatchJsonIsInvalid() {
        var llmClient = llmClient();
        when(llmClient.chat(contains("MỘT mảng JSON"), anyString())).thenReturn("not json");
        when(llmClient.chat(contains("Phương tễ học"), anyString())).thenReturn("Dịch đơn");

        var results = new LlmTextTranslator(llmClient).translateBatch(batchRequest("一", "二"));

        assertThat(results.stream().map(LlmTextTranslatorTest::translationOf)).containsExactly("Dịch đơn", "Dịch đơn");
    }

    @Test
    void defaultBatchPolicyShouldDependOnProvider() {
        assertThat(LlmTextTranslator.defaultBatchPolicy(LlmProvider.OLLAMA).maxUnits())
                .isEqualTo(5);
        assertThat(LlmTextTranslator.defaultBatchPolicy(LlmProvider.GOOGLE_GENAI)
                        .maxUnits())
                .isEqualTo(20);
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
