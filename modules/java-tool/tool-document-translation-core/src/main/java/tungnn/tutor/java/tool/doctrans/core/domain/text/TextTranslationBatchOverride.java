package tungnn.tutor.java.tool.doctrans.core.domain.text;

public record TextTranslationBatchOverride(Integer maxUnits, Integer maxChars) {

    public static final TextTranslationBatchOverride NONE = new TextTranslationBatchOverride(null, null);

    public TextTranslationBatchPolicy applyTo(TextTranslationBatchPolicy defaults) {
        return new TextTranslationBatchPolicy(
                maxUnits != null ? maxUnits : defaults.maxUnits(), maxChars != null ? maxChars : defaults.maxChars());
    }
}
