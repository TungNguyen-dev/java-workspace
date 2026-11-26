package tungnn.tutor.java.tool.doctrans.core.text;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class TextTranslationBatchPolicyTest {

    private static TextTranslationRequest.TextUnit unit(String id, int length) {
        return new TextTranslationRequest.TextUnit(id, "x".repeat(length));
    }

    private static List<List<String>> ids(List<List<TextTranslationRequest.TextUnit>> chunks) {
        return chunks.stream()
                .map(chunk ->
                        chunk.stream().map(TextTranslationRequest.TextUnit::id).toList())
                .toList();
    }

    @Test
    void shouldReturnNoChunksForEmptyInput() {
        assertThat(new TextTranslationRequest.BatchPolicy(3, 100).chunk(List.of()))
                .isEmpty();
    }

    @Test
    void shouldSplitByMaxUnitsKeepingOrder() {
        var units = List.of(unit("a", 1), unit("b", 1), unit("c", 1), unit("d", 1), unit("e", 1));

        var chunks = new TextTranslationRequest.BatchPolicy(2, 1000).chunk(units);

        assertThat(ids(chunks)).containsExactly(List.of("a", "b"), List.of("c", "d"), List.of("e"));
    }

    @Test
    void shouldSplitByMaxChars() {
        var units = List.of(unit("a", 40), unit("b", 40), unit("c", 40));

        var chunks = new TextTranslationRequest.BatchPolicy(10, 300).chunk(units);

        assertThat(ids(chunks)).containsExactly(List.of("a", "b"), List.of("c"));
    }

    @Test
    void shouldPutOversizedUnitInItsOwnChunk() {
        var units = List.of(unit("a", 10), unit("big", 500), unit("c", 10));

        var chunks = new TextTranslationRequest.BatchPolicy(10, 100).chunk(units);

        assertThat(ids(chunks)).containsExactly(List.of("a"), List.of("big"), List.of("c"));
    }

    @Test
    void shouldPackSmallSegmentsIntoOneChunk() {
        var segments = List.of(List.of(unit("a", 1), unit("b", 1)), List.of(unit("c", 1)));

        var chunks = new TextTranslationRequest.BatchPolicy(10, 1000).chunkSegments(segments);

        assertThat(ids(chunks)).containsExactly(List.of("a", "b", "c"));
    }

    @Test
    void shouldNeverSplitSegmentThatFits() {
        var segments = List.of(List.of(unit("a", 1)), List.of(unit("b", 1), unit("c", 1)), List.of(unit("d", 1)));

        var chunks = new TextTranslationRequest.BatchPolicy(2, 1000).chunkSegments(segments);

        assertThat(ids(chunks)).containsExactly(List.of("a"), List.of("b", "c"), List.of("d"));
    }

    @Test
    void shouldStartNewChunkWhenSegmentExceedsRemainingChars() {
        var segments = List.of(List.of(unit("a", 40), unit("b", 40)), List.of(unit("c", 40)));

        var chunks = new TextTranslationRequest.BatchPolicy(10, 300).chunkSegments(segments);

        assertThat(ids(chunks)).containsExactly(List.of("a", "b"), List.of("c"));
    }

    @Test
    void shouldSplitOversizedSegmentInOrderAndStartNextSegmentFresh() {
        var segments = List.of(
                List.of(unit("a", 1)), List.of(unit("x", 1), unit("y", 1), unit("z", 1)), List.of(unit("d", 1)));

        var chunks = new TextTranslationRequest.BatchPolicy(2, 1000).chunkSegments(segments);

        assertThat(ids(chunks)).containsExactly(List.of("a"), List.of("x", "y"), List.of("z"), List.of("d"));
    }

    @Test
    void shouldIgnoreEmptySegments() {
        var segments = List.of(List.<TextTranslationRequest.TextUnit>of(), List.of(unit("a", 1)));

        var chunks = new TextTranslationRequest.BatchPolicy(5, 1000).chunkSegments(segments);

        assertThat(ids(chunks)).containsExactly(List.of("a"));
    }

    @Test
    void singlePolicyShouldYieldOneUnitPerChunk() {
        var units = List.of(unit("a", 5), unit("b", 5), unit("c", 5));

        var chunks = TextTranslationRequest.BatchPolicy.SINGLE.chunk(units);

        assertThat(ids(chunks)).containsExactly(List.of("a"), List.of("b"), List.of("c"));
    }
}
