package tungnn.tutor.java.starter.application.command;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;
import java.util.stream.Stream;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.selenium.driver.ChromeWebDriverFactory;
import tungnn.tutor.java.selenium.driver.options.ChromeOptionsFactory;
import tungnn.tutor.java.starter.application.model.CrawlResult;
import tungnn.tutor.java.starter.application.service.CrawlService;
import tungnn.tutor.java.starter.application.service.impl.WebPageCrawlService;
import tungnn.tutor.java.starter.infrastructure.filesystem.UnitPageFileNameStrategy;
import tungnn.tutor.java.starter.infrastructure.webpage.CourseraPage;
import tungnn.tutor.java.starter.infrastructure.webpage.YoutubePage;
import tungnn.tutor.java.starter.infrastructure.webpage.crawler.PageCrawler;
import tungnn.tutor.java.starter.infrastructure.webpage.crawler.PageSource;

public class CrawlApplication {

  private static final int DRIVER_COUNT = 4;
  private static final String TEXT_EXTENSION = "txt";

  private static final Path STORAGE = Path.of("storage", "app", "crawler");
  private static final Path INPUT_DIR = STORAGE.resolve("01-todo");
  private static final Path OUTPUT_DIR = STORAGE.resolve("02-result");
  private static final Path DONE_DIR = STORAGE.resolve("03-done");

  static void main(String[] args) throws IOException {
    var driverFactory = new ChromeWebDriverFactory(new ChromeOptionsFactory());
    var drivers = driverFactory.getWebDrivers(DRIVER_COUNT);

    Map<PageSource, BlockingQueue<PageCrawler>> pools =
        Map.of(
            PageSource.COURSERA, newPool(drivers, CourseraPage::new),
            PageSource.YOUTUBE, newPool(drivers, YoutubePage::new));

    ExecutorService executor = Executors.newFixedThreadPool(pools.size());
    var crawlService = new WebPageCrawlService(pools, new UnitPageFileNameStrategy(), executor);

    Files.createDirectories(DONE_DIR);

    try (executor;
        Stream<Path> stream = Files.walk(INPUT_DIR)) {
      stream
          .filter(Files::isRegularFile)
          .filter(
              p -> {
                try {
                  return Files.isHidden(p);
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              })
          .forEach(course -> processCourse(crawlService, course));
    } finally {
      drivers.forEach(WebDriver::quit);
    }
  }

  private static BlockingQueue<PageCrawler> newPool(
      List<WebDriver> drivers, Function<WebDriver, PageCrawler> crawlerFactory) {
    return new LinkedBlockingDeque<>(drivers.stream().map(crawlerFactory).toList());
  }

  private static void processCourse(CrawlService crawlService, Path course) {
    try {
      List<URI> uris =
          Files.readAllLines(course).stream()
              .map(String::trim)
              .filter(line -> !line.isEmpty())
              .map(URI::create)
              .toList();

      Path courseOutputDir = OUTPUT_DIR.resolve(course.getFileName().toString());
      List<CrawlResult> results = crawlService.crawlAll(uris, courseOutputDir);
      results.forEach(System.out::println);

      markDone(course);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to process course file: " + course, e);
    }
  }

  private static void markDone(Path course) throws IOException {
    Path target = DONE_DIR.resolve(course.getFileName().toString());
    Files.move(course, target, StandardCopyOption.REPLACE_EXISTING);
  }
}
