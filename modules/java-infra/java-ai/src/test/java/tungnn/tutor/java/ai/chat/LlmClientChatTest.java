package tungnn.tutor.java.ai.chat;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.DefaultUsage;
import org.springframework.ai.chat.metadata.EmptyUsage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.ai.config.LlmProvider;

class LlmClientChatTest {

    @Test
    void shouldReturnTheTextAndTheReportedTokens() {
        var chat = new LlmClientChat(client(prompt -> new ChatResponse(
                List.of(new Generation(new AssistantMessage("xin chào"))),
                ChatResponseMetadata.builder().usage(new DefaultUsage(120, 30)).build())));

        var reply = chat.chatWithUsage("system", "user");

        assertThat(reply.text()).isEqualTo("xin chào");
        assertThat(reply.inputTokens()).isEqualTo(120);
        assertThat(reply.outputTokens()).isEqualTo(30);
        assertThat(reply.hasTokens()).isTrue();
    }

    @Test
    void shouldTreatEmptyUsageAsNotReported() {
        var chat = new LlmClientChat(client(prompt -> new ChatResponse(
                List.of(new Generation(new AssistantMessage("ok"))),
                ChatResponseMetadata.builder().usage(new EmptyUsage()).build())));

        var reply = chat.chatWithUsage(null, "user");

        assertThat(reply.text()).isEqualTo("ok");
        assertThat(reply.hasTokens()).isFalse();
    }

    @Test
    void shouldAddCacheTokensToTheInputWhenTheProviderExcludesThem() {
        Function<Prompt, ChatResponse> answer = prompt -> new ChatResponse(
                List.of(new Generation(new AssistantMessage("ok"))),
                ChatResponseMetadata.builder()
                        .usage(new DefaultUsage(100, 20, 120, null, 5000L, 200L))
                        .build());

        var anthropic = new LlmClientChat(client(LlmProvider.ANTHROPIC, answer)).chatWithUsage(null, "u");
        var openAi = new LlmClientChat(client(LlmProvider.OPENAI, answer)).chatWithUsage(null, "u");

        assertThat(anthropic.inputTokens()).isEqualTo(5300);
        assertThat(anthropic.cacheReadTokens()).isEqualTo(5000);
        assertThat(openAi.inputTokens()).isEqualTo(5000);
    }

    @Test
    void shouldSkipABlankSystemMessageAndReturnNullWithoutAResult() {
        var seen = new java.util.concurrent.atomic.AtomicReference<Prompt>();
        var chat = new LlmClientChat(client(prompt -> {
            seen.set(prompt);
            return new ChatResponse(List.of());
        }));

        var reply = chat.chatWithUsage("  ", "user");

        assertThat(seen.get().getInstructions()).hasSize(1);
        assertThat(reply.text()).isNull();
    }

    private static LlmClient client(Function<Prompt, ChatResponse> answer) {
        return client(LlmProvider.DEEPSEEK, answer);
    }

    private static LlmClient client(LlmProvider provider, Function<Prompt, ChatResponse> answer) {
        return new LlmClient() {
            @Override
            public LlmProvider provider() {
                return provider;
            }

            @Override
            public Optional<String> model() {
                return Optional.of("deepseek-chat");
            }

            @Override
            public String chat(String system, String user) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ChatResponse chat(Prompt prompt) {
                return answer.apply(prompt);
            }

            @Override
            public <T> T chat(String system, String user, Class<T> type) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Flux<String> stream(String system, String user) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ChatClient chatClient() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
