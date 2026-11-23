package tungnn.tutor.java.core.lib.io.filesystem;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DirectoryTreeUtils {

  private static final Set<String> DEFAULT_EXCLUDED_NAMES =
      Set.of(
          ".git",
          ".idea",
          ".vscode",
          ".venv",
          "venv",
          "node_modules",
          "target",
          "build",
          "out",
          "bin",
          "__pycache__",
          ".mvn",
          ".gradle",
          ".ds_store");

  private static final String INDENT_BLANK = "  ";
  private static final String INDENT_LINE = "│  ";
  private static final String BRANCH_TEE = "├── ";
  private static final String BRANCH_END = "└── ";

  private DirectoryTreeUtils() {}

  /**
   * Generates a visual directory tree using default exclusions.
   *
   * @param sourceDir the root directory to scan
   * @return formatted tree string
   * @throws IOException if an I/O error occurs checking sourceDir
   */
  public static String buildTree(Path sourceDir) throws IOException {
    return buildTree(sourceDir, DEFAULT_EXCLUDED_NAMES);
  }

  /**
   * Generates a visual directory tree with custom exclusion patterns.
   *
   * @param sourceDir the root directory to scan
   * @param excludeNames file/directory names to skip (case-insensitive)
   * @return formatted tree string
   * @throws IOException if an I/O error occurs checking sourceDir
   */
  public static String buildTree(Path sourceDir, Set<String> excludeNames) throws IOException {
    if (sourceDir == null || !Files.isDirectory(sourceDir)) {
      throw new IllegalArgumentException("sourceDir must be an existing directory: " + sourceDir);
    }
    Set<String> excluded = normalize(excludeNames);

    StringBuilder sb = new StringBuilder();

    // Format root node cleanly (handles root drives like '/' or 'C:\')
    Path rootName = sourceDir.getFileName();
    String rootDisplayName = (rootName != null) ? rootName.toString() : sourceDir.toString();
    if (!rootDisplayName.endsWith("/") && !rootDisplayName.endsWith("\\")) {
      rootDisplayName += "/";
    }

    sb.append(rootDisplayName).append(System.lineSeparator());
    appendChildren(sourceDir, "", sb, excluded);
    return sb.toString();
  }

  private static void appendChildren(
      Path dir, String prefix, StringBuilder sb, Set<String> excluded) {
    List<PathEntry> children = listSorted(dir, excluded);

    for (int i = 0; i < children.size(); i++) {
      PathEntry child = children.get(i);
      boolean last = (i == children.size() - 1);

      sb.append(prefix)
          .append(last ? BRANCH_END : BRANCH_TEE)
          .append(child.name())
          .append(child.isDirectory() ? "/" : "")
          .append(System.lineSeparator());

      if (child.isDirectory()) {
        appendChildren(child.path(), prefix + (last ? INDENT_BLANK : INDENT_LINE), sb, excluded);
      }
    }
  }

  private static List<PathEntry> listSorted(Path dir, Set<String> excluded) {
    List<PathEntry> children = new ArrayList<>();

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
      for (Path p : stream) {
        Path fileName = p.getFileName();
        if (fileName == null) {
          continue;
        }

        String name = fileName.toString();
        if (excluded.contains(name.toLowerCase())) {
          continue;
        }

        // Avoid following symlinks to prevent infinite recursive loops
        boolean isDir = Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS);
        children.add(new PathEntry(p, name, isDir));
      }
    } catch (IOException e) {
      // Gracefully handle unreadable directories (e.g. Permission Denied)
      return Collections.emptyList();
    }

    // Sort: Directories first (0), then files (1); both alphabetically (case-insensitive)
    children.sort(
        Comparator.comparing((PathEntry e) -> e.isDirectory() ? 0 : 1)
            .thenComparing(PathEntry::name, String.CASE_INSENSITIVE_ORDER));

    return children;
  }

  private static Set<String> normalize(Set<String> excludeNames) {
    if (excludeNames == null || excludeNames.isEmpty()) {
      return Collections.emptySet();
    }
    Set<String> result = new HashSet<>();
    for (String name : excludeNames) {
      if (name != null && !name.isBlank()) {
        result.add(name.trim().toLowerCase());
      }
    }
    return result;
  }

  /** Internal caching record to avoid redundant Files.isDirectory() OS calls. */
  private record PathEntry(Path path, String name, boolean isDirectory) {}
}
