package tungnn.tutor.java.tool.doctrans.core.domain.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tungnn.tutor.java.core.lib.multithread.ConcurrentUtils;

public abstract class AbstractTextTranslator implements TextTranslator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTextTranslator.class);

    @Override
    public TextTranslationResult translate(TextTranslationRequest request) {
        try {
            // Log full request record before calling implementation
            LOGGER.debug("Starting translation. Request: [{}]", request);
            var textTranslation = doTranslate(request);
            // Log full translation result record on success
            LOGGER.debug("Completed translation successfully. Result: [{}]", textTranslation);
            var textUnit = request.textUnit();
            var textTranslationNormalized = new TextTranslationResult.TextTranslation(
                    textUnit.id(), textUnit.content(), textTranslation.translation(), textTranslation.languageCode());
            return new TextTranslationResult.Success(textTranslationNormalized);
        } catch (Exception e) {
            LOGGER.error("Failed translation. Request: [{}]. Error: {}", request, e.getMessage(), e);
            return new TextTranslationResult.Failure(new TextTranslationResult.Error("", e.getMessage()));
        }
    }

    @Override
    public List<TextTranslationResult> translateBatch(TextTranslationBatchRequest request) {
        var textUnits = request.textUnits();
        if (textUnits.size() <= 1) {
            return TextTranslator.super.translateBatch(request);
        }
        Map<String, TextTranslationResult.TextTranslation> translationById = new HashMap<>();
        try {
            LOGGER.debug("Starting batch translation of {} text units", textUnits.size());
            for (var translation : doTranslateBatch(request)) {
                if (translation != null && translation.id() != null && translation.translation() != null) {
                    translationById.put(translation.id(), translation);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(
                    "Batch translation failed, falling back to single translation for {} text units: {}",
                    textUnits.size(),
                    e.getMessage());
        }
        var results = new TextTranslationResult[textUnits.size()];
        var missing = new ArrayList<Integer>();
        for (int i = 0; i < textUnits.size(); i++) {
            var textUnit = textUnits.get(i);
            var translation = translationById.get(textUnit.id());
            if (isUsable(textUnit, translation)) {
                results[i] = new TextTranslationResult.Success(new TextTranslationResult.TextTranslation(
                        textUnit.id(), textUnit.content(), translation.translation(), request.targetLanguage()));
            } else {
                missing.add(i);
            }
        }
        if (!missing.isEmpty()) {
            LOGGER.warn("Missing batch translation for {} text units, translating individually", missing.size());
            var retried = ConcurrentUtils.executeConcurrently(
                    missing,
                    index -> translate(new TextTranslationRequest.Standard(
                            textUnits.get(index),
                            request.targetLanguage(),
                            request.translationContext(),
                            request.translationGlossary())),
                    Math.max(1, maxConcurrency()));
            for (int i = 0; i < missing.size(); i++) {
                results[missing.get(i)] = retried.get(i);
            }
        }
        return List.of(results);
    }

    private static boolean isUsable(
            TextTranslationRequest.TextUnit textUnit, TextTranslationResult.TextTranslation translation) {
        if (translation == null) {
            return false;
        }
        var source = textUnit.content();
        return !translation.translation().isBlank() || source == null || source.isBlank();
    }

    protected abstract TextTranslationResult.TextTranslation doTranslate(TextTranslationRequest request);

    protected List<TextTranslationResult.TextTranslation> doTranslateBatch(TextTranslationBatchRequest request) {
        var translations = new ArrayList<TextTranslationResult.TextTranslation>(
                request.textUnits().size());
        for (var textUnit : request.textUnits()) {
            try {
                translations.add(doTranslate(new TextTranslationRequest.Standard(
                        textUnit,
                        request.targetLanguage(),
                        request.translationContext(),
                        request.translationGlossary())));
            } catch (Exception e) {
                LOGGER.warn("Batch item failed for text unit [id={}]: {}", textUnit.id(), e.getMessage());
            }
        }
        return translations;
    }
}
