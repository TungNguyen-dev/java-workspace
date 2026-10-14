package tungnn.tutor.java.spring.tool.doctrans.domain.shared;

import java.util.List;
import tungnn.tutor.java.spring.tool.doctrans.domain.document.SemanticUnit;

public interface TextUnitMapper {

  TextUnitMapping map(List<SemanticUnit> semanticUnits);
}
