package tungnn.tutor.java.tool.hub.api;

public record ToolResult(boolean success, Object data, String message) {
  public static ToolResult ok(Object data) {
    return new ToolResult(true, data, null);
  }

  public static ToolResult fail(String msg) {
    return new ToolResult(false, null, msg);
  }
}
