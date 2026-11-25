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
            int length = PER_UNIT_OVERHEAD_CHARS + (textUnit.content() == null ? 0 : textUnit.content().length());
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
}
