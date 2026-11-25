package tungnn.tutor.java.tool.doctrans.core.infra.translator.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.ai.config.LlmProvider;
import tungnn.tutor.java.ai.util.LlmOutputUtils;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtils;
import tungnn.tutor.java.jackson.JsonMapperUtils;
import tungnn.tutor.java.tool.doctrans.core.domain.text.AbstractTextTranslator;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchPolicy;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationBatchRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationRequest;
import tungnn.tutor.java.tool.doctrans.core.domain.text.TextTranslationResult;

public class LlmTextTranslator extends AbstractTextTranslator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmTextTranslator.class);
    private static final JsonMapper OBJECT_MAPPER = JsonMapperUtils.createJsonMapper();
    private static final String SYSTEM_PROMPT_RESOURCE = "prompt-trans-dongy-v5.md";
    private static final String SYSTEM_PROMPT;

    static {
        SYSTEM_PROMPT = ResourceUtils.getResourceAsString(SYSTEM_PROMPT_RESOURCE);
    }

    private static final String BATCH_SYSTEM_PROMPT_RESOURCE = "prompt-trans-dongy-batch-v1.md";
    private static final String BATCH_SYSTEM_PROMPT = ResourceUtils.getResourceAsString(BATCH_SYSTEM_PROMPT_RESOURCE);

    private final LlmClient llmClient;
    private final int maxConcurrency;
    private final TextTranslationBatchPolicy batchPolicy;

    public LlmTextTranslator(LlmClient llmClient) {
        this(llmClient, defaultConcurrency(llmClient.provider()));
    }

    public LlmTextTranslator(LlmClient llmClient, int maxConcurrency) {
        this(llmClient, maxConcurrency, defaultBatchPolicy(llmClient.provider()));
    }

    public LlmTextTranslator(LlmClient llmClient, int maxConcurrency, TextTranslationBatchPolicy batchPolicy) {
        this.llmClient = Objects.requireNonNull(llmClient, "llmClient must not be null");
        if (maxConcurrency < 1) {
            throw new IllegalArgumentException("maxConcurrency must be >= 1");
        }
        this.maxConcurrency = maxConcurrency;
        this.batchPolicy = Objects.requireNonNull(batchPolicy, "batchPolicy must not be null");
        LOGGER.info(
                "LlmTextTranslator initialized [provider={}, prompt={}, maxConcurrency={}, batchPolicy={}]",
                llmClient.provider(),
                SYSTEM_PROMPT_RESOURCE,
                maxConcurrency,
                batchPolicy);
    }

    public static LlmTextTranslator create(
            LlmClient llmClient, Integer maxConcurrency, Integer maxBatchUnits, Integer maxBatchChars) {
        var provider = llmClient.provider();
        var defaults = defaultBatchPolicy(provider);
        return new LlmTextTranslator(
                llmClient,
                maxConcurrency != null ? maxConcurrency : defaultConcurrency(provider),
                new TextTranslationBatchPolicy(
                        maxBatchUnits != null ? maxBatchUnits : defaults.maxUnits(),
                        maxBatchChars != null ? maxBatchChars : defaults.maxChars()));
    }

    static TextTranslationBatchPolicy defaultBatchPolicy(LlmProvider provider) {
        return switch (provider) {
            case OLLAMA, OLLAMA_OPENAI, LM_STUDIO, VLLM -> new TextTranslationBatchPolicy(5, 2000);
            default -> new TextTranslationBatchPolicy(20, 3000);
        };
    }

    @Override
    public TextTranslationBatchPolicy batchPolicy() {
        return batchPolicy;
    }

    @Override
    protected List<TextTranslationResult.TextTranslation> doTranslateBatch(TextTranslationBatchRequest request) {
        var sourceUnits = request.textUnits();
        var numberedUnits = new ArrayList<TextTranslationRequest.TextUnit>(sourceUnits.size());
        for (int i = 0; i < sourceUnits.size(); i++) {
            numberedUnits.add(new TextTranslationRequest.TextUnit(String.valueOf(i + 1), sourceUnits.get(i).content()));
        }
        var numberedRequest = new TextTranslationBatchRequest(
                numberedUnits,
                request.targetLanguage(),
                request.translationContext(),
                request.translationGlossary());
        var userPrompt = OBJECT_MAPPER.writeValueAsString(numberedRequest);
        LOGGER.trace("Batch User Prompt (JSON payload): {}", userPrompt);
        var raw = LlmOutputUtils.clean(llmClient.chat(BATCH_SYSTEM_PROMPT, userPrompt));
        BatchItem[] items;
        try {
            items = OBJECT_MAPPER.readValue(raw, BatchItem[].class);
        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    "%s returned an unparsable batch translation".formatted(llmClient.provider()), e);
        }
        var translationByIndex = new HashMap<String, String>();
        for (var item : items) {
            if (item == null || item.id() == null || item.translation() == null) {
                continue;
            }
            if (translationByIndex.putIfAbsent(item.id().trim(), item.translation()) != null) {
                LOGGER.warn("Duplicate batch item id [{}] ignored", item.id());
            }
        }
        var translations = new ArrayList<TextTranslationResult.TextTranslation>(sourceUnits.size());
        for (int i = 0; i < sourceUnits.size(); i++) {
            var translation = translationByIndex.get(String.valueOf(i + 1));
            if (translation != null) {
                var textUnit = sourceUnits.get(i);
                translations.add(new TextTranslationResult.TextTranslation(
                        textUnit.id(), textUnit.content(), translation, request.targetLanguage()));
            }
        }
        return translations;
    }

    record BatchItem(String id, String translation) {}

    static int defaultConcurrency(LlmProvider provider) {
        return switch (provider) {
            case OLLAMA, OLLAMA_OPENAI, LM_STUDIO, VLLM -> 1;
            case GOOGLE_GENAI, HUGGING_FACE, DEEPSEEK -> 10;
            default -> DEFAULT_MAX_CONCURRENCY;
        };
    }

    @Override
    public int maxConcurrency() {
        return maxConcurrency;
    }

    @Override
    protected TextTranslationResult.TextTranslation doTranslate(TextTranslationRequest request) {
        var userPrompt = OBJECT_MAPPER.writeValueAsString(request);
        LOGGER.trace("User Prompt (JSON payload): {}", userPrompt);
        var translated = LlmOutputUtils.clean(llmClient.chat(SYSTEM_PROMPT, userPrompt));
        var textUnit = request.textUnit();
        var source = textUnit.content();
        if (translated.isEmpty() && source != null && !source.isBlank()) {
            throw new IllegalStateException("%s returned an empty translation for text unit [id=%s]"
                    .formatted(llmClient.provider(), textUnit.id()));
        }
        return new TextTranslationResult.TextTranslation(textUnit.id(), source, translated, request.targetLanguage());
    }
}
