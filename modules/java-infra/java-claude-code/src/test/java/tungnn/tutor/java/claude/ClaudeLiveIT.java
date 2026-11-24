package tungnn.tutor.java.claude;

import static org.assertj.core.api.Assertions.assertThat;

import com.anthropic.client.AnthropicClient;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import tungnn.tutor.java.claude.client.ClaudeClientFactory;
import tungnn.tutor.java.claude.config.ChatOptions;
import tungnn.tutor.java.claude.config.ClaudeModels;
import tungnn.tutor.java.claude.message.ClaudeConversation;
import tungnn.tutor.java.claude.message.ClaudeMessageUtils;
import tungnn.tutor.java.claude.stream.ClaudeStreamUtils;
import tungnn.tutor.java.claude.structured.ClaudeStructuredUtils;
import tungnn.tutor.java.claude.token.ClaudeTokenUtils;
import tungnn.tutor.java.claude.tool.ClaudeToolUtils;

@EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".+")
class ClaudeLiveIT {
    private static final ChatOptions OPTIONS =
            ChatOptions.of(ClaudeModels.HAIKU).withMaxTokens(2_000);

    private static AnthropicClient client;

    record Capital(String country, String city) {}

    public static class GetTemperature implements Supplier<String> {
        @JsonPropertyDescription("City name")
        public String city;

        @Override
        public String get() {
            return "21 degrees Celsius in " + city;
        }
    }

    @BeforeAll
    static void setUp() {
        client = ClaudeClientFactory.create();
    }

    @Test
    void ask() {
        assertThat(ClaudeMessageUtils.ask(client, "Reply with exactly one word: pong", OPTIONS))
                .isNotBlank();
    }

    @Test
    void stream() {
        var chunks = new StringBuilder();
        var full = ClaudeStreamUtils.stream(client, "Say hello in one word", OPTIONS, chunks::append);
        assertThat(full).isNotBlank().isEqualTo(chunks.toString());
    }

    @Test
    void structured() {
        var capital = ClaudeStructuredUtils.ask(client, "What is the capital of France?", Capital.class, OPTIONS);
        assertThat(capital.city()).containsIgnoringCase("paris");
    }

    @Test
    void countTokens() {
        assertThat(ClaudeTokenUtils.countTokens(client, "Hello", OPTIONS)).isPositive();
    }

    @Test
    void tools() {
        var answer =
                ClaudeToolUtils.runTools(client, "What is the temperature in Hanoi?", OPTIONS, GetTemperature.class);
        assertThat(answer).contains("21");
    }

    @Test
    void conversation() {
        var conversation = new ClaudeConversation(client, OPTIONS);
        conversation.send("My favourite number is 42. Reply OK.");
        assertThat(conversation.send("What is my favourite number?")).contains("42");
        assertThat(conversation.turns()).isEqualTo(2);
    }
}
