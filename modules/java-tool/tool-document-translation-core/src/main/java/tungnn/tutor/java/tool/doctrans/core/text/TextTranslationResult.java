package tungnn.tutor.java.tool.doctrans.core.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public sealed interface TextTranslationResult permits TextTranslationResult.Single, TextTranslationResult.Batch {

    sealed interface Single extends TextTranslationResult permits Success, Failure {}

    record TranslatedText(String id, String sourceContent, String translatedContent, LanguageCode targetLanguage) {}

    record Success(TranslatedText translatedText) implements Single {}

    record Failure(Error error) implements Single {}

    record Error(String code, String message) {}

    record Batch(List<Single> results) implements TextTranslationResult {

        public Batch {
            Objects.requireNonNull(results, "results must not be null");
            results = List.copyOf(results);
        }

        public static Batch alignedTo(TextTranslationRequest.Batch request, List<? extends Single> raw) {
            var units = request.textUnits();
            var successById = new HashMap<String, Single>();
            for (var result : raw) {
                if (result instanceof Success(TranslatedText translatedText)
                        && translatedText != null
                        && translatedText.id() != null) {
                    successById.putIfAbsent(translatedText.id(), result);
                }
            }
            var sameSize = raw.size() == units.size();
            var aligned = new ArrayList<Single>(units.size());
            for (int i = 0; i < units.size(); i++) {
                var unit = units.get(i);
                Single result = successById.get(unit.id());
                if (result == null && sameSize) {
                    var candidate = raw.get(i);
                    if (candidate != null && !isForeignSuccess(candidate, unit)) {
                        result = candidate;
                    }
                }
                aligned.add(result != null ? result : missing(unit));
            }
            return new Batch(aligned);
        }

        private static boolean isForeignSuccess(Single result, TextTranslationRequest.TextUnit unit) {
            return result instanceof Success(TranslatedText translatedText)
                    && translatedText != null
                    && translatedText.id() != null
                    && !translatedText.id().equals(unit.id());
        }

        private static Failure missing(TextTranslationRequest.TextUnit unit) {
            return new Failure(new Error("", "No translation returned for text unit [id=%s]".formatted(unit.id())));
        }

        public int successCount() {
            return (int) results.stream().filter(Success.class::isInstance).count();
        }

        public int failureCount() {
            return (int) results.stream().filter(Failure.class::isInstance).count();
        }
    }
}
