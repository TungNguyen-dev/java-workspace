package tungnn.tutor.java.spring.tool.doctrans.domain.shared;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import tungnn.tutor.java.spring.tool.doctrans.domain.document.SemanticUnit;
import tungnn.tutor.java.spring.tool.doctrans.domain.text.TextUnit;

public record TextUnitMapping(
    List<TextUnit> textUnits, Map<String, List<SemanticUnit>> semanticUnitsByTextId) {

  public TextUnitMapping {
    Objects.requireNonNull(textUnits, "textUnits must not be null");

    Objects.requireNonNull(semanticUnitsByTextId, "semanticUnitsByTextId must not be null");

    textUnits = List.copyOf(textUnits);

    var copiedAssociations = new LinkedHashMap<String, List<SemanticUnit>>();

    for (var entry : semanticUnitsByTextId.entrySet()) {

      var textId = entry.getKey();
      var semanticUnits = entry.getValue();

      Objects.requireNonNull(textId, "semanticUnitsByTextId contains null key");

      Objects.requireNonNull(
          semanticUnits, "semanticUnitsByTextId[" + textId + "] must not be null");

      copiedAssociations.put(textId, List.copyOf(semanticUnits));
    }

    semanticUnitsByTextId = Collections.unmodifiableMap(copiedAssociations);
  }
}
