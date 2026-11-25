package tungnn.tutor.java.tool.doctrans.core.domain.text;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class AbstractTextTranslatorTest {

    private static TextTranslationBatchRequest batch(String... contents) {
        var units = new ArrayList<TextTranslationRequest.TextUnit>();
        for (int i = 0; i < contents.length; i++) {
            units.add(new TextTranslationRequest.TextUnit("id-" + i, contents[i]));
        }
        return new TextTranslationBatchRequest(units, LanguageCode.VI, null, null);
    }

    private static String translationOf(TextTranslationResult result) {
        return ((TextTranslationResult.Success) result).translation().translation();
    }

    private static class StubTranslator extends AbstractTextTranslator {
        final List<String> singleCalls = new java.util.concurrent.CopyOnWriteArrayList<>();
        int batchCalls;
        Function<TextTranslationBatchRequest, List<TextTranslationResult.TextTranslation>> batchHandler;

        @Override
        protected TextTranslationResult.TextTranslation doTranslate(TextTranslationRequest request) {
            var unit = request.textUnit();
            singleCalls.add(unit.id());
            return new TextTranslationResult.TextTranslation(
                    unit.id(), unit.content(), "single:" + unit.content(), request.targetLanguage());
        }

        @Override
        protected List<TextTranslationResult.TextTranslation> doTranslateBatch(TextTranslationBatchRequest request) {
            batchCalls++;
            return batchHandler.apply(request);
        }
    }

    @Test
    void shouldReturnBatchTranslationsInRequestOrder() {
        var translator = new StubTranslator();
        translator.batchHandler = request -> request.textUnits().stream()
                .map(unit -> new TextTranslationResult.TextTranslation(
                        unit.id(), "ignored", "batch:" + unit.content(), request.targetLanguage()))
                .toList()
                .reversed();

        var results = translator.translateBatch(batch("a", "b", "c"));

        assertThat(results).hasSize(3);
        assertThat(results.stream().map(AbstractTextTranslatorTest::translationOf))
                .containsExactly("batch:a", "batch:b", "batch:c");
        assertThat(((TextTranslationResult.Success) results.get(0))
                        .translation()
                        .original())
                .isEqualTo("a");
        assertThat(translator.singleCalls).isEmpty();
    }

    @Test
    void shouldFallBackOnlyForMissingUnits() {
        var translator = new StubTranslator();
        translator.batchHandler = request -> List.of(
                new TextTranslationResult.TextTranslation("id-0", "a", "batch:a", LanguageCode.VI),
                new TextTranslationResult.TextTranslation("id-2", "c", "batch:c", LanguageCode.VI));

        var results = translator.translateBatch(batch("a", "b", "c"));

        assertThat(results.stream().map(AbstractTextTranslatorTest::translationOf))
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

        assertThat(results.stream().map(AbstractTextTranslatorTest::translationOf))
                .containsExactly("single:a", "single:b");
        assertThat(translator.singleCalls).containsExactlyInAnyOrder("id-0", "id-1");
    }

    @Test
    void shouldFallBackWhenBatchTranslationIsBlankForNonBlankSource() {
        var translator = new StubTranslator();
        translator.batchHandler = request -> List.of(
                new TextTranslationResult.TextTranslation("id-0", "a", "  ", LanguageCode.VI),
                new TextTranslationResult.TextTranslation("id-1", "b", "batch:b", LanguageCode.VI));

        var results = translator.translateBatch(batch("a", "b"));

        assertThat(results.stream().map(AbstractTextTranslatorTest::translationOf))
                .containsExactly("single:a", "batch:b");
    }

    @Test
    void shouldUseSingleTranslationForOneUnitBatch() {
        var translator = new StubTranslator();

        var results = translator.translateBatch(batch("a"));

        assertThat(results.stream().map(AbstractTextTranslatorTest::translationOf))
                .containsExactly("single:a");
        assertThat(translator.batchCalls).isZero();
    }
}
