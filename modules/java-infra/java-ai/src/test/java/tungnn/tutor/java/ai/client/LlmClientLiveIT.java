package tungnn.tutor.java.ai.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.util.LlmClientUtils;
import tungnn.tutor.java.ai.util.LlmOutputUtils;

@EnabledIfEnvironmentVariable(named = "LLM_LIVE_TEST", matches = "true")
class LlmClientLiveIT {
    private static void assertAnswers(LlmProvider provider) {
        var answer = LlmClientUtils.fromEnv(provider).chat("Reply with exactly one word: pong");
        assertThat(LlmOutputUtils.clean(answer)).isNotBlank();
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "GEMINI_API_KEY", matches = ".+")
    void googleGenAi() {
        assertAnswers(LlmProvider.GOOGLE_GENAI);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
    void openAi() {
        assertAnswers(LlmProvider.OPENAI);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".+")
    void anthropic() {
        assertAnswers(LlmProvider.ANTHROPIC);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "DEEPSEEK_API_KEY", matches = ".+")
    void deepSeek() {
        assertAnswers(LlmProvider.DEEPSEEK);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "OLLAMA_MODEL", matches = ".+")
    void ollama() {
        assertAnswers(LlmProvider.OLLAMA);
    }
}
