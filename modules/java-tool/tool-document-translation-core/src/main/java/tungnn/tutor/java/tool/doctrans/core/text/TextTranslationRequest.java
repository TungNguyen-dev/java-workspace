package tungnn.tutor.java.tool.doctrans.core.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public sealed interface TextTranslationRequest permits TextTranslationRequest.Single, TextTranslationRequest.Batch {

    LanguageCode targetLanguage();

    TranslationContext translationContext();

    TranslationGlossary translationGlossary();

    record TextUnit(String id, String content) {}

    record TranslationContext() {}

    record TranslationGlossary() {}

    record Single(
            TextUnit textUnit,
            LanguageCode targetLanguage,
            TranslationContext translationContext,
            TranslationGlossary translationGlossary)
            implements TextTranslationRequest {}

    record Batch(
            List<TextUnit> textUnits,
            LanguageCode targetLanguage,
            TranslationContext translationContext,
            TranslationGlossary translationGlossary)
            implements TextTranslationRequest {

        public Batch {
            Objects.requireNonNull(textUnits, "textUnits must not be null");
            textUnits = List.copyOf(textUnits);
        }

        public Single single(TextUnit textUnit) {
            return new Single(textUnit, targetLanguage, translationContext, translationGlossary);
        }
    }

    record BatchPolicy(int maxUnits, int maxChars) {

        public static final BatchPolicy SINGLE = new BatchPolicy(1, Integer.MAX_VALUE);

        private static final int PER_UNIT_OVERHEAD_CHARS = 80;

        public BatchPolicy {
            if (maxUnits < 1) {
                throw new IllegalArgumentException("maxUnits must be >= 1");
            }
            if (maxChars < 1) {
                throw new IllegalArgumentException("maxChars must be >= 1");
            }
        }

        private static int lengthOf(TextUnit textUnit) {
            return PER_UNIT_OVERHEAD_CHARS
                    + (textUnit.content() == null ? 0 : textUnit.content().length());
        }

        public List<List<TextUnit>> chunk(List<TextUnit> textUnits) {
            var chunks = new ArrayList<List<TextUnit>>();
            var current = new ArrayList<TextUnit>();
            long currentChars = 0;
            for (var textUnit : textUnits) {
                int length = lengthOf(textUnit);
                boolean full = current.size() >= maxUnits || currentChars + length > maxChars;
                if (!current.isEmpty() && full) {
                    chunks.add(current);
                    current = new ArrayList<>();
                    currentChars = 0;
                }
                current.add(textUnit);
                currentChars += length;
            }
            if (!current.isEmpty()) {
                chunks.add(current);
            }
            return chunks;
        }

        public List<List<TextUnit>> chunkSegments(List<List<TextUnit>> segments) {
            var chunks = new ArrayList<List<TextUnit>>();
            var current = new ArrayList<TextUnit>();
            long currentChars = 0;
            for (var segment : segments) {
                if (segment.isEmpty()) {
                    continue;
                }
                long segmentChars =
                        segment.stream().mapToLong(BatchPolicy::lengthOf).sum();
                boolean fitsAlone = segment.size() <= maxUnits && segmentChars <= maxChars;
                if (!fitsAlone) {
                    // A segment larger than the policy cannot stay whole, so it is split in order into its own chunks.
                    if (!current.isEmpty()) {
                        chunks.add(current);
                        current = new ArrayList<>();
                        currentChars = 0;
                    }
                    chunks.addAll(chunk(segment));
                    continue;
                }
                boolean fitsCurrent =
                        current.size() + segment.size() <= maxUnits && currentChars + segmentChars <= maxChars;
                if (!current.isEmpty() && !fitsCurrent) {
                    chunks.add(current);
                    current = new ArrayList<>();
                    currentChars = 0;
                }
                current.addAll(segment);
                currentChars += segmentChars;
            }
            if (!current.isEmpty()) {
                chunks.add(current);
            }
            return chunks;
        }
    }

    record BatchOverride(Integer maxUnits, Integer maxChars) {

        public static final BatchOverride NONE = new BatchOverride(null, null);

        public BatchPolicy applyTo(BatchPolicy defaults) {
            return new BatchPolicy(
                    maxUnits != null ? maxUnits : defaults.maxUnits(),
                    maxChars != null ? maxChars : defaults.maxChars());
        }
    }
}
