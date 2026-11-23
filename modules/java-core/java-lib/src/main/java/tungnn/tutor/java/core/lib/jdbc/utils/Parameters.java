package tungnn.tutor.java.core.lib.jdbc.utils;

import java.sql.JDBCType;
import java.util.List;

public record Parameters(List<Parameter> parameters) {

  public Parameters {
    parameters = (parameters == null) ? List.of() : List.copyOf(parameters);
  }

  public record Parameter(int pos, JDBCType type, Object value) {}
}
