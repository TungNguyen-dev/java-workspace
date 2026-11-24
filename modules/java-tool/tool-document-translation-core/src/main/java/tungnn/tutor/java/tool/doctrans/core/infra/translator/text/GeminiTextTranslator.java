package tungnn.tutor.java.tool.doctrans.core.infra.translator.text;

import java.util.List;
import tools.jackson.databind.ObjectMapper;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtils;
import tungnn.tutor.java.jackson.v3.JsonUtils;
import tungnn.tutor.java.tool.doctrans.core.domain.text.AbstractTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;
import tungnn.tutor.java.tool.doctrans.core.infra.page.GeminiPage;
import tungnn.tutor.java.selenium.pool.WebDriverPool;

public class GeminiTextTranslator extends AbstractTextTranslator {

    private static final ObjectMapper OBJECT_MAPPER = JsonUtils.createJsonMapper();
    private static final String PROMPT_TEMPLATE_RESOURCE = "prompt-trans-v3.md";
    private static final String PROMPT_TEMPLATE = ResourceUtils.getResourceAsString(PROMPT_TEMPLATE_RESOURCE);
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
