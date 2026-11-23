package tungnn.tutor.java.tool.crawler.app.model;

import java.nio.file.Path;
import java.util.Set;

public record CrawlCourseRequest(Set<Course> courses) {

  public record Course(Path path, Path relativePath) {}
}
