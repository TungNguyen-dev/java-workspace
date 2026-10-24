package tungnn.tutor.java.spring.tool.doctrans.app.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationMemory;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.ExcelDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.SlideShowDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.WordDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.GeminiApiTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.huggingface.HuggingFaceChatClientFactory;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.huggingface.HuggingFaceTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.memory.CsvFileTextTranslationMemory;
import tungnn.tutor.java.tool.doctrans.core.service.DocumentTranslationService;
import tungnn.tutor.java.tool.doctrans.core.service.DocumentTranslationServiceImpl;

@Configuration
public class AppConfig {

  @Autowired private DocTransConfig docTransConfig;
  @Autowired private TranslatorConfig translatorConfig;

  /**
   * Gemini is the default provider. The ChatClient is built inline (not exposed as a bean) so it
   * never competes with the Hugging Face ChatClient.
   */
  @Bean
  @ConditionalOnProperty(
      prefix = "doctrans.translator",
      name = "provider",
      havingValue = "gemini",
      matchIfMissing = true)
  public TextTranslator geminiTextTranslator(ChatClient.Builder chatClientBuilder) {
    return new GeminiApiTextTranslator(chatClientBuilder.build());
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "doctrans.translator",
      name = "provider",
      havingValue = "huggingface")
  public TextTranslator huggingFaceTextTranslator() {
    var chatClient =
        HuggingFaceChatClientFactory.create(translatorConfig.huggingface().toProperties());
    return new HuggingFaceTextTranslator(chatClient);
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

  @Bean
  public DocumentTranslationService documentTranslationService(
      WordDocumentTranslator wordDocumentTranslator,
      ExcelDocumentTranslator excelDocumentTranslator,
      SlideShowDocumentTranslator slideShowDocumentTranslator) {
    return new DocumentTranslationServiceImpl(
        wordDocumentTranslator, excelDocumentTranslator, slideShowDocumentTranslator);
  }

  @Bean(destroyMethod = "shutdown")
  public ExecutorService translationJobExecutor() {
    return Executors.newFixedThreadPool(docTransConfig.workerThreads());
  }
}
