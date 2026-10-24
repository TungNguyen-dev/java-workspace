import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ProjectContextGenerator {

  private static final Path STORAGE_DIR = Path.of("storage", "source-context");
  private static final Set<String> EXCLUDED_DIRS =
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
          "dist",
          "bin",
          "__pycache__",
          ".mvn",
          ".gradle");
  private static final Set<String> TEXT_EXTENSIONS =
      Set.of(
          "java",
          "kt",
          "py",
          "js",
          "ts",
          "tsx",
          "jsx",
          "sql",
          "sh",
          "bat",
          "xml",
          "yml",
          "yaml",
          "json",
          "properties",
          "md",
          "txt",
          "gradle",
          "html",
          "css",
          "scss",
          "conf",
          "ini",
          "env",
          "toml",
          "cfg",
          "dockerfile",
          "gitignore");
  private static final long MAX_INLINE_FILE_BYTES = 64 * 1024L;
  private static final int MAX_TREE_DEPTH = 100;
  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private ProjectContextGenerator() {}

  static void main(String[] args) throws IOException {
    generate(args[0]);
  }

  public static void generate(String projectDirPath) throws IOException {
    if (projectDirPath == null || projectDirPath.isBlank()) {
      throw new IllegalArgumentException("Project directory path must not be empty");
    }

    Path root = Paths.get(projectDirPath).toAbsolutePath().normalize();
    if (!Files.isDirectory(root)) {
      throw new IllegalArgumentException("Not a directory: " + root);
    }

    String projectName = root.getFileName() == null ? "project" : root.getFileName().toString();

    // Đảm bảo thư mục đầu ra tồn tại
    Files.createDirectories(STORAGE_DIR);
    Path output = STORAGE_DIR.resolve(projectName + ".md");

    List<Path> files = collectFiles(root);

    // Sử dụng BufferedWriter để tiết kiệm RAM
    try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
      writeHeader(writer, projectName, root, files);
      writeTree(writer, root);
      writeInventory(writer, root, files);
      writeContents(writer, root, files, output);
    }
  }

  private static void writeHeader(
      BufferedWriter writer, String projectName, Path root, List<Path> files) throws IOException {
    long totalBytes = files.stream().mapToLong(ProjectContextGenerator::sizeOf).sum();
    writer.write("# Project Context: " + projectName + "\n\n");
    writer.write("| Property | Value |\n");
    writer.write("| --- | --- |\n");
    writer.write("| Root path | `" + root + "` |\n");
    writer.write("| Generated at | " + LocalDateTime.now().format(DATE_FORMATTER) + " |\n");
    writer.write("| Files indexed | " + files.size() + " |\n");
    writer.write("| Total size | " + humanSize(totalBytes) + " |\n\n");
  }

  private static void writeTree(BufferedWriter writer, Path root) throws IOException {
    writer.write("## Directory Structure\n\n```text\n" + root.getFileName() + "/\n");
    buildTree(root, "", 0, writer);
    writer.write("```\n\n");
  }

  private static void buildTree(Path dir, String prefix, int depth, BufferedWriter writer)
      throws IOException {
    if (depth >= MAX_TREE_DEPTH) return;

    List<FileEntry> entries = listEntries(dir);
    for (int i = 0; i < entries.size(); i++) {
      FileEntry entry = entries.get(i);
      boolean last = (i == entries.size() - 1);
      writer.write(prefix + (last ? "`-- " : "|-- ") + entry.name());
      if (entry.isDirectory()) {
        writer.write("/\n");
        buildTree(entry.path(), prefix + (last ? "    " : "|   "), depth + 1, writer);
      } else {
        writer.write('\n');
      }
    }
  }

  private static void writeInventory(BufferedWriter writer, Path root, List<Path> files)
      throws IOException {
    Map<String, Integer> byExtension = new LinkedHashMap<>();
    for (Path file : files) {
      byExtension.merge(extensionOf(file), 1, Integer::sum);
    }

    writer.write("## File Inventory\n\n| Extension | Count |\n| --- | --- |\n");
    byExtension.entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .forEach(
            e -> {
              try {
                writer.write(
                    "| "
                        + (e.getKey().isEmpty() ? "(none)" : e.getKey())
                        + " | "
                        + e.getValue()
                        + " |\n");
              } catch (IOException ex) {
                throw new RuntimeException(ex);
              }
            });
    writer.write("\n| File | Size |\n| --- | --- |\n");
    for (Path file : files) {
      writer.write("| `" + relative(root, file) + "` | " + humanSize(sizeOf(file)) + " |\n");
    }
    writer.write("\n");
  }

  private static void writeContents(BufferedWriter writer, Path root, List<Path> files, Path output)
      throws IOException {
    writer.write("## File Contents\n\n");
    for (Path file : files) {
      if (Files.isSameFile(file, output) || !isTextFile(file)) {
        continue;
      }
      String relative = relative(root, file);
      writer.write("### " + relative + "\n\n");
      long size = sizeOf(file);
      if (size > MAX_INLINE_FILE_BYTES) {
        writer.write("_Skipped: file larger than " + humanSize(MAX_INLINE_FILE_BYTES) + "._\n\n");
        continue;
      }

      String content = readSafely(file);
      // An toàn trước Backtick Injection bằng cách dùng 4 backticks nếu phát hiện 3 backticks
      String fence = content.contains("```") ? "````" : "```";

      writer.write(fence + languageOf(file) + "\n");
      writer.write(content);
      writer.write("\n" + fence + "\n\n");
    }
  }

  private static List<Path> collectFiles(Path root) throws IOException {
    List<Path> files = new ArrayList<>();
    collectFiles(root, files, 0);
    return files;
  }

  private static void collectFiles(Path dir, List<Path> files, int depth) throws IOException {
    if (depth >= MAX_TREE_DEPTH) return;
    for (FileEntry entry : listEntries(dir)) {
      if (entry.isDirectory()) {
        collectFiles(entry.path(), files, depth + 1);
      } else {
        files.add(entry.path());
      }
    }
  }

  private static List<FileEntry> listEntries(Path dir) throws IOException {
    List<FileEntry> entries = new ArrayList<>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
      for (Path entry : stream) {
        String name = entry.getFileName().toString();
        boolean isDir = Files.isDirectory(entry);

        if (Files.isSymbolicLink(entry)
            || EXCLUDED_DIRS.contains(name)
            || (name.startsWith(".") && isDir)) {
          continue;
        }
        entries.add(new FileEntry(entry, isDir, name));
      }
    }
    // Sắp xếp không cần gọi lại I/O disk
    entries.sort(
        Comparator.comparing((FileEntry e) -> !e.isDirectory())
            .thenComparing(FileEntry::name, String.CASE_INSENSITIVE_ORDER));
    return entries;
  }

  private static boolean isTextFile(Path file) {
    String name = file.getFileName().toString().toLowerCase();
    return TEXT_EXTENSIONS.contains(extensionOf(file)) || TEXT_EXTENSIONS.contains(name);
  }

  private static String extensionOf(Path file) {
    String name = file.getFileName().toString();
    int dot = name.lastIndexOf('.');
    return (dot < 0 || dot == name.length() - 1) ? "" : name.substring(dot + 1).toLowerCase();
  }

  private static String languageOf(Path file) {
    String ext = extensionOf(file);
    return switch (ext) {
      case "java" -> "java";
      case "py" -> "python";
      case "js", "jsx" -> "javascript";
      case "ts", "tsx" -> "typescript";
      case "sh" -> "bash";
      case "yml", "yaml" -> "yaml";
      case "md" -> "markdown";
      case "" -> "text";
      default -> ext;
    };
  }

  private static String readSafely(Path file) {
    try {
      return Files.readString(file, StandardCharsets.UTF_8).stripTrailing();
    } catch (IOException e) {
      return "_Unreadable file: " + e.getMessage() + "_";
    }
  }

  private static String relative(Path root, Path file) {
    return root.relativize(file).toString().replace('\\', '/');
  }

  private static long sizeOf(Path file) {
    try {
      return Files.size(file);
    } catch (IOException e) {
      return 0L;
    }
  }

  private static String humanSize(long bytes) {
    if (bytes < 1024) return bytes + " B";
    if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
    return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
  }

  private record FileEntry(Path path, boolean isDirectory, String name) {}
}
