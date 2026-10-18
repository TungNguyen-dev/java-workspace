package tungnn.tutor.java.tool.doctrans.core.domain.text;

public abstract class AbstractTextTranslator implements TextTranslator {

  @Override
  public TextTranslationResult translate(TextTranslationRequest request) {
    try {
      var textTranslation = doTranslate(request);

      var textUnit = request.textUnit();
      var textTranslationNormalized =
          new TextTranslationResult.TextTranslation(
              textUnit.id(),
              textUnit.content(),
              textTranslation.translation(),
              textTranslation.languageCode());

      return new TextTranslationResult.Success(textTranslationNormalized);
    } catch (Exception e) {
      return new TextTranslationResult.Failure(new TextTranslationResult.Error("", e.getMessage()));
    }
  }

  protected abstract TextTranslationResult.TextTranslation doTranslate(
      TextTranslationRequest request);
}
