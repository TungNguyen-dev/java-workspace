package tungnn.tutor.java.tool.meddoc.core.check;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;

class DoseCheckUtilsTest {

    private static MarkdownDocument doc(String formula, String composition) {
        return new MarkdownDocument(List.of(new Heading(1, "T"), new Heading(3, formula), new Paragraph(composition)));
    }

    @Test
    void shouldReadChineseAndArabicDoses() {
        assertThat(DoseCheckUtils.doses("石膏30g 知母9克 柴胡半斤 甘草三钱 黄芩二十克 生姜3~9片"))
                .containsExactlyInAnyOrder("30", "9", "0.5", "3", "20", "3", "9");
    }

    @Test
    void shouldAcceptWhenTheDosesAreKept() {
        var result = DoseCheckUtils.compare(doc("白虎汤", "石膏30g 知母9克"), doc("Bạch hổ thang", "Thạch cao 30 g, Tri mẫu 9 g"));

        assertThat(result.mismatches()).isEmpty();
        assertThat(result.sections()).isEqualTo(2);
    }

    @Test
    void shouldAcceptVietnameseHalfAsNumber() {
        var result = DoseCheckUtils.compare(doc("a", "柴胡半斤"), doc("b", "Sài hồ nửa cân"));

        assertThat(result.mismatches()).isEmpty();
    }

    @Test
    void shouldReportAChangedDoseWithItsSection() {
        var result = DoseCheckUtils.compare(doc("白虎汤", "石膏30g 知母9克"), doc("Bạch hổ thang", "Thạch cao 3 g, Tri mẫu 9 g"));

        assertThat(result.mismatches()).hasSize(1);
        var mismatch = result.mismatches().getFirst();
        assertThat(mismatch.section()).isEqualTo("白虎汤");
        assertThat(mismatch.missing()).containsExactly("30");
        assertThat(mismatch.extra()).containsExactly("3");
    }

    @Test
    void shouldReportADroppedDose() {
        var result = DoseCheckUtils.compare(doc("a", "石膏30g 知母9克"), doc("b", "Thạch cao 30 g"));

        assertThat(result.mismatches().getFirst().missing()).containsExactly("9");
    }

    @Test
    void shouldCompareTheWholeDocumentWhenTheSectionCountsDiffer() {
        var source = doc("白虎汤", "石膏30g");
        var translated = new MarkdownDocument(List.of(new Paragraph("Thạch cao 30 g")));

        var result = DoseCheckUtils.compare(source, translated);

        assertThat(result.sections()).isEqualTo(1);
        assertThat(result.mismatches()).isEmpty();
    }
}
