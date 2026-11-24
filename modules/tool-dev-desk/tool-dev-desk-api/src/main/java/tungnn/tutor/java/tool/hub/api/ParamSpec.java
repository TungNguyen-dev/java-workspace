package tungnn.tutor.java.tool.hub.api;

public record ParamSpec(String name, String type, boolean required, String description, String defaultValue) {
    public ParamSpec(String name, String type, boolean required, String description) {
        this(name, type, required, description, null);
    }
}
