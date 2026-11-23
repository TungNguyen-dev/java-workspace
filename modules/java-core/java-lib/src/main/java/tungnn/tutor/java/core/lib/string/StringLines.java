package tungnn.tutor.java.core.lib.string;

public class StringLines {

  static void main() {
    String text = "line1\r\nline2\nline3\rline4";
    text.lines().forEach(System.out::println);
  }
}
