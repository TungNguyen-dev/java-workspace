package tungnn.tutor.java.spring.tool.doctrans.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DocumentTranslationToolApplication {

  public static void main(String[] args) {
    SpringApplication.run(DocumentTranslationToolApplication.class, args);
  }
}
