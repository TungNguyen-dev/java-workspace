package tungnn.tutor.java.starter.application.service;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import tungnn.tutor.java.starter.application.model.CrawlResult;

public interface CrawlService {

  /**
   * Crawl a single URL, writing the result into {@code outputDir}. The file name is derived
   * internally from the page title and unit number.
   *
   * @param url source URL
   * @param unitNo unit index used for file naming/ordering
   * @param outputDir target directory (created if missing)
   * @return result describing the outcome
   */
  CrawlResult crawl(URI url, int unitNo, Path outputDir);

  /**
   * Crawl multiple URLs into {@code outputDir}, one file per URL. The unit number defaults to each
   * URL's position in the list. Partial failures are reported per-URL and do not abort the batch.
   *
   * @param urls source URLs in order
   * @param outputDir target directory (created if missing)
   * @return per-URL results, preserving input order
   */
  List<CrawlResult> crawlAll(List<URI> urls, Path outputDir);
}
