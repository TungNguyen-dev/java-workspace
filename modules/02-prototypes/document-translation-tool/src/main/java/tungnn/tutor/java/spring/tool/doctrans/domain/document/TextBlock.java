package tungnn.tutor.java.spring.tool.doctrans.domain.document;

import java.util.List;

public interface TextBlock {

  String textBlockId();

  BlockType blockType();

  List<SemanticUnit> semanticUnits();

  boolean replaceContent(List<SemanticUnitTranslation> translatedUnits);

  enum BlockType {
    PARAGRAPH,
    TABLE_CELL,
    TEXT_BOX,
    SHAPE,
    CAPTION
  }
}
