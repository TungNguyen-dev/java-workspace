package tungnn.tutor.java.tool.crawler.app.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import tungnn.tutor.java.tool.crawler.app.model.CrawlCourseRequest;
import tungnn.tutor.java.tool.crawler.app.model.CrawlCourseResult;
import tungnn.tutor.java.tool.crawler.core.WebCrawlRequest;
import tungnn.tutor.java.tool.crawler.service.WebCrawlService;

@Service
public class CrawlerServiceImpl implements CrawlerService {

    private final WebCrawlService webCrawlService;

    public CrawlerServiceImpl(WebCrawlService webCrawlService) {
        this.webCrawlService = webCrawlService;
    }

    @Override
    public CrawlCourseResult crawlCourse(CrawlCourseRequest request) {
        if (request == null || request.courses() == null || request.courses().isEmpty()) {
            return new CrawlCourseResult(Set.of());
        }

        var courses = List.copyOf(request.courses());
        var urlsPerCourse = courses.stream().map(course -> readUrlsFromFile(course.path())).toList();

        var flatRequests = urlsPerCourse.stream()
                .flatMap(List::stream)
                .map(WebCrawlRequest.PageUrl::new)
                .toList();
        var flatResults = webCrawlService.crawlAll(flatRequests);

        var resultCourses = new HashSet<CrawlCourseResult.Course>();
        var offset = 0;
        for (var i = 0; i < courses.size(); i++) {
            var size = urlsPerCourse.get(i).size();
            var slice = List.copyOf(flatResults.subList(offset, offset + size));
            resultCourses.add(new CrawlCourseResult.Course(courses.get(i).relativePath(), slice));
            offset += size;
        }
        return new CrawlCourseResult(resultCourses);
    }

    private List<String> readUrlsFromFile(Path path) {
        try (var lines = Files.lines(path)) {
            return lines.map(String::trim)
                    .filter(line -> !line.isBlank() && !line.startsWith("#"))
                    .toList();
        } catch (IOException e) {
            return List.of();
        }
    }
}
