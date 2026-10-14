package tungnn.tutor.java.tool.crawler.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CrawlerToolAppApplication {

  public static void main(String[] args) {
    SpringApplication.run(CrawlerToolAppApplication.class, args);
  }
}
