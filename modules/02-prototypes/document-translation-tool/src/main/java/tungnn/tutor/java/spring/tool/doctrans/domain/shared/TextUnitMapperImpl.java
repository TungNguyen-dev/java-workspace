package tungnn.tutor.java.spring.tool.doctrans.domain.shared;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import tungnn.tutor.java.spring.tool.doctrans.domain.document.SemanticUnit;
import tungnn.tutor.java.spring.tool.doctrans.domain.text.TextUnit;

public final class TextUnitMapperImpl implements TextUnitMapper {

  @Override
  public TextUnitMapping map(List<SemanticUnit> semanticUnits) {

    Objects.requireNonNull(semanticUnits, "semanticUnits must not be null");

    validateSemanticUnits(semanticUnits);

    var textUnitsByContent = new LinkedHashMap<String, TextUnit>();

    var semanticUnitsByTextId = new LinkedHashMap<String, List<SemanticUnit>>();

    for (var semanticUnit : semanticUnits) {
      var textContent = semanticUnit.semanticText();

      var textUnit = textUnitsByContent.computeIfAbsent(textContent, this::createTextUnit);

      semanticUnitsByTextId
          .computeIfAbsent(textUnit.textId(), ignored -> new ArrayList<>())
          .add(semanticUnit);
    }

    return new TextUnitMapping(List.copyOf(textUnitsByContent.values()), semanticUnitsByTextId);
  }

  private TextUnit createTextUnit(String textContent) {

    return new TextUnit(UUID.randomUUID().toString(), textContent);
  }

  private void validateSemanticUnits(List<SemanticUnit> semanticUnits) {

    for (var index = 0; index < semanticUnits.size(); index++) {

      var semanticUnit = semanticUnits.get(index);

      if (semanticUnit == null) {
        throw new IllegalArgumentException("semanticUnits[" + index + "] must not be null");
      }

      if (semanticUnit.semanticText() == null) {
        throw new IllegalArgumentException(
            "semanticUnits[" + index + "].semanticText " + "must not be null");
      }

      if (semanticUnit.textNodeId() == null || semanticUnit.textNodeId().isBlank()) {

        throw new IllegalArgumentException(
            "semanticUnits[" + index + "].textNodeId " + "must not be blank");
      }

      if (semanticUnit.index() < 0) {
        throw new IllegalArgumentException(
            "semanticUnits[" + index + "].index " + "must not be negative");
      }
    }
  }
}
