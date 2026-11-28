package tungnn.tutor.java.tool.meddoc.core.refine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.tool.meddoc.core.markdown.MarkdownActions;

class MedRefinePipelineTest {

    @Test
    void shouldStructureChineseSource() {
        var doc = new MarkdownDocument(List.of(
                new Paragraph("第三章"),
                new Paragraph("和解剂"),
                new Paragraph("1.熟悉:和解剂的概念。"),
                new Paragraph("2.掌握:小柴胡汤。"),
                new Paragraph("第一节\t\t和解少阳"),
                new Paragraph("小柴胡汤(伤寒论》"),
                new Paragraph("【组成】柴胡半斤(25g)"),
                new Paragraph("【主治】"),
                new Paragraph("1.伤寒少阳证。")));
        var skipped = new ArrayList<String>();

        var result = MedRefinePipeline.chineseSource(skipped).run(doc);

        assertThat(result.blocks())
                .containsExactly(
                        new Heading(1, "第三章 和解剂"),
                        new Heading(2, "和解少阳"),
                        new Heading(3, "小柴胡汤(伤寒论》"),
                        new Heading(4, "组成"),
                        new Paragraph("柴胡半斤(25g)"),
                        new Heading(4, "主治"),
                        new Paragraph("伤寒少阳证。"));
        assertThat(skipped).isEmpty();
    }

    @Test
    void shouldRemoveTheLiteratureAndCaseSections() {
        var doc = new MarkdownDocument(List.of(
                new Paragraph("第三章"),
                new Paragraph("和解剂"),
                new Paragraph("第一节\t\t和解少阳"),
                new Paragraph("小柴胡汤(伤寒论》"),
                new Paragraph("【组成】柴胡半斤(25g)"),
                new Paragraph("【文献摘要】"),
                new Paragraph("1．原书主治《伤寒论》：“往来寒热。”"),
                new Paragraph("【病案】"),
                new Paragraph("发热：某男，发热三日。"),
                new Paragraph("按语：少阳证。"),
                new Paragraph("大柴胡汤(伤寒论》"),
                new Paragraph("【组成】柴胡半斤(25g)")));
        var skipped = new ArrayList<String>();
        var removedHan = new java.util.concurrent.atomic.AtomicInteger();

        var result = MedRefinePipeline.chineseSource(skipped, removedHan).run(doc);

        assertThat(result.blocks())
                .noneMatch(b -> b instanceof Heading h
                        && (h.text().equals("文献摘要") || h.text().equals("病案")));
        assertThat(result.blocks())
                .contains(new Heading(3, "大柴胡汤(伤寒论》"), new Heading(4, "组成"))
                .doesNotContain(new Paragraph("按语：少阳证。"));
        assertThat(removedHan.get()).isPositive();
    }

    @Test
    void shouldReportStepsThatMatchedNothing() {
        var skipped = new ArrayList<String>();

        var result = MedRefinePipeline.chineseSource(skipped).run(new MarkdownDocument(List.of(new Paragraph("随便一段"))));

        assertThat(result.blocks()).containsExactly(new Paragraph("随便一段"));
        assertThat(skipped).hasSize(4).allMatch(s -> s.endsWith("no matching block found"));
    }

    @Test
    void shouldRelabelHeadingsFromSource() {
        var source = new MarkdownDocument(
                List.<MarkdownBlock>of(new Heading(1, "第三章 和解剂"), new Heading(4, "组成"), new Heading(4, "方解")));
        var translated = new MarkdownDocument(List.<MarkdownBlock>of(
                new Heading(1, "Chương 3"), new Heading(4, "Thành phần bài thuốc"), new Heading(4, "Giải phương")));

        var result = MarkdownActions.relabelHeadings(
                        source, 4, Map.of("组成", "Thành phần", "方解", "Giải nghĩa bài thuốc"))
                .apply(translated);

        assertThat(result.blocks())
                .containsExactly(
                        new Heading(1, "Chương 3"),
                        new Heading(4, "Thành phần"),
                        new Heading(4, "Giải nghĩa bài thuốc"));
    }

    @Test
    void shouldFailRelabelWhenHeadingCountDiffers() {
        var source = new MarkdownDocument(List.<MarkdownBlock>of(new Heading(1, "A"), new Heading(4, "组成")));
        var translated = new MarkdownDocument(List.<MarkdownBlock>of(new Heading(1, "A")));

        assertThatThrownBy(() -> MarkdownActions.relabelHeadings(source, 4, Map.of("组成", "Thành phần"))
                        .apply(translated))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldStructureBoldSourceWithSectionsAndSplitParagraphs() {
        var doc = new MarkdownDocument(List.of(
                new Paragraph("第七章"),
                new Paragraph("**补益剂**"),
                new Paragraph("通过在直接补益的基础上辅之以间接补益，以更好地达到"),
                new Paragraph("补益虚损的目的。"),
                new Paragraph("**第一节**"),
                new Paragraph("补气剂，适用于气虚证，症见倦怠乏力。"),
                new Paragraph("归脾汤《正体类要》"),
                new Paragraph("**【组成】白术** 当归"),
                new Paragraph("**【运用】**"),
                new Paragraph("1、本方是常用方。")));
        var skipped = new ArrayList<String>();

        var result = MedRefinePipeline.chineseSource(skipped).run(doc);

        assertThat(result.blocks())
                .containsExactly(
                        new Heading(1, "第七章 补益剂"),
                        new Paragraph("通过在直接补益的基础上辅之以间接补益，以更好地达到补益虚损的目的。"),
                        new Heading(2, "补气"),
                        new Paragraph("补气剂，适用于气虚证，症见倦怠乏力。"),
                        new Heading(3, "归脾汤《正体类要》"),
                        new Heading(4, "组成"),
                        new Paragraph("白术 当归"),
                        new Heading(4, "运用"),
                        new Paragraph("本方是常用方。"));
        assertThat(skipped).isEmpty();
    }

    @Test
    void shouldRemoveNumberingInTheClinicalUseSection() {
        var doc = new MarkdownDocument(List.of(
                new Paragraph("第三章"),
                new Paragraph("和解剂"),
                new Paragraph("第一节\t\t和解少阳"),
                new Paragraph("小柴胡汤(伤寒论》"),
                new Paragraph("【组成】柴胡半斤(25g)"),
                new Paragraph("【运用】"),
                new Paragraph("1．治疗感冒。"),
                new Paragraph("2．治疗发热。"),
                new Paragraph("【附方】"),
                new Paragraph("1．异功散（《小儿药证直诀》）即四君子汤加陈皮。"),
                new Paragraph("2．六君子汤（《医学正传》）陈皮一钱。")));

        var result = MedRefinePipeline.chineseSource(new ArrayList<>(), new java.util.concurrent.atomic.AtomicInteger())
                .run(doc);

        assertThat(result.blocks())
                .containsSubsequence(
                        new Heading(4, "运用"), new Paragraph("治疗感冒。"), new Paragraph("治疗发热。"))
                .containsSubsequence(
                        new Heading(4, "附方"),
                        new Paragraph("异功散（《小儿药证直诀》）即四君子汤加陈皮。"),
                        new Paragraph("六君子汤（《医学正传》）陈皮一钱。"));
    }
}
