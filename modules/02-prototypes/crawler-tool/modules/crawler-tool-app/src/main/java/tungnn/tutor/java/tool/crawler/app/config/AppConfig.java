package tungnn.tutor.java.tool.crawler.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tungnn.tutor.java.selenium.pool.PooledWebDriverPool;
import tungnn.tutor.java.selenium.pool.WebDriverPool;
import tungnn.tutor.java.tool.crawler.app.service.BatchCrawlExecutor;
import tungnn.tutor.java.tool.crawler.core.infrastructure.pool.KeyedPooledWebCrawlerFactory;
import tungnn.tutor.java.tool.crawler.core.infrastructure.pool.KeyedPooledWebCrawlerPool;
import tungnn.tutor.java.tool.crawler.core.infrastructure.pool.WebCrawlerPool;

@Configuration
public class AppConfig {

  @Bean
  public WebDriverPool driverPool() {
    return new PooledWebDriverPool(10);
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
