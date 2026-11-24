package tungnn.tutor.java.tool.crawler.core.infra.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface FileSystem {

  // ---------- Existence & metadata ----------

  boolean exists(String path);

  boolean isDirectory(String path);

  boolean isFile(String path);

  Optional<FileMetadata> metadata(String path);

  // ---------- Read ----------

  byte[] readAllBytes(String path) throws IOException;

  String readString(String path) throws IOException;

  InputStream newInputStream(String path) throws IOException;

  // ---------- Write ----------

  void write(String path, byte[] content, WriteOption... options) throws IOException;

  void writeString(String path, String content, WriteOption... options) throws IOException;

  OutputStream newOutputStream(String path, WriteOption... options) throws IOException;

  // ---------- Mutations ----------

  void createDirectories(String path) throws IOException;

  void copy(String source, String target, WriteOption... options) throws IOException;

  void move(String source, String target, WriteOption... options) throws IOException;

  boolean delete(String path) throws IOException;

  long deleteRecursively(String path) throws IOException;

  // ---------- Listing ----------

  List<String> list(String directory) throws IOException;

  Stream<String> walk(String directory) throws IOException;

  // ---------- Supporting types ----------

  enum WriteOption {
    CREATE_PARENTS, // auto-create missing parent directories
    OVERWRITE, // replace if target exists
    APPEND // append instead of truncate
  }

  record FileMetadata(String path, boolean directory, long size, Instant lastModified) {}
}
