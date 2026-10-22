package tungnn.tutor.java.spring.tool.doctrans.app.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.ExcelDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.SlideShowDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.WordDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.GeminiApiTextTranslator;

@Configuration
public class AppConfig {

  @Bean
  public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
    return chatClientBuilder.build();
  }

  @Bean
  public TextTranslator textTranslator(ChatClient chatClient) {
    return new GeminiApiTextTranslator(chatClient);
  }

  @Bean
  public ExcelDocumentTranslator excelDocumentTranslator(TextTranslator textTranslator) {
    return new ExcelDocumentTranslator(textTranslator);
  }

  @Bean
  public SlideShowDocumentTranslator slideShowDocumentTranslator(TextTranslator textTranslator) {
    return new SlideShowDocumentTranslator(textTranslator);
  }

  @Bean
  public WordDocumentTranslator wordDocumentTranslator(TextTranslator textTranslator) {
    return new WordDocumentTranslator(textTranslator);
  }
}
