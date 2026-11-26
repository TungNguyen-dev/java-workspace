package tungnn.tutor.java.tool.doctrans.core.text.impl;

import tungnn.tutor.java.tool.doctrans.core.text.AbstractTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.text.TextTranslationResult;

public class DummyTextTranslator extends AbstractTextTranslator {
    @Override
    protected TextTranslationResult.TranslatedText doTranslate(TextTranslationRequest.Single request) {
        var textUnit = request.textUnit();
        var translatedContent = String.format("[DUMMY - %s] %s", request.targetLanguage(), textUnit.content());
        return new TextTranslationResult.TranslatedText(
                textUnit.id(), textUnit.content(), translatedContent, request.targetLanguage());
    }
}
