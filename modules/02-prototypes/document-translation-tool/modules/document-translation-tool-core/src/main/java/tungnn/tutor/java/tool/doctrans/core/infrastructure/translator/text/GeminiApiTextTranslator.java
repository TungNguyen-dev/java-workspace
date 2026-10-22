package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import tools.jackson.databind.ObjectMapper;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtil;
import tungnn.tutor.java.jackson.v3.JsonUtil;
import tungnn.tutor.java.tool.doctrans.core.domain.text.AbstractTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;

public class GeminiApiTextTranslator extends AbstractTextTranslator {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeminiApiTextTranslator.class);
  private static final ObjectMapper OBJECT_MAPPER = JsonUtil.createJsonMapper();
  private static final String SYSTEM_PROMPT =
      ResourceUtil.getResourceAsString("prompt-trans-v2.md");

  private final ChatClient chatClient;

  public GeminiApiTextTranslator(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  @Override
  protected TextTranslationResult.TextTranslation doTranslate(TextTranslationRequest request) {
    // Serialize the translation request into a JSON string
    var userPrompt = OBJECT_MAPPER.writeValueAsString(request);

    // Log the full request record before calling the Gemini API
    LOGGER.debug("User Prompt (JSON payload): {}", userPrompt);

    // Invoke Gemini API synchronously using Spring AI ChatClient
    var result = chatClient.prompt().system(SYSTEM_PROMPT).user(userPrompt).call().content();

    var textUnit = request.textUnit();
    var textTranslation =
        new TextTranslationResult.TextTranslation(
            textUnit.id(), textUnit.content(), result, request.targetLanguage());

    // Log the full result record and response length after receiving the response
    LOGGER.debug(
        "Received translation response from Gemini API. Result: [{}], Response length: {} chars",
        textTranslation,
        result != null ? result.length() : 0);

    return textTranslation;
  }
}
