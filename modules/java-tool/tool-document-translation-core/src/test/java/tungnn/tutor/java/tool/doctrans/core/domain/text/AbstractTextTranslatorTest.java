package tungnn.tutor.java.tool.doctrans.core.domain.text;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class AbstractTextTranslatorTest {

    private static TextTranslationRequest.Batch batch(String... contents) {
        var units = new ArrayList<TextTranslationRequest.TextUnit>();
        for (int i = 0; i < contents.length; i++) {
            units.add(new TextTranslationRequest.TextUnit("id-" + i, contents[i]));
        }
        return new TextTranslationRequest.Batch(units, LanguageCode.VI, null, null);
    }

    private static String translationOf(TextTranslationResult.Single result) {
        return ((TextTranslationResult.Success) result).translatedText().translatedContent();
    }

    private static class StubTranslator extends AbstractTextTranslator {
        final List<String> singleCalls = new java.util.concurrent.CopyOnWriteArrayList<>();
        int batchCalls;
        Function<TextTranslationRequest.Batch, List<TextTranslationResult.TranslatedText>> batchHandler;

        @Override
        protected TextTranslationResult.TranslatedText doTranslate(TextTranslationRequest.Single request) {
            var unit = request.textUnit();
            singleCalls.add(unit.id());
            return new TextTranslationResult.TranslatedText(
                    unit.id(), unit.content(), "single:" + unit.content(), request.targetLanguage());
        }

        @Override
        protected List<TextTranslationResult.TranslatedText> doTranslateBatch(TextTranslationRequest.Batch request) {
            batchCalls++;
            return batchHandler.apply(request);
        }
    }

    @Test
    void shouldReturnBatchTranslationsInRequestOrder() {
        var translator = new StubTranslator();
        translator.batchHandler = request -> request.textUnits().stream()
                .map(unit -> new TextTranslationResult.TranslatedText(
                        unit.id(), "ignored", "batch:" + unit.content(), request.targetLanguage()))
                .toList()
                .reversed();

        var results = translator.translateBatch(batch("a", "b", "c"));

        assertThat(results.results()).hasSize(3);
        assertThat(results.results().stream().map(AbstractTextTranslatorTest::translationOf))
                .containsExactly("batch:a", "batch:b", "batch:c");
        assertThat(((TextTranslationResult.Success) results.results().get(0))
                        .translatedText()
                        .sourceContent())
                .isEqualTo("a");
        assertThat(translator.singleCalls).isEmpty();
    }

    @Test
    void shouldFallBackOnlyForMissingUnits() {
        var translator = new StubTranslator();
        translator.batchHandler = request -> List.of(
                new TextTranslationResult.TranslatedText("id-0", "a", "batch:a", LanguageCode.VI),
                new TextTranslationResult.TranslatedText("id-2", "c", "batch:c", LanguageCode.VI));

        var results = translator.translateBatch(batch("a", "b", "c"));

        assertThat(results.results().stream().map(AbstractTextTranslatorTest::translationOf))
                .containsExactly("batch:a", "single:b", "batch:c");
        assertThat(translator.singleCalls).containsExactly("id-1");
    }

    @Test
    void shouldFallBackForAllUnitsWhenBatchThrows() {
        var translator = new StubTranslator();
        translator.batchHandler = request -> {
            throw new IllegalStateException("boom");
        };

        var results = translator.translateBatch(batch("a", "b"));

        assertThat(results.results().stream().map(AbstractTextTranslatorTest::translationOf))
                .containsExactly("single:a", "single:b");
        assertThat(translator.singleCalls).containsExactlyInAnyOrder("id-0", "id-1");
    }

    @Test
    void shouldFallBackWhenBatchTranslationIsBlankForNonBlankSource() {
        var translator = new StubTranslator();
        translator.batchHandler = request -> List.of(
                new TextTranslationResult.TranslatedText("id-0", "a", "  ", LanguageCode.VI),
                new TextTranslationResult.TranslatedText("id-1", "b", "batch:b", LanguageCode.VI));

        var results = translator.translateBatch(batch("a", "b"));

        assertThat(results.results().stream().map(AbstractTextTranslatorTest::translationOf))
                .containsExactly("single:a", "batch:b");
    }

    @Test
    void shouldUseSingleTranslationForOneUnitBatch() {
        var translator = new StubTranslator();

        var results = translator.translateBatch(batch("a"));

        assertThat(results.results().stream().map(AbstractTextTranslatorTest::translationOf))
                .containsExactly("single:a");
        assertThat(translator.batchCalls).isZero();
    }
}
