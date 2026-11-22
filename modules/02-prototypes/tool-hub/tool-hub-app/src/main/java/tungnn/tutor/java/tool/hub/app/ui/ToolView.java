package tungnn.tutor.java.tool.hub.app.ui;

import java.util.List;
import java.util.Locale;
import tungnn.tutor.java.tool.hub.api.ToolDescriptor;
import tungnn.tutor.java.tool.hub.api.ToolLayout;

public record ToolView(ToolDescriptor descriptor, ToolLayout layout, List<FieldView> fields) {
  public String id() {
    return descriptor.id();
  }

  public String name() {
    return descriptor.name() == null || descriptor.name().isBlank()
        ? descriptor.id()
        : descriptor.name();
  }

  public String description() {
    return descriptor.description();
  }

  public String category() {
    return descriptor.category() == null || descriptor.category().isBlank()
        ? "other"
        : descriptor.category();
  }

  public String template() {
    return "tools/" + layout.name().toLowerCase(Locale.ROOT);
  }

  public FieldView primaryText() {
    return fields.stream()
        .filter(f -> f.widget() == Widget.TEXTAREA)
        .findFirst()
        .or(() -> fields.stream().filter(f -> f.widget().isText()).findFirst())
        .orElse(null);
  }

  public List<FieldView> secondaryFields() {
    FieldView primary = primaryText();
    return fields.stream().filter(f -> f != primary).toList();
  }

  public List<FieldView> fileFields() {
    return fields.stream().filter(f -> f.widget() == Widget.FILE).toList();
  }

  public List<FieldView> nonFileFields() {
    return fields.stream().filter(f -> f.widget() != Widget.FILE).toList();
  }
}
