package tungnn.tutor.java.tool.hub.app.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import tungnn.tutor.java.tool.hub.api.ParamSpec;

public final class FieldViewFactory {
    private FieldViewFactory() {}

    public static FieldView of(ParamSpec p) {
        String type = p.type() == null ? "string" : p.type().trim();
        String lower = type.toLowerCase(Locale.ROOT);
        List<String> options = List.of();
        Widget widget;
        if (lower.startsWith("enum:")) {
            widget = Widget.SELECT;
            options = Arrays.stream(type.substring("enum:".length()).split("\\|"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        } else {
            widget = switch (lower) {
                case "text" -> Widget.TEXTAREA;
                case "number" -> Widget.NUMBER;
                case "boolean" -> Widget.CHECKBOX;
                case "file" -> Widget.FILE;
                default -> Widget.INPUT;
            };
        }
        String label = p.description() != null && !p.description().isBlank() ? p.description() : p.name();
        return new FieldView(p.name(), label, widget, options, p.required(), p.description(), p.defaultValue());
    }

    public static List<FieldView> of(List<ParamSpec> params) {
        return params == null
                ? List.of()
                : params.stream().map(FieldViewFactory::of).toList();
    }
}
