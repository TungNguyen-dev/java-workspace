package tungnn.tutor.java.tool.crawler.core.application.service;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import tungnn.tutor.java.tool.crawler.core.application.model.CrawlResult;

public interface CrawlService {

  CrawlResult crawl(URI url, int unitNo, Path outputDir);

  List<CrawlResult> crawlAll(List<URI> urls, Path outputDir);
}
