package tungnn.tutor.java.core.lib.io.filesystem;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class FileUtil {

  private FileUtil() {}

  // =========================================================
  // File Operations
  // =========================================================

  public static Path createTempFile(String prefix, String suffix) {
    try {
      return Files.createTempFile(prefix, suffix);
    } catch (IOException e) {
      throw new RuntimeException("Failed to create temp file", e);
    }
  }

  public static Path createDirectories(Path path) {
    try {
      return Files.createDirectories(path);
    } catch (IOException e) {
      throw new RuntimeException("Failed to create directories: " + path, e);
    }
  }

  // =========================================================
  // Checking a File or Directory
  // =========================================================

  public static boolean exists(Path path) {
    return Files.exists(path);
  }

  public static boolean isFile(Path path) {
    return Files.isRegularFile(path);
  }

  public static boolean isDirectory(Path path) {
    return Files.isDirectory(path);
  }

  public static boolean isReadable(Path path) {
    return Files.isReadable(path);
  }

  public static boolean isWritable(Path path) {
    return Files.isWritable(path);
  }

  // =========================================================
  // Deleting a File or Directory
  // =========================================================

  public static void delete(Path path) {
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      throw new RuntimeException("Failed to delete: " + path, e);
    }
  }

  public static void deleteRecursively(Path root) {
    if (!Files.exists(root)) return;

    try {
      Files.walkFileTree(
          root,
          new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              Files.delete(file);
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException {
              Files.delete(dir);
              return FileVisitResult.CONTINUE;
            }
          });
    } catch (IOException e) {
      throw new RuntimeException("Failed to delete recursively: " + root, e);
    }
  }

  // =========================================================
  // Copying a File or Directory
  // =========================================================

  public static void copy(Path source, Path target, CopyOption... options) {
    try {
      Files.copy(source, target, options);
    } catch (IOException e) {
      throw new RuntimeException("Failed to copy: " + source + " -> " + target, e);
    }
  }

  public static void copyDirectory(Path source, Path target) {
    try {
      Files.walkFileTree(
          source,
          new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
              Path targetDir = target.resolve(source.relativize(dir));
              Files.createDirectories(targetDir);
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              Files.copy(
                  file,
                  target.resolve(source.relativize(file)),
                  StandardCopyOption.REPLACE_EXISTING);
              return FileVisitResult.CONTINUE;
            }
          });
    } catch (IOException e) {
      throw new RuntimeException("Failed to copy directory", e);
    }
  }

  // =========================================================
  // Moving a File or Directory
  // =========================================================

  public static void move(Path source, Path target, CopyOption... options) {
    try {
      Files.move(source, target, options);
    } catch (IOException e) {
      throw new RuntimeException("Failed to move: " + source + " -> " + target, e);
    }
  }

  // =========================================================
  // Managing Metadata
  // =========================================================

  public static long size(Path path) {
    try {
      return Files.size(path);
    } catch (IOException e) {
      throw new RuntimeException("Failed to get size: " + path, e);
    }
  }

  public static FileTime lastModified(Path path) {
    try {
      return Files.getLastModifiedTime(path);
    } catch (IOException e) {
      throw new RuntimeException("Failed to get last modified time: " + path, e);
    }
  }

  // =========================================================
  // Reading, Writing, and Creating Files
  // =========================================================

  public static String readString(Path path) {
    return readString(path, StandardCharsets.UTF_8);
  }

  public static String readString(Path path, Charset charset) {
    try {
      return Files.readString(path, charset);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read file: " + path, e);
    }
  }

  public static void writeString(Path path, String content) {
    writeString(path, content, StandardCharsets.UTF_8);
  }

  public static void writeString(Path path, String content, Charset charset) {
    try {
      ensureParentExists(path);
      Files.writeString(
          path, content, charset, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Failed to write file: " + path, e);
    }
  }

  public static void appendString(Path path, String content) {
    try {
      ensureParentExists(path);
      Files.writeString(
          path,
          content,
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND);
    } catch (IOException e) {
      throw new RuntimeException("Failed to append file: " + path, e);
    }
  }

  // =========================================================
  // Creating and Reading Directories
  // =========================================================

  public static List<Path> list(Path dir) {
    try (Stream<Path> stream = Files.list(dir)) {
      return stream.toList();
    } catch (IOException e) {
      throw new RuntimeException("Failed to list directory: " + dir, e);
    }
  }

  // =========================================================
  // Walking the File Tree
  // =========================================================

  public static List<Path> walk(Path root) {
    try (Stream<Path> stream = Files.walk(root)) {
      return stream.toList();
    } catch (IOException e) {
      throw new RuntimeException("Failed to walk: " + root, e);
    }
  }

  // =========================================================
  // Finding Files
  // =========================================================

  public static List<Path> findByExtension(Path root, String extension) {
    Objects.requireNonNull(extension);

    List<Path> result = new ArrayList<>();

    try (Stream<Path> stream = Files.walk(root)) {
      stream
          .filter(Files::isRegularFile)
          .filter(p -> p.toString().endsWith(extension))
          .forEach(result::add);
    } catch (IOException e) {
      throw new RuntimeException("Failed to find files", e);
    }

    return result;
  }

  // =========================================================
  // Links (Symbolic)
  // =========================================================

  public static void createSymbolicLink(Path link, Path target) {
    try {
      Files.createSymbolicLink(link, target);
    } catch (IOException e) {
      throw new RuntimeException("Failed to create symlink", e);
    }
  }

  public static boolean isSymbolicLink(Path path) {
    return Files.isSymbolicLink(path);
  }

  // =========================================================
  // Internal Helpers
  // =========================================================

  private static void ensureParentExists(Path path) throws IOException {
    Path parent = path.getParent();
    if (parent != null && !Files.exists(parent)) {
      Files.createDirectories(parent);
    }
  }
}
