package tungnn.tutor.java.tool.crawler.core.infra.filesystem;

public interface FileNameStrategy<T> {

  String buildFileName(int index, T source);

  default String buildFileName(int index, T source, String extension) {
    String base = buildFileName(index, source);
    return (extension == null || extension.isBlank()) ? base : base + "." + extension;
  }
}
