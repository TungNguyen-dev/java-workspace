package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text;

import java.util.List;
import tools.jackson.databind.ObjectMapper;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtil;
import tungnn.tutor.java.jackson.v3.JsonUtil;
import tungnn.tutor.java.tool.doctrans.core.domain.text.AbstractTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.page.GeminiPage;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.pool.WebDriverPool;

public class GeminiTextTranslator extends AbstractTextTranslator {

  private static final ObjectMapper OBJECT_MAPPER = JsonUtil.createJsonMapper();
  private static final String PROMPT_TEMPLATE =
      ResourceUtil.getResourceAsString("prompt-trans-v1.md");

  private final WebDriverPool webDriverPool;

  public GeminiTextTranslator(WebDriverPool webDriverPool) {
    this.webDriverPool = webDriverPool;
  }

  @Override
  protected TextTranslationResult.TextTranslation doTranslate(TextTranslationRequest request) {
    var driver = webDriverPool.borrowDriver();
    try {
      var geminiPage = new GeminiPage(driver);

      var translationRequestJson = OBJECT_MAPPER.writeValueAsString(request);
      var prompt = PROMPT_TEMPLATE.replace("{{translationRequest}}", translationRequestJson);

      var result = geminiPage.chat(prompt, List.of());

      var textUnit = request.textUnit();
      return new TextTranslationResult.TextTranslation(
          textUnit.id(), textUnit.content(), result, request.targetLanguage());
    } finally {
      webDriverPool.returnDriver(driver);
    }
  }
}
