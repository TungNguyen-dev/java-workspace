package tungnn.tutor.java.tool.hub.api;

public record ToolFile(String filename, String contentType, byte[] content) {
  public ToolFile {
    if (filename == null || filename.isBlank()) filename = "file";
    if (contentType == null || contentType.isBlank()) contentType = "application/octet-stream";
    if (content == null) content = new byte[0];
  }

  public long size() {
    return content.length;
  }
}
