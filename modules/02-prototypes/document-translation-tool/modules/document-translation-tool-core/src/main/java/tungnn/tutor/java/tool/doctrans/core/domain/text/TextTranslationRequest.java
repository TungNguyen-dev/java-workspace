package tungnn.tutor.java.tool.doctrans.core.domain.text;

public interface TextTranslationRequest {

  TextUnit textUnit();

  LanguageCode targetLanguage();

  record TextUnit(String id, String content) {}

  record TranslationContext() {}

  record TranslationGlossary() {}

  record Standard(
      TextUnit textUnit,
      LanguageCode targetLanguage,
      TranslationContext translationContext,
      TranslationGlossary translationGlossary)
      implements TextTranslationRequest {}
}
