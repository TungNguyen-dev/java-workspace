package tungnn.tutor.java.starter.application.service.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import tungnn.tutor.java.starter.application.model.CrawlResult;
import tungnn.tutor.java.starter.application.service.CrawlService;
import tungnn.tutor.java.starter.infrastructure.filesystem.FileNameStrategy;
import tungnn.tutor.java.starter.infrastructure.filesystem.UnitPageFileNameStrategy;
import tungnn.tutor.java.starter.infrastructure.obsidian.ObsidianNote;
import tungnn.tutor.java.starter.infrastructure.webpage.crawler.PageCrawlResult;
import tungnn.tutor.java.starter.infrastructure.webpage.crawler.PageCrawler;
import tungnn.tutor.java.starter.infrastructure.webpage.crawler.PageSource;

public class WebPageCrawlService implements CrawlService {

  private static final String MARKDOWN_EXTENSION = ".md";
  private static final char PREFIX_SEPARATOR = '-';

  private final Map<PageSource, BlockingQueue<PageCrawler>> pools;
  private final FileNameStrategy<PageCrawlResult> fileNameStrategy;
  private final Executor executor;

  public WebPageCrawlService(
      Map<PageSource, BlockingQueue<PageCrawler>> pools,
      FileNameStrategy<PageCrawlResult> fileNameStrategy,
      Executor executor) {
    this.pools = Map.copyOf(Objects.requireNonNull(pools, "pools"));
    this.fileNameStrategy = Objects.requireNonNull(fileNameStrategy, "fileNameStrategy");
    this.executor = Objects.requireNonNull(executor, "executor");
  }

  @Override
  public CrawlResult crawl(URI url, int unitNo, Path outputDir) {
    ensureDirectory(outputDir);
    return crawlSafe(url, unitNo, outputDir);
  }

  @Override
  public List<CrawlResult> crawlAll(List<URI> urls, Path outputDir) {
    ensureDirectory(outputDir);
    Set<String> existingPrefixes = loadExistingPrefixes(outputDir);

    List<CompletableFuture<CrawlResult>> futures =
        IntStream.range(0, urls.size())
            .mapToObj(i -> crawlAsync(urls.get(i), i + 1, outputDir, existingPrefixes))
            .toList();

    return futures.stream().map(CompletableFuture::join).toList();
  }

  private CompletableFuture<CrawlResult> crawlAsync(
      URI url, int unitNo, Path outputDir, Set<String> existingPrefixes) {
    return CompletableFuture.supplyAsync(
        () -> {
          String prefix = String.format(UnitPageFileNameStrategy.UNIT_PREFIX, unitNo);
          return existingPrefixes.contains(prefix)
              ? CrawlResult.skipped(url, "Already exists")
              : crawlSafe(url, unitNo, outputDir);
        },
        executor);
  }

  private Set<String> loadExistingPrefixes(Path outputDir) {
    try (Stream<Path> files = Files.list(outputDir)) {
      return files
          .map(path -> path.getFileName().toString())
          .map(this::extractPrefix)
          .collect(Collectors.toSet());
    } catch (IOException e) {
      throw new UncheckedIOException("Cannot list output dir: " + outputDir, e);
    }
  }

  private String extractPrefix(String fileName) {
    int dash = fileName.indexOf(PREFIX_SEPARATOR);
    return dash > 0 ? fileName.substring(0, dash + 1) : fileName;
  }

  private CrawlResult crawlSafe(URI url, int unitNo, Path outputDir) {
    try {
      PageCrawlResult page = crawlWithPool(url);
      Path outputFile = outputDir.resolve(resolveFileName(unitNo, page));
      ObsidianNote note = new ObsidianNote(page.title(), page.content(), List.of(url.toString()));
      writeContent(outputFile, note.toMarkdown());
      return CrawlResult.success(url, outputFile);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return CrawlResult.failed(url, "Interrupted while borrowing page from pool");
    } catch (Exception e) {
      return CrawlResult.failed(url, e.getMessage());
    }
  }

  private String resolveFileName(int unitNo, PageCrawlResult result) {
    return fileNameStrategy.buildFileName(unitNo, result) + MARKDOWN_EXTENSION;
  }

  private PageCrawlResult crawlWithPool(URI url) throws InterruptedException {
    BlockingQueue<PageCrawler> pool = resolvePool(url);
    PageCrawler crawler = pool.take(); // block until available
    try {
      return crawler.crawl(url.toString());
    } finally {
      returnQuietly(pool, crawler); // always return
    }
  }

  private BlockingQueue<PageCrawler> resolvePool(URI url) {
    PageSource source = PageSource.fromUrl(url);
    BlockingQueue<PageCrawler> pool = pools.get(source);
    if (pool == null) {
      throw new IllegalArgumentException("No pool configured for source: " + source);
    }
    return pool;
  }

  private void returnQuietly(BlockingQueue<PageCrawler> pool, PageCrawler crawler) {
    try {
      pool.put(crawler);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void writeContent(Path file, String content) {
    try {
      Files.writeString(file, content == null ? "" : content, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to write: " + file, e);
    }
  }

  private void ensureDirectory(Path dir) {
    try {
      Files.createDirectories(dir);
    } catch (IOException e) {
      throw new UncheckedIOException("Cannot create output dir: " + dir, e);
    }
  }
}
