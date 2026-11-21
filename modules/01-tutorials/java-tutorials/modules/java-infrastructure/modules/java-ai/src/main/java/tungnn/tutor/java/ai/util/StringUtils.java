package tungnn.tutor.java.ai.util;

public final class StringUtils {
  private StringUtils() {
    throw new UnsupportedOperationException("cannot instantiate utility class");
  }

  public static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  public static boolean isNotBlank(String value) {
    return !isBlank(value);
  }
}
