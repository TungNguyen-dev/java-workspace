package tungnn.tutor.java.starter.infrastructure.filesystem;

public interface FileNameStrategy<T> {

  String buildFileName(int index, T source);

  /** Build a file name with the given extension appended. */
  default String buildFileName(int index, T source, String extension) {
    String base = buildFileName(index, source);
    return (extension == null || extension.isBlank()) ? base : base + "." + extension;
  }
}
