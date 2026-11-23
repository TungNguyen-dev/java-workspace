package tungnn.tutor.java.tool.crawler.app.service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tungnn.tutor.java.core.lib.io.filesystem.FileNameUtil;
import tungnn.tutor.java.core.lib.io.filesystem.FileUtil;
import tungnn.tutor.java.tool.crawler.app.config.CrawlerConfig;
import tungnn.tutor.java.tool.crawler.app.model.CrawlCourseResult;
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
      log.warn("CrawlCourseResult hoặc danh sách khóa học rỗng, bỏ qua ghi note.");
      return;
    }

    for (var course : crawlCourseResult.courses()) {
      writeNotesForCourse(course);
    }
  }

  private void writeNotesForCourse(CrawlCourseResult.Course course) {
    var courseDir = crawlerConfig.outputDir().resolve(course.relativePath());
    FileUtil.createDirectories(courseDir);

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
        var sanitizedTitle = FileNameUtil.sanitize(success.title());
        var fileName = String.format(formatPattern, unitNumber, sanitizedTitle);
        var destinationFile = courseDir.resolve(fileName);

        writeLessonNote(destinationFile, success);
      } else if (result instanceof CrawlResult.Failure(Exception exception)) {
        log.error("Bỏ qua bài học thất bại do lỗi", exception);
      }
      unitNumber++;
    }
  }

  private void writeLessonNote(Path filePath, CrawlResult.Success success) {
    var note =
        new ObsidianNote(
            success.title(),
            success.content(),
            success.url(),
            List.of(),
            "TungNN",
            "tungnn.hn@gmail.com");
    var markdownContent = note.toMarkdown();

    FileUtil.writeString(filePath, markdownContent);
  }

  record ObsidianNote(
      String title,
      String content,
      String url,
      List<String> references,
      String author,
      String email) {

    private static final String TEMPLATE =
        """
        ---
        id: %s
        title: "%s"
        author: %s
        email: %s
        date: %s
        tags:
        ---
        # %s

        %s

        ---

        ## Reference

        - [%s](%s)

        """;

    public String toMarkdown() {
      var now = LocalDateTime.now();
      var id = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
      var date = now.toLocalDate().format(DateTimeFormatter.ISO_DATE);

      // Tránh việc title chứa dấu ngoặc kép làm hỏng YAML frontmatter
      var yamlTitle = title.replace("\"", "\\\"");

      var sb = new StringBuilder();
      sb.append(
          String.format(TEMPLATE, id, yamlTitle, author, email, date, title, content, title, url));

      if (references != null && !references.isEmpty()) {
        for (var ref : references) {
          sb.append("- ").append(ref).append(System.lineSeparator());
        }
        sb.append(System.lineSeparator()).append("---").append(System.lineSeparator());
      }

      return sb.toString();
    }
  }
}
