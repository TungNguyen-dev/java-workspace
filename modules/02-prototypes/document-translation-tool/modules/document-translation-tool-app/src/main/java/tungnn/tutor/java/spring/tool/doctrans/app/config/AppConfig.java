package tungnn.tutor.java.spring.tool.doctrans.app.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tungnn.tutor.java.ai.util.LlmClientUtils;
import tungnn.tutor.java.tool.doctrans.core.domain.document.DocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationMemory;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.ExcelDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.MarkdownDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.SlideShowDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.document.WordDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.DummyTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.LlmTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.infrastructure.translator.text.memory.CsvFileTextTranslationMemory;
import tungnn.tutor.java.tool.doctrans.core.service.DocumentTranslationService;
import tungnn.tutor.java.tool.doctrans.core.service.DocumentTranslationServiceImpl;

@Configuration
public class AppConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);
  @Autowired private DocTransConfig docTransConfig;
  @Autowired private TranslatorConfig translatorConfig;

  /**
   * {@code dummy} for offline runs, otherwise an LLM translator for the configured provider. The
   * LLM client is built manually (no Spring AI auto-configuration), so no ChatModel beans exist.
   */
  @Bean
  public TextTranslator textTranslator() {
    if (translatorConfig.isDummy()) {
      return new DummyTextTranslator();
    }
    var properties = translatorConfig.llmClientProperties();
    LOGGER.info("Using LLM text translator: {}", properties);
    var llmClient = LlmClientUtils.create(properties);
    var maxConcurrency = translatorConfig.llmSettings().maxConcurrency();
    return maxConcurrency == null
        ? new LlmTextTranslator(llmClient)
        : new LlmTextTranslator(llmClient, maxConcurrency);
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
