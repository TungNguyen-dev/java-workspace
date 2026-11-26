package tungnn.tutor.java.tool.doctrans.core.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.core.lib.multithread.ConcurrentUtils;

public abstract class AbstractTextTranslator implements TextTranslator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTextTranslator.class);

    private static boolean isUsable(
            TextTranslationRequest.TextUnit textUnit, TextTranslationResult.TranslatedText translatedText) {
        if (translatedText == null) {
            return false;
        }
        var source = textUnit.content();
        return !translatedText.translatedContent().isBlank() || source == null || source.isBlank();
    }

    @Override
    public TextTranslationResult.Single translate(TextTranslationRequest.Single request) {
        try {
            // Log full request record before calling implementation
            LOGGER.debug("Starting translation. Request: [{}]", request);
            var translatedText = doTranslate(request);
            // Log full translation result record on success
            LOGGER.debug("Completed translation successfully. Result: [{}]", translatedText);
            var textUnit = request.textUnit();
            var normalizedText = new TextTranslationResult.TranslatedText(
                    textUnit.id(),
                    textUnit.content(),
                    translatedText.translatedContent(),
                    translatedText.targetLanguage());
            return new TextTranslationResult.Success(normalizedText);
        } catch (Exception e) {
            LOGGER.error("Failed translation. Request: [{}]. Error: {}", request, e.getMessage(), e);
            return new TextTranslationResult.Failure(new TextTranslationResult.Error("", e.getMessage()));
        }
    }

    @Override
    public TextTranslationResult.Batch translateBatch(TextTranslationRequest.Batch request) {
        var textUnits = request.textUnits();
        if (textUnits.size() <= 1) {
            return TextTranslator.super.translateBatch(request);
        }
        Map<String, TextTranslationResult.TranslatedText> translatedTextById = new HashMap<>();
        try {
            LOGGER.debug("Starting batch translation of {} text units", textUnits.size());
            for (var translatedText : doTranslateBatch(request)) {
                if (translatedText != null
                        && translatedText.id() != null
                        && translatedText.translatedContent() != null) {
                    translatedTextById.put(translatedText.id(), translatedText);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(
                    "Batch translation failed, falling back to single translation for {} text units: {}",
                    textUnits.size(),
                    e.getMessage());
        }
        var results = new TextTranslationResult.Single[textUnits.size()];
        var missing = new ArrayList<Integer>();
        for (int i = 0; i < textUnits.size(); i++) {
            var textUnit = textUnits.get(i);
            var translatedText = translatedTextById.get(textUnit.id());
            if (isUsable(textUnit, translatedText)) {
                results[i] = new TextTranslationResult.Success(new TextTranslationResult.TranslatedText(
                        textUnit.id(),
                        textUnit.content(),
                        translatedText.translatedContent(),
                        request.targetLanguage()));
            } else {
                missing.add(i);
            }
        }
        if (!missing.isEmpty()) {
            LOGGER.warn("Missing batch translation for {} text units, translating individually", missing.size());
            var retried = ConcurrentUtils.executeConcurrently(
                    missing, index -> translate(request.single(textUnits.get(index))), Math.max(1, maxConcurrency()));
            for (int i = 0; i < missing.size(); i++) {
                results[missing.get(i)] = retried.get(i);
            }
        }
        return new TextTranslationResult.Batch(List.of(results));
    }

    protected abstract TextTranslationResult.TranslatedText doTranslate(TextTranslationRequest.Single request);

    protected List<TextTranslationResult.TranslatedText> doTranslateBatch(TextTranslationRequest.Batch request) {
        var translations = new ArrayList<TextTranslationResult.TranslatedText>(
                request.textUnits().size());
        for (var textUnit : request.textUnits()) {
            try {
                translations.add(doTranslate(request.single(textUnit)));
            } catch (Exception e) {
                LOGGER.warn("Batch item failed for text unit [id={}]: {}", textUnit.id(), e.getMessage());
            }
        }
        return translations;
    }
}
