package tungnn.tutor.java.tool.meddoc.core.markdown;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.meddoc.core.llm.MarkdownChat;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.ListItem;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.OrderedList;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

class MarkdownActionsTest {

    private static MarkdownDocument doc(MarkdownBlock... blocks) {
        return new MarkdownDocument(List.of(blocks));
    }

    @Test
    void shouldRemoveNamedSectionsUpToTheNextHeadingOfTheSameOrHigherLevel() {
        var removed = new java.util.concurrent.atomic.AtomicInteger();
        var source = doc(
                new Heading(3, "甲方"),
                new Heading(4, "组成"),
                new Paragraph("甲药"),
                new Heading(4, "病案"),
                new Paragraph("病例一"),
                new Heading(5, "细节"),
                new Paragraph("病例二"),
                new Heading(3, "乙方"),
                new Heading(5, "病案"),
                new Paragraph("保留"));

        var result = MarkdownActions.removeSections(4, java.util.Set.of("病案"), removed::addAndGet)
                .apply(source);

        assertThat(result.blocks())
                .containsExactly(
                        new Heading(3, "甲方"),
                        new Heading(4, "组成"),
                        new Paragraph("甲药"),
                        new Heading(3, "乙方"),
                        new Heading(5, "病案"),
                        new Paragraph("保留"));
        assertThat(removed.get()).isEqualTo(2 + 3 + 2 + 3);
    }

    @Test
    void shouldLeaveTheDocumentUnchangedWhenNoSectionMatches() {
        var source = doc(new Heading(4, "组成"), new Paragraph("甲药"));

        var result = MarkdownActions.removeSections(4, java.util.Set.of("病案")).apply(source);

        assertThat(result.blocks()).containsExactlyElementsOf(source.blocks());
    }

    @Test
    void shouldKeepAFailingSectionAndStillTransformTheOthers() {
        var llm = mock(MarkdownChat.class);
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

    @Test
    void shouldStripBoldMarkers() {
        var result = MarkdownActions.stripBold()
                .apply(doc(
                        new Heading(3, "**人参蛤蚧散《博济方》**"),
                        new Paragraph("**第三节** **气血双补**"),
                        new Paragraph("**【组成】白术** 当归"),
                        new Paragraph("正文含有 **强调** 的词")));

        assertThat(result.blocks())
                .containsExactly(
                        new Heading(3, "人参蛤蚧散《博济方》"),
                        new Paragraph("第三节 气血双补"),
                        new Paragraph("【组成】白术 当归"),
                        new Paragraph("正文含有 **强调** 的词"));
    }

    @Test
    void shouldCompleteSectionTitlesFromTheNextParagraph() {
        var prefix = Pattern.compile("^第[一二三四五六七八九十百\\d]+[节節]\\s*");
        Function<String, String> lead = next -> next.startsWith("补血剂") ? "补血" : null;

        var result = MarkdownActions.sectionHeading(prefix, 2, lead)
                .apply(doc(
                        new Paragraph("第一节"),
                        new Paragraph("补血剂，适用于血虚证"),
                        new Paragraph("第二节 补"),
                        new Paragraph("补血剂，适用于血虚证"),
                        new Paragraph("第四节 补 阴"),
                        new Paragraph("补阴剂，适用于阴虚证")));

        assertThat(result.blocks())
                .containsExactly(
                        new Heading(2, "补血"),
                        new Paragraph("补血剂，适用于血虚证"),
                        new Heading(2, "补血"),
                        new Paragraph("补血剂，适用于血虚证"),
                        new Heading(2, "补阴"),
                        new Paragraph("补阴剂，适用于阴虚证"));
    }

    @Test
    void shouldMergeParagraphsSplitAcrossPages() {
        var result = MarkdownActions.mergeBrokenParagraphs()
                .apply(doc(
                        new Paragraph("通过在直接补益的基础上辅之以间接补益，以更好地达到"),
                        new Paragraph(""),
                        new Paragraph("补益虚损的目的。"),
                        new Paragraph("腹痛喜得温按者，加干姜、肉桂等以温中祛寒止痛；若咯痰色白量多"),
                        new Paragraph("者，加半夏、陈皮以燥湿化痰。")));

        assertThat(result.blocks())
                .containsExactly(
                        new Paragraph("通过在直接补益的基础上辅之以间接补益，以更好地达到补益虚损的目的。"),
                        new Paragraph("腹痛喜得温按者，加干姜、肉桂等以温中祛寒止痛；若咯痰色白量多者，加半夏、陈皮以燥湿化痰。"));
    }

    @Test
    void shouldNotMergeParagraphsThatAreSeparate() {
        var blocks = doc(
                new Paragraph("通过在直接补益的基础上辅之以间接补益，以更好地达到目的。"),
                new Paragraph("补益虚损的目的。"),
                new Paragraph("通过在直接补益的基础上辅之以间接补益，以更好地达到"),
                new Paragraph("2、若兼中焦虚寒者，加干姜"),
                new Paragraph("通过在直接补益的基础上辅之以间接补益，以更好地达到"),
                new Paragraph("【用法】水煎服"),
                new Paragraph("短句"),
                new Paragraph("接续的内容"));

        assertThat(MarkdownActions.mergeBrokenParagraphs().apply(blocks).blocks())
                .isEqualTo(blocks.blocks());
    }

    @Test
    void shouldTurnBookTitlesIntoParentheses() {
        var result = MarkdownActions.bookTitleToParen(3)
                .apply(doc(
                        new Heading(3, "Tứ quân tử thang《Thánh tế tổng lục》"),
                        new Heading(3, "Ngọc bình phong tán《Cứu nguyên phương》, lục từ《Y phương loại tụ》"),
                        new Heading(3, "Bát trân thang (Bát trân tán)《Thụy trúc đường》"),
                        new Heading(3, "Sinh mạch tán")));

        assertThat(result.blocks())
                .containsExactly(
                        new Heading(3, "Tứ quân tử thang (Thánh tế tổng lục)"),
                        new Heading(3, "Ngọc bình phong tán (Cứu nguyên phương, lục từ Y phương loại tụ)"),
                        new Heading(3, "Bát trân thang (Bát trân tán) (Thụy trúc đường)"),
                        new Heading(3, "Sinh mạch tán"));
    }

    @Test
    void shouldTurnOrderedListsIntoPlainParagraphs() {
        var list = new OrderedList(List.of(
                new ListItem(List.of(new Paragraph("治疗感冒。"))), new ListItem(List.of(new Paragraph("治疗发热。")))));

        var result = MarkdownActions.unnumberLists().apply(doc(new Heading(4, "方解"), list));

        assertThat(result.blocks())
                .containsExactly(new Heading(4, "方解"), new Paragraph("治疗感冒。"), new Paragraph("治疗发热。"));
    }

    @Test
    void shouldStripManualNumbersFromRunsStartingAtOne() {
        var result = MarkdownActions.unnumberLists()
                .apply(doc(
                        new Heading(4, "附方"),
                        new Paragraph("1．异功散"),
                        new Paragraph(""),
                        new Paragraph("2、六君子汤"),
                        new Paragraph("（3）保元汤"),
                        new Paragraph("异功散、六君子汤均由四君子汤加味而成。")));

        assertThat(result.blocks())
                .containsExactly(
                        new Heading(4, "附方"),
                        new Paragraph("异功散"),
                        new Paragraph(""),
                        new Paragraph("六君子汤"),
                        new Paragraph("保元汤"),
                        new Paragraph("异功散、六君子汤均由四君子汤加味而成。"));
    }

    @Test
    void shouldKeepNumbersThatDoNotStartARunAtOne() {
        var document = doc(new Paragraph("3、凡具有和解少阳的方剂。"), new Paragraph("3.5g石膏"));

        assertThat(MarkdownActions.unnumberLists().apply(document).blocks()).isEqualTo(document.blocks());
    }
}
