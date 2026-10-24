package tungnn.tutor.java.spring.tool.doctrans.app.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationMemory;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.ExcelDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.SlideShowDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.WordDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.GeminiApiTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.memory.CsvFileTextTranslationMemory;

@Configuration
public class AppConfig {

  @Autowired private DocTransConfig docTransConfig;

  @Bean
  public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
    return chatClientBuilder.build();
  }

  @Bean
  public TextTranslator textTranslator(ChatClient chatClient) {
    return new GeminiApiTextTranslator(chatClient);
  }

  @Bean
  public TextTranslationMemory textTranslationMemory() {
    return new CsvFileTextTranslationMemory(docTransConfig.memoryDir());
  }

  @Bean
  public ExcelDocumentTranslator excelDocumentTranslator(
      TextTranslator textTranslator, TextTranslationMemory textTranslationMemory) {
    return new ExcelDocumentTranslator(textTranslator, textTranslationMemory);
  }

  @Bean
  public SlideShowDocumentTranslator slideShowDocumentTranslator(
      TextTranslator textTranslator, TextTranslationMemory textTranslationMemory) {
    return new SlideShowDocumentTranslator(textTranslator, textTranslationMemory);
  }

  @Bean
  public WordDocumentTranslator wordDocumentTranslator(
      TextTranslator textTranslator, TextTranslationMemory textTranslationMemory) {
    return new WordDocumentTranslator(textTranslator, textTranslationMemory);
  }
}
