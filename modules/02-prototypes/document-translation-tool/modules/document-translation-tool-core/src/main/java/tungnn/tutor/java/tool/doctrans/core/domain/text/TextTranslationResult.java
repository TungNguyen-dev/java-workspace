package tungnn.tutor.java.tool.doctrans.core.domain.text;

public interface TextTranslationResult {

  record TextTranslation(
      String id, String original, String translation, LanguageCode languageCode) {}

  record Error(String code, String message) {}

  record Success(TextTranslation translation) implements TextTranslationResult {}

  record Failure(Error error) implements TextTranslationResult {}
}
