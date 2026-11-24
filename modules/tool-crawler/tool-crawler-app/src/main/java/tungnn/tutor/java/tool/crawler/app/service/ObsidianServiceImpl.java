package tungnn.tutor.java.tool.crawler.app.service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tungnn.tutor.java.core.lib.io.filesystem.FileNameUtils;
import tungnn.tutor.java.core.lib.io.filesystem.FileUtils;
import tungnn.tutor.java.tool.crawler.app.config.CrawlerConfig;
import tungnn.tutor.java.tool.crawler.app.model.CrawlCourseResult;
import tungnn.tutor.java.tool.crawler.app.model.ObsidianNote;
import tungnn.tutor.java.tool.crawler.core.domain.CrawlResult;

@Service
public class ObsidianServiceImpl implements ObsidianService {

    private static final Logger log = LoggerFactory.getLogger(ObsidianServiceImpl.class);

    private final CrawlerConfig crawlerConfig;

    public ObsidianServiceImpl(CrawlerConfig crawlerConfig) {
        this.crawlerConfig = crawlerConfig;
    }

    @Override
    public void saveAllCourses(CrawlCourseResult crawlCourseResult) {
        if (crawlCourseResult == null || crawlCourseResult.courses() == null) {
            log.warn("CrawlCourseResult or the course list is empty, skipping note writing.");
            return;
        }

        for (var course : crawlCourseResult.courses()) {
            writeNotesForCourse(course);
        }
    }

    private void writeNotesForCourse(CrawlCourseResult.Course course) {
        var courseDir = crawlerConfig.outputDir().resolve(course.relativePath());
        FileUtils.createDirectories(courseDir);

        var results = course.crawlResults();
        if (results == null || results.isEmpty()) {
            return;
        }

        var totalUnits = results.size();
        var zeroPaddingWidth = String.valueOf(totalUnits).length();
        var formatPattern = "%0" + zeroPaddingWidth + "d - %s.md";

        var unitNumber = 1;
        for (var result : results) {
            if (result instanceof CrawlResult.Success success) {
                var sanitizedTitle = FileNameUtils.sanitize(success.title());
                var fileName = String.format(formatPattern, unitNumber, sanitizedTitle);
                var destinationFile = courseDir.resolve(fileName);

                writeLessonNote(destinationFile, success);
            } else if (result instanceof CrawlResult.Failure(Exception exception)) {
                log.error("Skipping failed lesson due to error", exception);
            }
            unitNumber++;
        }
    }

    private void writeLessonNote(Path filePath, CrawlResult.Success success) {
        var note = new ObsidianNote(
                success.title(), success.content(), success.url(), List.of(), "TungNN", "tungnn.hn@gmail.com");
        var markdownContent = note.toMarkdown();

        FileUtils.writeString(filePath, markdownContent);
    }
}
