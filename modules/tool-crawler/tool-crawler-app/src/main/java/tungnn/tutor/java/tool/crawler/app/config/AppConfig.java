package tungnn.tutor.java.tool.crawler.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tungnn.tutor.java.selenium.pool.GenericWebDriverPool;
import tungnn.tutor.java.selenium.pool.WebDriverPool;
import tungnn.tutor.java.selenium.pool.WebDriverPoolConfig;
import tungnn.tutor.java.tool.crawler.app.service.BatchCrawlExecutor;
import tungnn.tutor.java.tool.crawler.core.infra.pool.KeyedPooledWebCrawlerFactory;
import tungnn.tutor.java.tool.crawler.core.infra.pool.KeyedPooledWebCrawlerPool;
import tungnn.tutor.java.tool.crawler.core.infra.pool.WebCrawlerPool;

@Configuration
public class AppConfig {

    // Must equal the sum of the per-type maxTotal values in KeyedPooledWebCrawlerPool.
    private static final int DRIVER_COUNT = 10;

    @Bean
    public WebDriverPool driverPool() {
        return new GenericWebDriverPool(WebDriverPoolConfig.fromEnv().withMaxTotal(DRIVER_COUNT));
    }

    @Bean
    public WebCrawlerPool webCrawlerPool(WebDriverPool driverPool) {
        var factory = new KeyedPooledWebCrawlerFactory(driverPool);
        return new KeyedPooledWebCrawlerPool(factory);
    }

    @Bean
    public BatchCrawlExecutor batchCrawlExecutor(WebCrawlerPool webCrawlerPool) {
        return new BatchCrawlExecutor(webCrawlerPool);
    }
}
