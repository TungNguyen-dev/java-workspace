package tungnn.tutor.java.tool.crawler.app.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import tungnn.tutor.java.tool.crawler.core.domain.CrawlResult;

public record CrawlCourseResult(Set<Course> courses) {

  public record Course(Path relativePath, List<CrawlResult> crawlResults) {

    public boolean success() {
      return crawlResults.stream().allMatch(r -> r instanceof CrawlResult.Success);
    }
  }
}
