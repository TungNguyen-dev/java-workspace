package tungnn.tutor.java.tool.hub.app.ui;

import java.util.Map;

public record BindResult(Map<String, Object> input, Map<String, String> errors) {
  public boolean hasErrors() {
    return !errors.isEmpty();
  }
}
