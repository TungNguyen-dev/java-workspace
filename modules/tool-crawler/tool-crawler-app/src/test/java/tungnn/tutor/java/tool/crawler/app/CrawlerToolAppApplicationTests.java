package tungnn.tutor.java.tool.crawler.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tungnn.tutor.java.selenium.pool.WebDriverPool;

@SpringBootTest
class CrawlerToolAppApplicationTests {

    @MockitoBean
    WebDriverPool driverPool;

    @MockitoBean
    CrawlerRunner crawlerRunner;

    @Test
    void contextLoads() {}
}
