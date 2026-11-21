package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.util.LlmOutputUtils;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtil;
import tungnn.tutor.java.jackson.v3.JsonUtil;
import tungnn.tutor.java.tool.doctrans.core.domain.text.AbstractTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;

/**
 * {@link tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator} backed by any LLM
 * provider supported by {@code java-ai} (Gemini, OpenAI, Anthropic, DeepSeek, Ollama, Hugging Face,
 * ...). The {@link LlmClient} is supplied from outside, so this class knows nothing about HTTP,
 * tokens or models.
 */
public class LlmTextTranslator extends AbstractTextTranslator {
  private static final Logger LOGGER = LoggerFactory.getLogger(LlmTextTranslator.class);
  private static final ObjectMapper OBJECT_MAPPER = JsonUtil.createJsonMapper();
  private static final String SYSTEM_PROMPT =
      ResourceUtil.getResourceAsString("prompt-trans-v2.md");
  private final LlmClient llmClient;
  private final int maxConcurrency;

  public LlmTextTranslator(LlmClient llmClient) {
    this(llmClient, defaultConcurrency(llmClient.provider()));
  }

  public LlmTextTranslator(LlmClient llmClient, int maxConcurrency) {
    this.llmClient = Objects.requireNonNull(llmClient, "llmClient must not be null");
    if (maxConcurrency < 1) {
      throw new IllegalArgumentException("maxConcurrency must be >= 1");
    }
    this.maxConcurrency = maxConcurrency;
  }

  /** Local servers handle one request at a time; hosted APIs tolerate more parallelism. */
  static int defaultConcurrency(LlmProvider provider) {
    return switch (provider) {
      case OLLAMA, OLLAMA_OPENAI, LM_STUDIO, VLLM -> 1;
      case GOOGLE_GENAI -> 3;
      case HUGGING_FACE, DEEPSEEK -> 10;
      default -> DEFAULT_MAX_CONCURRENCY;
    };
  }

  @Override
  public int maxConcurrency() {
    return maxConcurrency;
  }

  @Override
  protected TextTranslationResult.TextTranslation doTranslate(TextTranslationRequest request) {
    var userPrompt = OBJECT_MAPPER.writeValueAsString(request);
    LOGGER.trace("User Prompt (JSON payload): {}", userPrompt);
    var translated = LlmOutputUtils.clean(llmClient.chat(SYSTEM_PROMPT, userPrompt));
    var textUnit = request.textUnit();
    var source = textUnit.content();
    if (translated.isEmpty() && source != null && !source.isBlank()) {
      throw new IllegalStateException(
          "%s returned an empty translation for text unit [id=%s]"
              .formatted(llmClient.provider(), textUnit.id()));
    }
    return new TextTranslationResult.TextTranslation(
        textUnit.id(), source, translated, request.targetLanguage());
  }
}
