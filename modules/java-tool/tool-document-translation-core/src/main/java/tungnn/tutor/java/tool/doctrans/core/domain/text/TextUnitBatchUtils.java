package tungnn.tutor.java.tool.doctrans.core.domain.text;

import java.util.ArrayList;
import java.util.List;

public final class TextUnitBatchUtils {

    private static final int PER_UNIT_OVERHEAD_CHARS = 80;

    private TextUnitBatchUtils() {
        throw new UnsupportedOperationException("Cannot instantiate a utility class");
    }

    public static List<List<TextTranslationRequest.TextUnit>> chunk(
            List<TextTranslationRequest.TextUnit> textUnits, TextTranslationBatchPolicy policy) {
        var chunks = new ArrayList<List<TextTranslationRequest.TextUnit>>();
        var current = new ArrayList<TextTranslationRequest.TextUnit>();
        long currentChars = 0;
        for (var textUnit : textUnits) {
            int length = lengthOf(textUnit);
            boolean full = current.size() >= policy.maxUnits() || currentChars + length > policy.maxChars();
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

    public static List<List<TextTranslationRequest.TextUnit>> chunkSegments(
            List<List<TextTranslationRequest.TextUnit>> segments, TextTranslationBatchPolicy policy) {
        var chunks = new ArrayList<List<TextTranslationRequest.TextUnit>>();
        var current = new ArrayList<TextTranslationRequest.TextUnit>();
        long currentChars = 0;
        for (var segment : segments) {
            if (segment.isEmpty()) {
                continue;
            }
            long segmentChars =
                    segment.stream().mapToLong(TextUnitBatchUtils::lengthOf).sum();
            boolean fitsAlone = segment.size() <= policy.maxUnits() && segmentChars <= policy.maxChars();
            if (!fitsAlone) {
                // A segment larger than the policy cannot stay whole, so it is split in order into its own chunks.
                if (!current.isEmpty()) {
                    chunks.add(current);
                    current = new ArrayList<>();
                    currentChars = 0;
                }
                chunks.addAll(chunk(segment, policy));
                continue;
            }
            boolean fitsCurrent = current.size() + segment.size() <= policy.maxUnits()
                    && currentChars + segmentChars <= policy.maxChars();
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

    private static int lengthOf(TextTranslationRequest.TextUnit textUnit) {
        return PER_UNIT_OVERHEAD_CHARS
                + (textUnit.content() == null ? 0 : textUnit.content().length());
    }
}
