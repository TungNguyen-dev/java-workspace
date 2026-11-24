package tungnn.tutor.java.tool.crawler.app.command;

import tungnn.tutor.java.selenium.pool.GenericWebDriverPool;
import tungnn.tutor.java.selenium.pool.WebDriverPoolConfig;
import tungnn.tutor.java.tool.crawler.app.CrawlerRunner;
import tungnn.tutor.java.tool.crawler.app.config.CrawlerConfig;
import tungnn.tutor.java.tool.crawler.app.service.CrawlerServiceImpl;
import tungnn.tutor.java.tool.crawler.app.service.ObsidianServiceImpl;
import tungnn.tutor.java.tool.crawler.core.service.DefaultWebCrawlService;
import tungnn.tutor.java.tool.crawler.core.service.WebCrawlOptions;
import tungnn.tutor.java.tool.crawler.core.infra.registry.WebCrawlerRegistry;

public class CrawlApplication {

    private static final int DRIVER_COUNT = 10;

    static void main() {
        var config = new CrawlerConfig(
                new CrawlerConfig.Storage("storage/app/crawler"),
                new CrawlerConfig.Dir("01-todo", "02-result", "03-done"));

        try (var driverPool = new GenericWebDriverPool(WebDriverPoolConfig.fromEnv().withMaxTotal(DRIVER_COUNT));
                var crawlService = new DefaultWebCrawlService(
                        driverPool, WebCrawlerRegistry.defaults(), WebCrawlOptions.defaults())) {
            var crawlerService = new CrawlerServiceImpl(crawlService);
            new CrawlerRunner(crawlerService, new ObsidianServiceImpl(config), config).run();
        }
    }
}
