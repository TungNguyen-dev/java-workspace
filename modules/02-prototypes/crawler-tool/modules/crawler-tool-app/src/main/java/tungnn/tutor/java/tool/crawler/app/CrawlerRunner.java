package tungnn.tutor.java.tool.crawler.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tungnn.tutor.java.tool.crawler.app.config.CrawlerConfig;
import tungnn.tutor.java.tool.crawler.app.model.CrawlCourseRequest;
import tungnn.tutor.java.tool.crawler.app.model.CrawlCourseResult;
import tungnn.tutor.java.tool.crawler.app.service.CrawlerService;
import tungnn.tutor.java.tool.crawler.app.service.ObsidianService;

@Component
public class CrawlerRunner implements CommandLineRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerRunner.class);

  private final CrawlerService crawlService;
  private final ObsidianService obsidianService;
  private final CrawlerConfig appConfig;

  public CrawlerRunner(
      CrawlerService crawlService, ObsidianService obsidianService, CrawlerConfig appConfig) {
    this.crawlService = crawlService;
    this.obsidianService = obsidianService;
    this.appConfig = appConfig;
  }

  @Override
  public void run(String @NonNull ... args) {
    LOGGER.info("Loading application configuration...");
    ensureDirectoriesExist(appConfig);

    // 1. Scan input files (.txt) containing URL lists
    var inputFiles = scanInputFiles(appConfig.inputDir());
    if (inputFiles.isEmpty()) {
      LOGGER.info("No URL list files found in input directory: {}", appConfig.inputDir());
      return;
    }

    LOGGER.info("Found {} course list file(s) to crawl.", inputFiles.size());

    try {
      // 2. Prepare request data
      var courses =
          inputFiles.stream()
              .map(
                  filePath -> {
                    var relativePath = appConfig.inputDir().relativize(filePath);
                    return new CrawlCourseRequest.Course(filePath, relativePath);
                  })
              .collect(Collectors.toSet());

      var request = new CrawlCourseRequest(courses);

      // 3. Execute crawling process via CrawlService
      LOGGER.info("Starting data crawling process...");
      var result = crawlService.crawlCourse(request);

      // 4. Save Markdown notes to Obsidian vault
      LOGGER.info("Saving markdown notes to Obsidian vault...");
      obsidianService.saveAllCourses(result);

      // 5. Move successfully processed input files to the 'done' directory
      moveSuccessfulCourseFilesToDone(result, appConfig.inputDir(), appConfig.doneDir());

      LOGGER.info("Crawling and storage process completed successfully!");

    } catch (Exception e) {
      LOGGER.error("Fatal error during CrawlerRunner execution: {}", e.getMessage(), e);
    }
  }

  /** Ensures that input, output, and done directories exist. */
  private void ensureDirectoriesExist(CrawlerConfig config) {
    try {
      Files.createDirectories(config.inputDir());
      Files.createDirectories(config.outputDir());
      Files.createDirectories(config.doneDir());
    } catch (IOException e) {
      throw new RuntimeException("Failed to create storage directories", e);
    }
  }

  /** Scans all non-hidden regular files in the input directory. */
  private List<Path> scanInputFiles(Path inputDir) {
    try (var stream = Files.walk(inputDir)) {
      return stream
          .filter(Files::isRegularFile)
          .filter(
              p -> {
                try {
                  return !Files.isHidden(p);
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              })
          .toList();
    } catch (IOException e) {
      LOGGER.error("Error scanning input directory: {}", e.getMessage(), e);
      return List.of();
    }
  }

  /** Filters successful courses and moves corresponding input files to the done directory. */
  private void moveSuccessfulCourseFilesToDone(
      CrawlCourseResult result, Path inputDir, Path doneDir) {
    if (result == null || result.courses() == null) {
      return;
    }

    for (var course : result.courses()) {
      if (!course.success()) {
        LOGGER.warn(
            "Course failed or contains failed lessons, retaining input file: {}",
            course.relativePath());
        continue;
      }

      var relativePath = course.relativePath();
      var sourcePath = inputDir.resolve(relativePath);
      var targetPath = doneDir.resolve(relativePath);

      if (!Files.exists(sourcePath)) {
        LOGGER.error("Source file not found for moving: {}", sourcePath);
        continue;
      }

      try {
        if (targetPath.getParent() != null) {
          Files.createDirectories(targetPath.getParent());
        }
        Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        LOGGER.info("Moved file successfully to done directory: {}", targetPath);
      } catch (IOException e) {
        LOGGER.error("Failed to move file {}: {}", sourcePath, e.getMessage(), e);
      }
    }
  }
}
