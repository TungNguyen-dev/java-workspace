package tungnn.tutor.java.tool.crawler.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tungnn.tutor.java.selenium.pool.GenericWebDriverPool;
import tungnn.tutor.java.selenium.pool.WebDriverPool;
import tungnn.tutor.java.selenium.pool.WebDriverPoolConfig;
import tungnn.tutor.java.tool.crawler.core.domain.WebCrawlService;
import tungnn.tutor.java.tool.crawler.core.infra.service.DefaultWebCrawlService;
import tungnn.tutor.java.tool.crawler.core.infra.service.WebCrawlOptions;
import tungnn.tutor.java.tool.crawler.core.infra.service.WebCrawlerRegistry;

@Configuration
public class AppConfig {

    private static final int DRIVER_COUNT = 10;

    @Bean
    public WebDriverPool driverPool() {
        return new GenericWebDriverPool(WebDriverPoolConfig.fromEnv().withMaxTotal(DRIVER_COUNT));
    }

    @Bean
    public WebCrawlService webCrawlService(WebDriverPool driverPool) {
        return new DefaultWebCrawlService(driverPool, WebCrawlerRegistry.defaults(), WebCrawlOptions.defaults());
    }
}
