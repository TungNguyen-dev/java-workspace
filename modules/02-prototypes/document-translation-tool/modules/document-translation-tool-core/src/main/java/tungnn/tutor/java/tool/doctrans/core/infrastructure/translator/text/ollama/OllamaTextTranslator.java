package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.ollama;

import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import tools.jackson.databind.ObjectMapper;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtil;
import tungnn.tutor.java.jackson.v3.JsonUtil;
import tungnn.tutor.java.tool.doctrans.core.domain.text.AbstractTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;

/**
 * {@link tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator} backed by a local Ollama
 * LLM via its OpenAI-compatible endpoint.
 */
public class OllamaTextTranslator extends AbstractTextTranslator {

  private static final Logger LOGGER = LoggerFactory.getLogger(OllamaTextTranslator.class);
  private static final ObjectMapper OBJECT_MAPPER = JsonUtil.createJsonMapper();
  private static final String SYSTEM_PROMPT =
      ResourceUtil.getResourceAsString("prompt-trans-v2.md");

  /** Reasoning models (DeepSeek-R1, Qwen3, ...) may prepend a chain-of-thought block. */
  private static final Pattern THINK_BLOCK = Pattern.compile("(?s)<think>.*?</think>");

  /** Some models wrap the whole answer in a Markdown code fence. */
  private static final Pattern CODE_FENCE =
      Pattern.compile("(?s)^```[a-zA-Z0-9_-]*\\R(.*?)\\R?```$");

  private final ChatClient chatClient;

  public OllamaTextTranslator(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  public static String cleanOutput(String raw) {
    if (raw == null) {
      return "";
    }
    var text = THINK_BLOCK.matcher(raw).replaceAll("").trim();
    var fence = CODE_FENCE.matcher(text);
    if (fence.matches()) {
      text = fence.group(1).trim();
    }
    return text;
  }

  @Override
  protected TextTranslationResult.TextTranslation doTranslate(TextTranslationRequest request) {
    var userPrompt = OBJECT_MAPPER.writeValueAsString(request);
    LOGGER.trace("User Prompt (JSON payload): {}", userPrompt);

    var raw = chatClient.prompt().system(SYSTEM_PROMPT).user(userPrompt).call().content();
    var translated = cleanOutput(raw);

    var textUnit = request.textUnit();
    var source = textUnit.content();
    if (translated.isEmpty() && source != null && !source.isBlank()) {
      throw new IllegalStateException(
          "Ollama returned an empty translation for text unit [id=" + textUnit.id() + "]");
    }

    return new TextTranslationResult.TextTranslation(
        textUnit.id(), source, translated, request.targetLanguage());
  }
}
