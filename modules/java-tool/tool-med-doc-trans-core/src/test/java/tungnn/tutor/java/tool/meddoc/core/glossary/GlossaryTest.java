package tungnn.tutor.java.tool.meddoc.core.glossary;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Heading;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownBlock.Paragraph;
import tungnn.tutor.java.infra.document.adapter.markdown.model.MarkdownElement.MarkdownDocument;
import tungnn.tutor.java.tool.doctrans.core.text.LanguageCode;

class GlossaryTest {

    @Test
    void shouldParseTheTableAnswerAndIgnoreHeaderAndSeparator() {
        var glossary = Glossary.parse("| 中文 | Bản dịch |\n| --- | --- |\n| 石膏 | Thạch cao |\n| 知母 | Tri mẫu |\n");

        assertThat(glossary.terms()).containsEntry("石膏", "Thạch cao").containsEntry("知母", "Tri mẫu").hasSize(2);
    }

    @Test
    void shouldOnlyPromptForTermsThatAppearInTheText() {
        var glossary = Glossary.parse("| 石膏 | Thạch cao |\n| 知母 | Tri mẫu |");

        var section = glossary.promptSectionFor("石膏30g");

        assertThat(section).contains("石膏 → Thạch cao").doesNotContain("知母");
        assertThat(glossary.promptSectionFor("甘草")).isEmpty();
    }

    @Test
    void shouldCollectFormulaNamesAndHerbsFromTheSource() {
        var doc = new MarkdownDocument(List.of(
                new Heading(1, "第三章"), new Heading(3, "白虎汤"), new Paragraph("石膏30g 知母9克 粳米三钱")));

        assertThat(GlossaryExtractor.candidates(doc)).containsExactly("白虎汤", "石膏", "知母", "粳米");
    }

    @Test
    void shouldAskTheChatInBatchesAndKeepTheAnswers() {
        var doc = new MarkdownDocument(List.of(new Heading(3, "白虎汤"), new Paragraph("石膏30g")));
        var extractor = new GlossaryExtractor((system, user) -> {
            assertThat(system).contains("Vietnamese");
            return user.lines().map(l -> "| " + l + " | dịch " + l + " |").reduce("", (a, b) -> a + b + "\n");
        });

        var result = extractor.extract(doc, LanguageCode.VI);

        assertThat(result.glossary().terms()).containsEntry("白虎汤", "dịch 白虎汤").containsEntry("石膏", "dịch 石膏");
        assertThat(result.failedBatches()).isZero();
    }

    @Test
    void shouldCountAFailedBatchAndStillReturnAGlossary() {
        var doc = new MarkdownDocument(List.of(new Heading(3, "白虎汤")));
        var extractor = new GlossaryExtractor((system, user) -> {
            throw new IllegalStateException("down");
        });

        var result = extractor.extract(doc, LanguageCode.VI);

        assertThat(result.failedBatches()).isEqualTo(1);
        assertThat(result.glossary().isEmpty()).isTrue();
    }
}
