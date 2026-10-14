package tungnn.tutor.java.spring.tool.doctrans.domain.document;

public record SemanticUnitTranslation(
    String textNodeId, int index, String sourceText, String translatedText) {}
