package tungnn.tutor.java.tool.crawler.app.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import tungnn.tutor.java.tool.crawler.app.model.CrawlCourseRequest;
import tungnn.tutor.java.tool.crawler.app.model.CrawlCourseResult;
import tungnn.tutor.java.tool.crawler.core.domain.CrawlRequest;

@Service
public class CrawlerServiceImpl implements CrawlerService {

  private final BatchCrawlExecutor batchCrawlExecutor;

  public CrawlerServiceImpl(BatchCrawlExecutor batchCrawlExecutor) {
    this.batchCrawlExecutor = batchCrawlExecutor;
  }

  @Override
  public CrawlCourseResult crawlCourse(CrawlCourseRequest request) {
    if (request == null || request.courses() == null || request.courses().isEmpty()) {
      return new CrawlCourseResult(Set.of());
    }

    var courseRequestsMap =
        request.courses().stream()
            .collect(
                Collectors.toMap(
                    course -> course,
                    course ->
                        readUrlsFromFile(course.path()).stream()
                            .map(CrawlRequest.WebPage::new)
                            .toList()));

    var flatPageRequests = courseRequestsMap.values().stream().flatMap(List::stream).toList();

    var crawlResultMap = batchCrawlExecutor.submitBatch(flatPageRequests);

    var resultCourses =
        courseRequestsMap.entrySet().stream()
            .map(
                entry -> {
                  var courseReq = entry.getKey();
                  var pageRequests = entry.getValue();

                  var crawlResults = pageRequests.stream().map(crawlResultMap::get).toList();

                  return new CrawlCourseResult.Course(courseReq.relativePath(), crawlResults);
                })
            .collect(Collectors.toSet());

    return new CrawlCourseResult(resultCourses);
  }

  private List<String> readUrlsFromFile(Path path) {
    try (var lines = Files.lines(path)) {
      return lines
          .map(String::trim)
          .filter(line -> !line.isBlank() && !line.startsWith("#"))
          .toList();
    } catch (IOException e) {
      return List.of();
    }
  }
}
