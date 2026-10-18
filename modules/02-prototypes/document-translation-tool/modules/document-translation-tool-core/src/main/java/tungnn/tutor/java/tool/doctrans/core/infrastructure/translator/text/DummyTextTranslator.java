package tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text;

import tungnn.tutor.java.tool.doctrans.core.domain.text.AbstractTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;

public class DummyTextTranslator extends AbstractTextTranslator {

  @Override
  protected TextTranslationResult.TextTranslation doTranslate(TextTranslationRequest request) {
    var textUnit = request.textUnit();

    var translatedContent =
        String.format("[DUMMY - %s] %s", request.targetLanguage(), textUnit.content());

    return new TextTranslationResult.TextTranslation(
        textUnit.id(), textUnit.content(), translatedContent, request.targetLanguage());
  }
}
