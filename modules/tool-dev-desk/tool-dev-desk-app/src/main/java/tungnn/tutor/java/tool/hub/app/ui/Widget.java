package tungnn.tutor.java.tool.hub.app.ui;

public enum Widget {
  INPUT,
  TEXTAREA,
  NUMBER,
  CHECKBOX,
  SELECT,
  FILE;

  public boolean isText() {
    return this == INPUT || this == TEXTAREA;
  }

  public boolean isOption() {
    return this == SELECT || this == CHECKBOX;
  }
}
