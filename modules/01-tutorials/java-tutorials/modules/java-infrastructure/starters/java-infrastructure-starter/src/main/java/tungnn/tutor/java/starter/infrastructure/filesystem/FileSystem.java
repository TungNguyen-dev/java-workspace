package tungnn.tutor.java.starter.infrastructure.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Storage-agnostic file system abstraction. Implementations may back onto local disk, in-memory, or
 * object storage (S3, Blob, ...). Paths use '/' as separator and are relative to an
 * implementation-defined root.
 */
public interface FileSystem {

  // ---------- Existence & metadata ----------

  boolean exists(String path);

  boolean isDirectory(String path);

  boolean isFile(String path);

  /** Metadata of a file/dir, empty if it does not exist. */
  Optional<FileMetadata> metadata(String path);

  // ---------- Read ----------

  /** Read entire file into memory. Prefer streaming for large files. */
  byte[] readAllBytes(String path) throws IOException;

  String readString(String path) throws IOException;

  /** Open a stream for reading. Caller must close it. */
  InputStream newInputStream(String path) throws IOException;

  // ---------- Write ----------

  void write(String path, byte[] content, WriteOption... options) throws IOException;

  void writeString(String path, String content, WriteOption... options) throws IOException;

  /** Open a stream for writing. Caller must close it. */
  OutputStream newOutputStream(String path, WriteOption... options) throws IOException;

  // ---------- Mutations ----------

  void createDirectories(String path) throws IOException;

  void copy(String source, String target, WriteOption... options) throws IOException;

  void move(String source, String target, WriteOption... options) throws IOException;

  /** Delete a single file/empty dir. @return true if something was deleted. */
  boolean delete(String path) throws IOException;

  /** Delete recursively (dir + contents). @return number of entries removed. */
  long deleteRecursively(String path) throws IOException;

  // ---------- Listing ----------

  /** Direct children (non-recursive). */
  List<String> list(String directory) throws IOException;

  /** Lazily walk the tree rooted at {@code directory}. Caller should close the stream. */
  Stream<String> walk(String directory) throws IOException;

  // ---------- Supporting types ----------

  enum WriteOption {
    CREATE_PARENTS, // auto-create missing parent directories
    OVERWRITE, // replace if target exists
    APPEND // append instead of truncate
  }

  record FileMetadata(String path, boolean directory, long size, Instant lastModified) {}
}
