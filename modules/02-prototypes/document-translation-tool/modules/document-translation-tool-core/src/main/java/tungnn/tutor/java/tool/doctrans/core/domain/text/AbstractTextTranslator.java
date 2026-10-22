package tungnn.tutor.java.tool.doctrans.core.domain.text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTextTranslator implements TextTranslator {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTextTranslator.class);

  @Override
  public TextTranslationResult translate(TextTranslationRequest request) {
    try {
      // Log full request record before calling implementation
      LOGGER.debug("Starting translation. Request: [{}]", request);

      var textTranslation = doTranslate(request);

      // Log full translation result record on success
      LOGGER.debug("Completed translation successfully. Result: [{}]", textTranslation);

      var textUnit = request.textUnit();
      var textTranslationNormalized =
          new TextTranslationResult.TextTranslation(
              textUnit.id(),
              textUnit.content(),
              textTranslation.translation(),
              textTranslation.languageCode());

      return new TextTranslationResult.Success(textTranslationNormalized);
    } catch (Exception e) {
      LOGGER.error("Failed translation. Request: [{}]. Error: {}", request, e.getMessage(), e);
      return new TextTranslationResult.Failure(new TextTranslationResult.Error("", e.getMessage()));
    }
  }

  protected abstract TextTranslationResult.TextTranslation doTranslate(
      TextTranslationRequest request);
}
