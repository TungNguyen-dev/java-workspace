package tungnn.tutor.java.tool.hub.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ToolHubApplication {
    static void main(String[] args) {
        SpringApplication.run(ToolHubApplication.class, args);
    }
}
