package tungnn.tutor.java.tool.doctrans.core.domain.text;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class TextTranslationRequestTest {

    @Test
    void singleShouldKeepSharedFieldsOfBatch() {
        var context = new TextTranslationRequest.TranslationContext();
        var glossary = new TextTranslationRequest.TranslationGlossary();
        var unit = new TextTranslationRequest.TextUnit("1", "a");
        var batch = new TextTranslationRequest.Batch(List.of(unit), LanguageCode.VI, context, glossary);

        var single = batch.single(unit);

        assertThat(single.textUnit()).isEqualTo(unit);
        assertThat(single.targetLanguage()).isEqualTo(LanguageCode.VI);
        assertThat(single.translationContext()).isSameAs(context);
        assertThat(single.translationGlossary()).isSameAs(glossary);
    }

    @Test
    void batchShouldCopyTextUnitsDefensively() {
        var units = new ArrayList<TextTranslationRequest.TextUnit>();
        units.add(new TextTranslationRequest.TextUnit("1", "a"));
        var batch = new TextTranslationRequest.Batch(units, LanguageCode.VI, null, null);

        units.add(new TextTranslationRequest.TextUnit("2", "b"));

        assertThat(batch.textUnits()).hasSize(1);
    }

    @Test
    void batchShouldRejectNullTextUnits() {
        assertThatThrownBy(() -> new TextTranslationRequest.Batch(null, LanguageCode.VI, null, null))
                .isInstanceOf(NullPointerException.class);
    }
}
