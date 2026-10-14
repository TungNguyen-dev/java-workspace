package tungnn.tutor.java.spring.tool.doctrans.domain.text;

import java.util.List;

public class DummyTextTranslator extends AbstractTextTranslator {

  @Override
  protected List<TextUnit> doTranslate(TextTranslationRequest request) {
    return request.texts().stream()
        .map(
            textUnit -> {
              String translatedContent =
                  String.format(
                      "[DUMMY - %s] %s", request.targetLanguage(), textUnit.textContent());

              return new TextUnit(textUnit.textId(), translatedContent);
            })
        .toList();
  }
}
