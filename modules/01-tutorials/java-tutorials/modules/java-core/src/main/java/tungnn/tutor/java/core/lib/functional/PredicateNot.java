package tungnn.tutor.java.core.lib.functional;

import java.util.List;
import java.util.function.Predicate;

public class PredicateNot {

  static void main() {
    List<String> names = List.of("Alice", "Bob", "Carol", "", "John");

    // No empty
    names.stream().filter(Predicate.not(String::isEmpty)).forEach(System.out::println);

    // Chainable
    names.stream()
        .filter(Predicate.not(String::isEmpty).and(s -> s.length() > 3))
        .forEach(System.out::println);
  }
}
