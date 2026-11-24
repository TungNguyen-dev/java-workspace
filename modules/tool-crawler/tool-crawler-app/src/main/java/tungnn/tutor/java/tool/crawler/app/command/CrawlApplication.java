package tungnn.tutor.java.tool.crawler.app.command;

import tungnn.tutor.java.selenium.pool.GenericWebDriverPool;
import tungnn.tutor.java.selenium.pool.WebDriverPoolConfig;
import tungnn.tutor.java.tool.crawler.app.CrawlerRunner;
import tungnn.tutor.java.tool.crawler.app.config.CrawlerConfig;
import tungnn.tutor.java.tool.crawler.app.service.BatchCrawlExecutor;
import tungnn.tutor.java.tool.crawler.app.service.CrawlerServiceImpl;
import tungnn.tutor.java.tool.crawler.app.service.ObsidianServiceImpl;
import tungnn.tutor.java.tool.crawler.core.infra.pool.KeyedPooledWebCrawlerFactory;
import tungnn.tutor.java.tool.crawler.core.infra.pool.KeyedPooledWebCrawlerPool;

public class CrawlApplication {

    private static final int DRIVER_COUNT = 10;

    static void main() {
        var config = new CrawlerConfig(
                new CrawlerConfig.Storage("storage/app/crawler"),
                new CrawlerConfig.Dir("01-todo", "02-result", "03-done"));

        try (var driverPool = new GenericWebDriverPool(WebDriverPoolConfig.fromEnv().withMaxTotal(DRIVER_COUNT));
                var crawlerPool = new KeyedPooledWebCrawlerPool(new KeyedPooledWebCrawlerFactory(driverPool))) {
            var crawlerService = new CrawlerServiceImpl(new BatchCrawlExecutor(crawlerPool));
            new CrawlerRunner(crawlerService, new ObsidianServiceImpl(config), config).run();
        }
    }
}
