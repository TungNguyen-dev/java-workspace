package tungnn.tutor.java.tool.meddoc.core.markdown;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.ai.client.LlmClient;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

class MarkdownActionsTest {

    private static MarkdownDocument doc(MarkdownBlock... blocks) {
        return new MarkdownDocument(List.of(blocks));
    }

    @Test
    void shouldKeepAFailingSectionAndStillTransformTheOthers() {
        var llm = mock(LlmClient.class);
        when(llm.chat(anyString(), anyString()))
                .thenThrow(new IllegalStateException("timeout"))
                .thenReturn("đã xử lý");
        var errors = new ArrayList<String>();
        var document = doc(
                new Heading(3, "Bài A"),
                new Heading(4, "Thành phần"),
                new Paragraph("a"),
                new Heading(3, "Bài B"),
                new Heading(4, "Thành phần"),
                new Paragraph("b"));

        var result = MarkdownActions.llmSection(
                        "Thành phần", 4, llm, "prompt", (parent, e) -> errors.add(parent + ": " + e.getMessage()))
                .apply(document);

        assertThat(result.blocks())
                .containsExactly(
                        new Heading(3, "Bài A"),
                        new Heading(4, "Thành phần"),
                        new Paragraph("a"),
                        new Heading(3, "Bài B"),
                        new Heading(4, "Thành phần"),
                        new Paragraph("đã xử lý"));
        assertThat(errors).containsExactly("Bài A: timeout");
    }

    @Test
    void shouldFailRelabelWhenHeadingLevelsDiffer() {
        var source = doc(new Heading(1, "A"), new Heading(4, "组成"));
        var translated = doc(new Heading(1, "A"), new Heading(3, "Thành phần bài thuốc"));

        assertThatThrownBy(() -> MarkdownActions.relabelHeadings(source, 4, Map.of("组成", "Thành phần"))
                        .apply(translated))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("level");
    }

    @Test
    void shouldNotMergeAParagraphThatIsNotAChapterName() {
        var result = MarkdownActions.chapterTitle(1)
                .apply(doc(new Paragraph("第三章"), new Paragraph("凡具有和解少阳,调和肝脾的方剂,统称和解剂。")));

        assertThat(result.blocks()).containsExactly(new Heading(1, "第三章"), new Paragraph("凡具有和解少阳,调和肝脾的方剂,统称和解剂。"));
    }

    @Test
    void shouldOnlyRemoveObjectivesNumberedFromOne() {
        var document = doc(
                new Heading(1, "第三章 和解剂"),
                new Paragraph("1、熟悉:概念。"),
                new Paragraph("2、掌握:小柴胡汤。"),
                new Paragraph("3.5g石膏"));

        var result = MarkdownActions.removeNumberedAfterHeading(1).apply(document);

        assertThat(result.blocks()).containsExactly(new Heading(1, "第三章 和解剂"), new Paragraph("3.5g石膏"));
    }

    @Test
    void shouldKeepNumberedContentThatDoesNotStartAtOne() {
        var document = doc(new Heading(1, "第三章 和解剂"), new Paragraph("3、凡具有和解少阳的方剂。"));

        var result = MarkdownActions.removeNumberedAfterHeading(1).apply(document);

        assertThat(result.blocks()).hasSize(2);
    }

    @Test
    void shouldRoundTripListNumbersWithoutTouchingDecimals() {
        var document = doc(new Paragraph("1. 伤寒少阳证。"), new Paragraph("2.往来寒热。"), new Paragraph("3.5g石膏"));

        var marked = MarkdownActions.listNumbersToEnumerationMark().apply(document);
        var restored = MarkdownActions.enumerationMarkToListNumber().apply(marked);

        assertThat(marked.blocks())
                .containsExactly(new Paragraph("1、伤寒少阳证。"), new Paragraph("2、往来寒热。"), new Paragraph("3.5g石膏"));
        assertThat(restored.blocks())
                .containsExactly(new Paragraph("1. 伤寒少阳证。"), new Paragraph("2. 往来寒热。"), new Paragraph("3.5g石膏"));
    }
}
