package tungnn.tutor.java.tool.hub.api;

public record ToolDescriptor(String id, String name, String description, String category, ToolLayout layout) {
    public ToolDescriptor {
        if (layout == null) layout = ToolLayout.AUTO;
    }

    public ToolDescriptor(String id, String name, String description, String category) {
        this(id, name, description, category, ToolLayout.AUTO);
    }
}
