package tungnn.tutor.java.ai.util;

import java.util.regex.Pattern;

public final class LlmOutputUtils {
  private static final Pattern THINK_BLOCK = Pattern.compile("(?is)<think>.*?</think>");
  private static final Pattern CODE_FENCE =
      Pattern.compile("(?s)^```[a-zA-Z0-9_-]*\\R(.*?)\\R?```$");

  private LlmOutputUtils() {
    throw new UnsupportedOperationException("cannot instantiate utility class");
  }

  public static String clean(String raw) {
    if (raw == null) {
      return "";
    }
    var text = THINK_BLOCK.matcher(raw).replaceAll("").trim();
    var fence = CODE_FENCE.matcher(text);
    if (fence.matches()) {
      text = fence.group(1).trim();
    }
    return text;
  }
}
