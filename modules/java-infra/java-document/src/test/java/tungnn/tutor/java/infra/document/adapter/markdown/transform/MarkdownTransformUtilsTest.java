package tungnn.tutor.java.infra.document.adapter.markdown.transform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

class MarkdownTransformUtilsTest {

    private static MarkdownDocument doc(tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock... blocks) {
        return new MarkdownDocument(List.of(blocks));
    }

    @Test
    void shouldMergeBlocksIntoOneParagraph() {
        var result = MarkdownTransformUtils.mergeToParagraph(0, 2, " ").apply(doc(new Paragraph("a"), new Paragraph("b")));

        assertThat(result.blocks()).containsExactly(new Paragraph("a b"));
    }

    @Test
    void shouldRejectAnInvalidRange() {
        var action = MarkdownTransformUtils.mergeToParagraph(0, 3, " ");

        assertThatThrownBy(() -> action.apply(doc(new Paragraph("a")))).hasMessageContaining("Invalid range");
    }

    @Test
    void shouldRemoveBlocksMatchingAPredicate() {
        var result = MarkdownTransformUtils.removeIf(b -> b instanceof Paragraph)
                .apply(doc(new Heading(1, "t"), new Paragraph("a")));

        assertThat(result.blocks()).containsExactly(new Heading(1, "t"));
    }

    @Test
    void shouldReplaceTextByRegex() {
        var result = MarkdownTransformUtils.replaceText("a+", "x").apply(doc(new Paragraph("baab")));

        assertThat(result.blocks()).containsExactly(new Paragraph("bxb"));
    }

    @Test
    void shouldTransformOnlyTheContentOfTheNamedSection() {
        var result = MarkdownTransformUtils.transformSection("Body", 1, String::toUpperCase)
                .apply(doc(new Heading(1, "Body"), new Paragraph("text"), new Heading(1, "Other"), new Paragraph("keep")));

        assertThat(result.blocks())
                .containsExactly(new Heading(1, "Body"), new Paragraph("TEXT"), new Heading(1, "Other"), new Paragraph("keep"));
    }

    @Test
    void shouldRunThePipelineStepsInOrder() {
        var result = new MarkdownPipeline()
                .step("a", MarkdownTransformUtils.replaceText("1", "2"))
                .step("b", MarkdownTransformUtils.replaceText("2", "3"))
                .run(doc(new Paragraph("1")));

        assertThat(result.blocks()).containsExactly(new Paragraph("3"));
    }
}
