package tungnn.tutor.java.tool.meddoc.core.inspect;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;

class SuspiciousTextUtilsTest {

    @Test
    void shouldFlagDuplicateHerbAndMissingDose() {
        var doc = new MarkdownDocument(
                List.of(new Paragraph("【组成】石膏一两；知母六钱；石膏一两；甘草；粳米六钱。")));

        var suspects = SuspiciousTextUtils.scanSource(doc);

        assertThat(suspects).extracting(Suspect::reason)
                .anyMatch(r -> r.startsWith("Herb listed more than once"))
                .anyMatch(r -> r.startsWith("Herb without a dose"));
    }

    @Test
    void shouldFlagCutOffParagraphAndStrayCharacters() {
        var doc = new MarkdownDocument(List.of(new Paragraph("本方用于气分热盛证，症见身热面赤烦渴引饮脉洪大而"), new Paragraph("ま斗")));

        var suspects = SuspiciousTextUtils.scanSource(doc);

        assertThat(suspects).extracting(Suspect::reason)
                .anyMatch(r -> r.contains("cut off"))
                .anyMatch(r -> r.contains("Stray characters"));
    }

    @Test
    void shouldFlagTheWordTeOnlyForVietnamese() {
        var doc = new MarkdownDocument(List.of(
                new Heading(1, "Thanh nhiệt tễ"), new Paragraph("Bài thuốc thanh nhiệt"), new Paragraph("Phương tễ học")));

        var suspects = SuspiciousTextUtils.scanTranslation(doc, LanguageCode.VI);

        assertThat(suspects).extracting(Suspect::blockIndex).containsExactly(0, 2);
        assertThat(suspects).allMatch(s -> s.reason().contains("tễ"));
        assertThat(SuspiciousTextUtils.scanTranslation(doc, LanguageCode.EN)).isEmpty();
    }

    @Test
    void shouldFlagRemainingChineseOnlyWhenTargetIsNotChinese() {
        var doc = new MarkdownDocument(List.of(new Paragraph("Bạch hổ thang 石膏")));

        assertThat(SuspiciousTextUtils.scanTranslation(doc, LanguageCode.VI)).hasSize(1);
        assertThat(SuspiciousTextUtils.scanTranslation(doc, LanguageCode.ZH)).isEmpty();
    }
}
