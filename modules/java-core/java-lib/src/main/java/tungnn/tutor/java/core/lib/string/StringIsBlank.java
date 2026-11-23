package tungnn.tutor.java.core.lib.string;

public class StringIsBlank {

  // Common Unicode whitespace characters
  private static final String[] WHITESPACES = {
    "\u0020", // Space
    "\u0009", // Tab
    "\n", // Line Feed
    "\r", // Carriage Return
    "\u000C", // Form Feed
    "\u00A0", // No-Break Space
    "\u1680", // Ogham Space Mark
    "\u2000", // En Quad
    "\u2001", // Em Quad
    "\u2002", // En Space
    "\u2003", // Em Space
    "\u2004", // Three-Per-Em Space
    "\u2005", // Four-Per-Em Space
    "\u2006", // Six-Per-Em Space
    "\u2007", // Figure Space
    "\u2008", // Punctuation Space
    "\u2009", // Thin Space
    "\u200A", // Hair Space
    "\u2028", // Line Separator
    "\u2029", // Paragraph Separator
    "\u202F", // Narrow No-Break Space
    "\u205F", // Medium Mathematical Space
    "\u3000", // Ideographic Space - Full-width Space

    // Edge cases (not always considered blank)
    "\u200B", // Zero Width Space
    "\u200C", // Zero Width Non-Joiner
    "\u200D", // Zero Width Joiner
    "\u2060", // Word Joiner
    "\uFEFF" // BOM
  };

  static void main() {
    for (String s : WHITESPACES) {
      if (isNullOrBlank(s)) {
        System.out.println("String does not contain visible characters");
      }
    }
  }

  public static boolean isNullOrBlank(String s) {
    return s == null || s.isBlank();
  }
}
