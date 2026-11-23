package tungnn.tutor.java.ai.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.util.LlmClientUtils;

class SpringAiLlmClientTest {
  @Test
  void chatShouldSendSystemAndUserMessages() {
    var model = new FakeChatModel("Xin chào");
    var client = LlmClientUtils.of(model, LlmProvider.OPENAI, "gpt");
    var answer = client.chat("Be brief", "Hello");
    assertThat(answer).isEqualTo("Xin chào");
    var messages = model.prompts.getFirst().getInstructions();
    assertThat(messages)
        .extracting(m -> m.getMessageType())
        .containsExactly(MessageType.SYSTEM, MessageType.USER);
    assertThat(messages.get(1).getText()).isEqualTo("Hello");
  }

  @Test
  void chatWithoutSystemShouldOnlySendUserMessage() {
    var model = new FakeChatModel("ok");
    var client = LlmClientUtils.of(model, LlmProvider.OPENAI, "gpt");
    assertThat(client.chat("ping")).isEqualTo("ok");
    assertThat(model.prompts.getFirst().getInstructions())
        .extracting(m -> m.getMessageType())
        .containsExactly(MessageType.USER);
  }

  @Test
  void chatWithPromptShouldReturnRawResponse() {
    var client = LlmClientUtils.of(new FakeChatModel("raw"), LlmProvider.OLLAMA, "llama3");
    var response = client.chat(new Prompt("hi"));
    assertThat(response.getResult().getOutput().getText()).isEqualTo("raw");
  }

  @Test
  void chatShouldConvertStructuredOutput() {
    var client =
        LlmClientUtils.of(
            new FakeChatModel("{\"name\":\"Tung\",\"age\":30}"), LlmProvider.OPENAI, "gpt");
    var person = client.chat(null, "Who?", Person.class);
    assertThat(person).isEqualTo(new Person("Tung", 30));
  }

  private static final class FakeChatModel implements ChatModel {
    private final String answer;
    private final List<Prompt> prompts = new ArrayList<>();

    FakeChatModel(String answer) {
      this.answer = answer;
    }

    @Override
    public ChatResponse call(Prompt prompt) {
      prompts.add(prompt);
      return new ChatResponse(List.of(new Generation(new AssistantMessage(answer))));
    }
  }

  record Person(String name, int age) {}
}
