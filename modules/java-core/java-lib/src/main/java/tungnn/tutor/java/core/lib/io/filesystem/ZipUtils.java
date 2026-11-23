package tungnn.tutor.java.core.lib.io.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ZipUtils {

  private static final String ZIP_EXTENSION = ".zip";

  private ZipUtils() {}

  public static Path zipDirectory(Path sourceDir) throws IOException {
    return zipDirectory(sourceDir, Collections.emptyList());
  }

  public static Path zipDirectory(Path sourceDir, List<String> excludeGlobs) throws IOException {
    if (!Files.isDirectory(sourceDir)) {
      throw new IllegalArgumentException("Not a directory: " + sourceDir);
    }

    // target zip is created as a sibling of sourceDir, same as `zip -r dir.zip dir/`
    Path targetZipFile =
        sourceDir
            .toAbsolutePath()
            .getParent()
            .resolve(sourceDir.getFileName().toString() + ZIP_EXTENSION);

    List<PathMatcher> excludeMatchers = toMatchers(excludeGlobs);

    try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(targetZipFile))) {
      Files.walkFileTree(
          sourceDir,
          new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
              if (dir.equals(sourceDir)) {
                return FileVisitResult.CONTINUE;
              }
              Path relativePath = sourceDir.relativize(dir);
              // skip whole subtree to avoid walking files we would exclude anyway
              return isExcluded(relativePath, excludeMatchers)
                  ? FileVisitResult.SKIP_SUBTREE
                  : FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              Path relativePath = sourceDir.relativize(file);
              if (!isExcluded(relativePath, excludeMatchers)) {
                zos.putNextEntry(new ZipEntry(toZipEntryName(relativePath)));
                Files.copy(file, zos);
                zos.closeEntry();
              }
              return FileVisitResult.CONTINUE;
            }
          });
    }

    return targetZipFile;
  }

  private static boolean isExcluded(Path relativePath, List<PathMatcher> excludeMatchers) {
    for (PathMatcher matcher : excludeMatchers) {
      if (matcher.matches(relativePath)) {
        return true;
      }
    }
    return false;
  }

  private static List<PathMatcher> toMatchers(List<String> excludeGlobs) {
    if (excludeGlobs == null || excludeGlobs.isEmpty()) {
      return Collections.emptyList();
    }
    FileSystem fileSystem = FileSystems.getDefault();
    List<PathMatcher> matchers = new ArrayList<>(excludeGlobs.size());
    for (String glob : excludeGlobs) {
      matchers.add(fileSystem.getPathMatcher("glob:" + glob));
    }
    return matchers;
  }

  private static String toZipEntryName(Path relativePath) {
    return relativePath.toString().replace(File.separatorChar, '/');
  }
}
