package tungnn.tutor.java.tool.hub.app.ui;

import java.util.List;
import tungnn.tutor.java.tool.hub.api.ToolLayout;

public final class LayoutResolver {
    private LayoutResolver() {}

    public static ToolLayout resolve(ToolLayout declared, List<FieldView> fields) {
        if (declared != null && declared != ToolLayout.AUTO) return declared;
        if (fields.stream().anyMatch(f -> f.widget() == Widget.FILE)) return ToolLayout.FILE;
        long text = fields.stream().filter(f -> f.widget().isText()).count();
        boolean restAreOptions =
                fields.stream().allMatch(f -> f.widget().isText() || f.widget().isOption());
        if (text == 1 && restAreOptions) return ToolLayout.TRANSFORM;
        return ToolLayout.FORM;
    }
}
