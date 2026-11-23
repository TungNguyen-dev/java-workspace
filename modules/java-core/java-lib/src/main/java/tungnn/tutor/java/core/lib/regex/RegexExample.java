package tungnn.tutor.java.core.lib.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexExample {

  static void main() {
    String input = "Contact us at contact@example.com or support@example.org";
    String regex = "(\\w+@\\w+\\.\\w+)";

    try {
      // 1. Compile the pattern
      Pattern pattern = Pattern.compile(regex);

      // 2. Create matcher
      Matcher matcher = pattern.matcher(input);

      // 3. Find multiple matches
      while (matcher.find()) {
        System.out.println("Full match (group 0): " + matcher.group(0));
        System.out.println("Captured group (group 1): " + matcher.group(1));
        System.out.println("Start index: " + matcher.start());
        System.out.println("End index: " + matcher.end());
        System.out.println("-----");
      }

    } catch (PatternSyntaxException ex) {
      // 4. Handle invalid regex syntax
      System.err.println("Invalid regular expression syntax:");
      System.err.println("Description: " + ex.getDescription());
      System.err.println("Error index: " + ex.getIndex());
      System.err.println("Erroneous pattern: " + ex.getPattern());
    }
  }
}
