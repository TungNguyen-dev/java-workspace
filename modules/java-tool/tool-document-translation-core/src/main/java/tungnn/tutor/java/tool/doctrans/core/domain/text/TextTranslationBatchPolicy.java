package tungnn.tutor.java.tool.doctrans.core.domain.text;

public record TextTranslationBatchPolicy(int maxUnits, int maxChars) {
    public static final TextTranslationBatchPolicy SINGLE = new TextTranslationBatchPolicy(1, Integer.MAX_VALUE);

    public TextTranslationBatchPolicy {
        if (maxUnits < 1) {
            throw new IllegalArgumentException("maxUnits must be >= 1");
        }
        if (maxChars < 1) {
            throw new IllegalArgumentException("maxChars must be >= 1");
        }
    }
}
