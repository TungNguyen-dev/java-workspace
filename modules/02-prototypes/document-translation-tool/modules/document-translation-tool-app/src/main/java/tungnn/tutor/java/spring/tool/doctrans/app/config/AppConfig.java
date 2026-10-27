package tungnn.tutor.java.spring.tool.doctrans.app.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationMemory;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.ExcelDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.MarkdownDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.SlideShowDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.WordDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.DummyTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.GeminiApiTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.huggingface.HuggingFaceChatClientFactory;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.huggingface.HuggingFaceTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.memory.CsvFileTextTranslationMemory;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.ollama.OllamaChatClientFactory;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.ollama.OllamaTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.service.DocumentTranslationService;
import tungnn.tutor.java.tool.doctrans.core.service.DocumentTranslationServiceImpl;

@Configuration
public class AppConfig {

  @Autowired private DocTransConfig docTransConfig;
  @Autowired private TranslatorConfig translatorConfig;

  /**
   * Gemini is the default provider. The ChatClient is built inline (not exposed as a bean) so it
   * never competes with other ChatClients.
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
  @ConditionalOnProperty(prefix = "doctrans.translator", name = "provider", havingValue = "ollama")
  public TextTranslator ollamaTextTranslator() {
    var chatClient = OllamaChatClientFactory.create(translatorConfig.ollama().toProperties());
    return new OllamaTextTranslator(chatClient);
  }

  @Bean
  @ConditionalOnProperty(prefix = "doctrans.translator", name = "provider", havingValue = "dummy")
  public TextTranslator dummyTextTranslator() {
    return new DummyTextTranslator();
  }

  @Bean
  public TextTranslationMemory textTranslationMemory() {
    return new CsvFileTextTranslationMemory(docTransConfig.memoryDir());
  }

  @Bean
  @Qualifier("wordDocumentTranslator")
  public DocumentTranslator wordDocumentTranslator(
      TextTranslator textTranslator, TextTranslationMemory textTranslationMemory) {
    return new WordDocumentTranslator(textTranslator, textTranslationMemory);
  }

  @Bean
  @Qualifier("excelDocumentTranslator")
  public DocumentTranslator excelDocumentTranslator(
      TextTranslator textTranslator, TextTranslationMemory textTranslationMemory) {
    return new ExcelDocumentTranslator(textTranslator, textTranslationMemory);
  }

  @Bean
  @Qualifier("slideShowDocumentTranslator")
  public DocumentTranslator slideShowDocumentTranslator(
      TextTranslator textTranslator, TextTranslationMemory textTranslationMemory) {
    return new SlideShowDocumentTranslator(textTranslator, textTranslationMemory);
  }

  @Bean
  @Qualifier("markdownDocumentTranslator")
  public DocumentTranslator markdownDocumentTranslator(
      TextTranslator textTranslator, TextTranslationMemory textTranslationMemory) {
    return new MarkdownDocumentTranslator(textTranslator, textTranslationMemory);
  }

  @Bean
  public DocumentTranslationService documentTranslationService(
      @Qualifier("wordDocumentTranslator") DocumentTranslator wordDocumentTranslator,
      @Qualifier("excelDocumentTranslator") DocumentTranslator excelDocumentTranslator,
      @Qualifier("slideShowDocumentTranslator") DocumentTranslator slideShowDocumentTranslator,
      @Qualifier("markdownDocumentTranslator") DocumentTranslator markdownDocumentTranslator) {

    return new DocumentTranslationServiceImpl(
        (WordDocumentTranslator) wordDocumentTranslator,
        (ExcelDocumentTranslator) excelDocumentTranslator,
        (SlideShowDocumentTranslator) slideShowDocumentTranslator,
        (MarkdownDocumentTranslator) markdownDocumentTranslator);
  }

  @Bean(destroyMethod = "shutdown")
  public ExecutorService translationJobExecutor() {
    return Executors.newFixedThreadPool(docTransConfig.workerThreads());
  }
}
