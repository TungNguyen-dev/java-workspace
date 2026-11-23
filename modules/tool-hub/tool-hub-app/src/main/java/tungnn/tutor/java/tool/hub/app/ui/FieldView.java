package tungnn.tutor.java.tool.hub.app.ui;

import java.util.List;

public record FieldView(
    String name,
    String label,
    Widget widget,
    List<String> options,
    boolean required,
    String description,
    String defaultValue) {}
