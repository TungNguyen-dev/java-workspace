package tungnn.tutor.java.spring.tool.doctrans.domain.text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tungnn.tutor.java.spring.tool.doctrans.shared.TranslationError;

public abstract class AbstractTextTranslator implements TextTranslator {

  @Override
  public TextTranslationResult translate(TextTranslationRequest request) {
    try {
      validateRequest(request);

      var originalTextUnits = request.texts();

      var originalTextUnitMap = createTextUnitMap(originalTextUnits);

      var translatedTextUnits = doTranslate(request);

      validateTranslatedTextUnits(translatedTextUnits, originalTextUnitMap);

      var translations =
          translatedTextUnits.stream()
              .map(
                  translatedTextUnit ->
                      new TextUnitTranslation(
                          translatedTextUnit.textId(),
                          originalTextUnitMap.get(translatedTextUnit.textId()),
                          translatedTextUnit.textContent(),
                          request.targetLanguage()))
              .toList();

      return new TextTranslationResult.Success(translations);

    } catch (Exception e) {
      var error = new TranslationError("", e.getMessage());

      return new TextTranslationResult.Failure(error);
    }
  }

  protected abstract List<TextUnit> doTranslate(TextTranslationRequest request);

  private void validateRequest(TextTranslationRequest request) {

    if (request == null) {
      throw new IllegalArgumentException("request must not be null");
    }

    if (request.texts() == null) {
      throw new IllegalArgumentException("request.texts must not be null");
    }

    if (request.targetLanguage() == null) {
      throw new IllegalArgumentException("request.targetLanguage must not be null");
    }

    for (var index = 0; index < request.texts().size(); index++) {

      var textUnit = request.texts().get(index);

      if (textUnit == null) {
        throw new IllegalArgumentException("request.texts[" + index + "] must not be null");
      }

      if (textUnit.textId() == null || textUnit.textId().isBlank()) {

        throw new IllegalArgumentException("request.texts[" + index + "].textId must not be blank");
      }

      if (textUnit.textContent() == null) {
        throw new IllegalArgumentException(
            "request.texts[" + index + "].textContent must not be null");
      }
    }
  }

  private Map<String, String> createTextUnitMap(List<TextUnit> textUnits) {

    var result = new HashMap<String, String>();

    for (var textUnit : textUnits) {
      var previous = result.put(textUnit.textId(), textUnit.textContent());

      if (previous != null) {
        throw new IllegalStateException("Duplicate textId: " + textUnit.textId());
      }
    }

    return result;
  }

  private void validateTranslatedTextUnits(
      List<TextUnit> translatedTextUnits, Map<String, String> originalTextUnitMap) {

    if (translatedTextUnits == null) {
      throw new IllegalStateException("Translator returned null translations");
    }

    var translatedTextIds = new java.util.HashSet<String>();

    for (var translatedTextUnit : translatedTextUnits) {

      if (translatedTextUnit == null) {
        throw new IllegalStateException("Translator returned null TextUnit");
      }

      var textId = translatedTextUnit.textId();

      if (!originalTextUnitMap.containsKey(textId)) {
        throw new IllegalStateException("Unknown translated textId: " + textId);
      }

      if (!translatedTextIds.add(textId)) {
        throw new IllegalStateException("Duplicate translated textId: " + textId);
      }
    }

    if (translatedTextIds.size() != originalTextUnitMap.size()) {

      throw new IllegalStateException("Translator returned incomplete translations");
    }
  }
}
