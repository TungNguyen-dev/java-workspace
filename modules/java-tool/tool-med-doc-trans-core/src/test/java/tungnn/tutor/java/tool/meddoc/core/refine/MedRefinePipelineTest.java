package tungnn.tutor.java.tool.meddoc.core.refine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
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
                        new Paragraph("1.伤寒少阳证。"));
        assertThat(skipped).isEmpty();
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
}
