package tungnn.tutor.java.core.lib.string;

public class StringStrip {

  static void main() {
    // IDEOGRAPHIC SPACE (U+3000)
    String str = "\u3000Hello World\u3000";

    System.out.println("Original: [" + str + "]");
    System.out.println("Original length: " + str.length());

    String trimmed = str.trim();
    String stripped = str.strip();
    String leading = str.stripLeading();
    String trailing = str.stripTrailing();

    System.out.println("trim(): [" + trimmed + "], length=" + trimmed.length());
    System.out.println("strip(): [" + stripped + "], length=" + stripped.length());
    System.out.println("stripLeading(): [" + leading + "], length=" + leading.length());
    System.out.println("stripTrailing(): [" + trailing + "], length=" + trailing.length());
  }
}
