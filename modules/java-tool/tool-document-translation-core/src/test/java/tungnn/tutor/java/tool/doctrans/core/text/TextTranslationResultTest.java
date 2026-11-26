package tungnn.tutor.java.tool.doctrans.core.text;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class TextTranslationResultTest {

    private static TextTranslationRequest.Batch request(String... ids) {
        var units = new ArrayList<TextTranslationRequest.TextUnit>();
        for (var id : ids) {
            units.add(new TextTranslationRequest.TextUnit(id, "src-" + id));
        }
        return new TextTranslationRequest.Batch(units, LanguageCode.VI, null, null);
    }

    private static TextTranslationResult.Success success(String id, String translation) {
        return new TextTranslationResult.Success(
                new TextTranslationResult.TranslatedText(id, "src-" + id, translation, LanguageCode.VI));
    }

    private static TextTranslationResult.Failure failure(String message) {
        return new TextTranslationResult.Failure(new TextTranslationResult.Error("", message));
    }

    private static String translationOf(TextTranslationResult.Single result) {
        return ((TextTranslationResult.Success) result).translatedText().translatedContent();
    }

    @Test
    void shouldMatchResultsReturnedOutOfOrderById() {
        var aligned =
                TextTranslationResult.Batch.alignedTo(request("a", "b"), List.of(success("b", "B"), success("a", "A")));

        assertThat(aligned.results().stream().map(TextTranslationResultTest::translationOf))
                .containsExactly("A", "B");
    }

    @Test
    void shouldKeepFailureAtSamePosition() {
        var aligned =
                TextTranslationResult.Batch.alignedTo(request("a", "b"), List.of(success("a", "A"), failure("x")));

        assertThat(translationOf(aligned.results().get(0))).isEqualTo("A");
        assertThat(aligned.results().get(1)).isInstanceOf(TextTranslationResult.Failure.class);
        assertThat(aligned.successCount()).isEqualTo(1);
        assertThat(aligned.failureCount()).isEqualTo(1);
    }

    @Test
    void shouldNotReuseSuccessWithForeignIdPositionally() {
        var aligned =
                TextTranslationResult.Batch.alignedTo(request("a", "b"), List.of(success("zzz", "Z"), failure("x")));

        assertThat(aligned.results()).allMatch(TextTranslationResult.Failure.class::isInstance);
    }

    @Test
    void shouldFillMissingUnitsWithFailureWhenSizeDiffers() {
        var aligned = TextTranslationResult.Batch.alignedTo(request("a", "b", "c"), List.of(success("b", "B")));

        assertThat(aligned.results()).hasSize(3);
        assertThat(aligned.results().get(0)).isInstanceOf(TextTranslationResult.Failure.class);
        assertThat(translationOf(aligned.results().get(1))).isEqualTo("B");
        assertThat(aligned.results().get(2)).isInstanceOf(TextTranslationResult.Failure.class);
    }

    @Test
    void shouldTreatNullElementAsMissing() {
        var aligned = TextTranslationResult.Batch.alignedTo(
                request("a", "b"), Arrays.<TextTranslationResult.Single>asList(null, success("b", "B")));

        assertThat(aligned.results().get(0)).isInstanceOf(TextTranslationResult.Failure.class);
        assertThat(translationOf(aligned.results().get(1))).isEqualTo("B");
    }
}
