package tungnn.tutor.java.core.lib.stream;

import java.util.stream.Stream;

public class StreamIterateInJava9 {

  static void main() {
    Stream.iterate(0, n -> n <= 10, n -> n + 1).forEach(System.out::println);
  }
}
