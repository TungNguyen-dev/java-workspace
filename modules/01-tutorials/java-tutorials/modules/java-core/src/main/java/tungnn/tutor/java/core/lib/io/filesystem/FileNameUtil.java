package tungnn.tutor.java.core.lib.io.filesystem;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class FileNameUtil {

  private static final int MAX_LENGTH = 255;
  private static final String DEFAULT_NAME = "default_filename";

  private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

  private static final Set<String> WINDOWS_RESERVED =
      Set.of(
          "CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7",
          "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9");

  private FileNameUtil() {}

  public static String sanitize(String input) {
    if (input == null || input.isBlank()) {
      return DEFAULT_NAME;
    }

    // 1. Strip diacritics (é -> e)
    String noDiacritics =
        DIACRITICS.matcher(Normalizer.normalize(input, Normalizer.Form.NFD)).replaceAll("");

    // 2. Keep only safe code points; whitespace -> '_'
    StringBuilder sb = new StringBuilder(noDiacritics.length());
    noDiacritics
        .codePoints()
        .forEach(
            cp -> {
              if (Character.isLetterOrDigit(cp) || cp == '_' || cp == '-' || cp == '.') {
                sb.appendCodePoint(cp);
              } else if (Character.isWhitespace(cp)) {
                sb.append('_');
              }
            });

    // 3. Collapse repeated dots; trim edges (leading '_', trailing '_' and '.')
    String sanitized = trimEdges(collapseDots(sb.toString()));
    if (sanitized.isEmpty()) {
      return DEFAULT_NAME;
    }

    // 4. Escape Windows reserved device names (incl. with extension: CON, CON.txt)
    if (isWindowsReserved(sanitized)) {
      sanitized = "_" + sanitized;
    }

    // 5. Enforce max length, preserving extension
    return enforceMaxLength(sanitized);
  }

  private static String collapseDots(String input) {
    StringBuilder sb = new StringBuilder(input.length());
    boolean lastWasDot = false;
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (c == '.') {
        if (!lastWasDot) sb.append(c);
        lastWasDot = true;
      } else {
        sb.append(c);
        lastWasDot = false;
      }
    }
    return sb.toString();
  }

  // Trim leading '_' and trailing '_' / '.' ; keep leading '.' (Unix dotfiles).
  private static String trimEdges(String s) {
    int start = 0;
    int end = s.length();
    while (start < end && s.charAt(start) == '_') start++;
    while (end > start && (s.charAt(end - 1) == '_' || s.charAt(end - 1) == '.')) end--;
    return s.substring(start, end);
  }

  private static boolean isWindowsReserved(String name) {
    int dot = name.indexOf('.');
    String base = (dot > 0) ? name.substring(0, dot) : name;
    return WINDOWS_RESERVED.contains(base.toUpperCase(Locale.ROOT));
  }

  private static String enforceMaxLength(String input) {
    if (input.length() <= MAX_LENGTH) return input;

    FileNameInfo info = parse(input);
    if (info.hasExtension()) {
      String ext = info.dottedExtension(); // ".txt"
      int allowed = MAX_LENGTH - ext.length();
      if (allowed > 0) {
        return info.name().substring(0, Math.min(info.name().length(), allowed)) + ext;
      }
    }
    return input.substring(0, MAX_LENGTH);
  }

  public static String formatPaddedIndex(int number, int width, String prefix) {
    // Format only the number -> prefix is safe even if it contains '%'
    return prefix + " " + String.format("%0" + Math.max(width, 0) + "d", number);
  }

  public static String appendFilenameSuffix(String filename, String suffix) {
    FileNameInfo info = parse(filename);
    if (info.name().endsWith(suffix)) {
      return filename;
    }
    return info.name() + suffix + info.dottedExtension();
  }

  public static String parseExtension(String filename) {
    return parse(filename).extension();
  }

  public static FileNameInfo parse(String filename) {
    if (filename == null) return new FileNameInfo("", "");
    int dot = filename.lastIndexOf('.');
    return (dot <= 0 || dot == filename.length() - 1)
        ? new FileNameInfo(filename, "")
        : new FileNameInfo(filename.substring(0, dot), filename.substring(dot + 1));
  }

  public record FileNameInfo(String name, String extension) {

    /** true nếu có phần mở rộng hợp lệ. */
    public boolean hasExtension() {
      return !extension.isEmpty();
    }

    /** Trả về extension kèm dấu chấm (".txt"), hoặc "" nếu không có. */
    public String dottedExtension() {
      return hasExtension() ? "." + extension : "";
    }

    /** Ghép lại thành tên file đầy đủ. */
    public String fullName() {
      return name + dottedExtension();
    }
  }
}
