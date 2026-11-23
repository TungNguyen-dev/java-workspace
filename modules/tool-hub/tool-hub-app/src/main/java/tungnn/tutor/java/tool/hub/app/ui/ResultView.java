package tungnn.tutor.java.tool.hub.app.ui;

import java.util.List;

public record ResultView(
    Kind kind,
    String text,
    List<String> columns,
    List<List<String>> rows,
    String downloadToken,
    String filename,
    long size,
    String message,
    long elapsedMs) {
  public boolean textual() {
    return kind == Kind.TEXT || kind == Kind.JSON || kind == Kind.EMPTY;
  }

  public enum Kind {
    EMPTY,
    TEXT,
    JSON,
    TABLE,
    FILE
  }
}
