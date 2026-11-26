package tungnn.tutor.java.spring.tool.doctrans.app.config;

import tungnn.tutor.java.tool.doctrans.core.text.TextTranslationRequest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tungnn.tutor.java.ai.util.LlmClientUtils;
import tungnn.tutor.java.tool.doctrans.core.document.DocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.cache.TranslationCache;
import tungnn.tutor.java.tool.doctrans.core.text.TextTranslator;
import tungnn.tutor.java.tool.doctrans.core.document.impl.ExcelDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.document.impl.MarkdownDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.document.impl.SlideShowDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.document.impl.WordDocumentTranslator;
import tungnn.tutor.java.tool.doctrans.core.text.impl.DummyTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.text.impl.LlmTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.cache.CsvFileTranslationCache;
import tungnn.tutor.java.tool.doctrans.service.DocumentTranslationService;
import tungnn.tutor.java.tool.doctrans.service.DefaultDocumentTranslationService;

@Configuration
public class AppConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    @Autowired
    private DocTransConfig docTransConfig;

    @Autowired
    private TranslatorConfig translatorConfig;

    @Bean
    public TextTranslator textTranslator() {
        if (translatorConfig.isDummy()) {
            return new DummyTextTranslator();
        }
        var properties = translatorConfig.llmClientProperties();
        LOGGER.info("Using LLM text translator: {}", properties);
        var llmClient = LlmClientUtils.create(properties);
        var settings = translatorConfig.llmSettings();
        return LlmTextTranslator.create(llmClient, settings.maxConcurrency());
    }

    private TextTranslationRequest.BatchOverride batchOverride() {
        if (translatorConfig.isDummy()) {
            return TextTranslationRequest.BatchOverride.NONE;
        }
        var settings = translatorConfig.llmSettings();
        return new TextTranslationRequest.BatchOverride(settings.maxBatchUnits(), settings.maxBatchChars());
    }

    @Bean
    public TranslationCache textTranslationCache() {
        return new CsvFileTranslationCache(docTransConfig.memoryDir());
    }

    @Bean
    @Qualifier("wordDocumentTranslator")
    public DocumentTranslator wordDocumentTranslator(
            TextTranslator textTranslator, TranslationCache textTranslationCache) {
        return new WordDocumentTranslator(textTranslator, textTranslationCache, batchOverride());
    }

    @Bean
    @Qualifier("excelDocumentTranslator")
    public DocumentTranslator excelDocumentTranslator(
            TextTranslator textTranslator, TranslationCache textTranslationCache) {
        return new ExcelDocumentTranslator(textTranslator, textTranslationCache, batchOverride());
    }

    @Bean
    @Qualifier("slideShowDocumentTranslator")
    public DocumentTranslator slideShowDocumentTranslator(
            TextTranslator textTranslator, TranslationCache textTranslationCache) {
        return new SlideShowDocumentTranslator(textTranslator, textTranslationCache, batchOverride());
    }

    @Bean
    @Qualifier("markdownDocumentTranslator")
    public DocumentTranslator markdownDocumentTranslator(
            TextTranslator textTranslator, TranslationCache textTranslationCache) {
        return new MarkdownDocumentTranslator(textTranslator, textTranslationCache, batchOverride());
    }

    @Bean
    public DocumentTranslationService documentTranslationService(
            @Qualifier("wordDocumentTranslator") DocumentTranslator wordDocumentTranslator,
            @Qualifier("excelDocumentTranslator") DocumentTranslator excelDocumentTranslator,
            @Qualifier("slideShowDocumentTranslator") DocumentTranslator slideShowDocumentTranslator,
            @Qualifier("markdownDocumentTranslator") DocumentTranslator markdownDocumentTranslator) {
        return new DefaultDocumentTranslationService(
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
