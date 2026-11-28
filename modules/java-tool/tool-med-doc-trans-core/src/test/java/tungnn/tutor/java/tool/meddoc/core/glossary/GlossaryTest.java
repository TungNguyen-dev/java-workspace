package tungnn.tutor.java.tool.meddoc.core.glossary;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class GlossaryTest {

    @Test
    void shouldOnlyPromptForTermsThatAppearInTheText() {
        var glossary = new Glossary(List.of(new GlossaryEntry("石膏", "Thạch cao"), new GlossaryEntry("知母", "Tri mẫu")));

        var section = glossary.promptSectionFor("石膏30g");

        assertThat(section).contains("石膏 → Thạch cao").doesNotContain("知母");
        assertThat(glossary.promptSectionFor("甘草")).isEmpty();
    }

    @Test
    void shouldPreferTheLongestTermAndNotCountItsPartsAgain() {
        var glossary = new Glossary(List.of(new GlossaryEntry("石膏", "Thạch cao"), new GlossaryEntry("生石膏", "Sinh thạch cao")));

        assertThat(glossary.matching("生石膏30g")).extracting(GlossaryEntry::source).containsExactly("生石膏");
        assertThat(glossary.matching("生石膏30g 石膏9g"))
                .extracting(GlossaryEntry::source)
                .containsExactly("生石膏", "石膏");
    }

    @Test
    void shouldListEveryContextOfATermAndAskToPickOne() {
        var glossary = new Glossary(List.of(
                new GlossaryEntry("生地", "Sinh địa", "vị thuốc"), new GlossaryEntry("生地", "Đất sống", "nghĩa đen")));

        var section = glossary.promptSectionFor("生地黄");

        assertThat(section)
                .contains("生地 → Sinh địa (ngữ cảnh: vị thuốc)")
                .contains("生地 → Đất sống (ngữ cảnh: nghĩa đen)")
                .contains("chọn cách hợp với ngữ cảnh");
    }

    @Test
    void shouldNotPrintAContextWhenThereIsNone() {
        var glossary = new Glossary(List.of(new GlossaryEntry("石膏", "Thạch cao")));

        assertThat(glossary.promptSectionFor("石膏")).doesNotContain("ngữ cảnh");
    }

    @Test
    void shouldRestrictToTheEntriesInTheDocumentAndRenderThem() {
        var glossary = new Glossary(List.of(new GlossaryEntry("石膏", "Thạch cao", "vị thuốc"), new GlossaryEntry("知母", "Tri mẫu")));

        var restricted = glossary.restrictTo("石膏30g");

        assertThat(restricted.size()).isEqualTo(1);
        assertThat(restricted.render()).contains("| 石膏 | Thạch cao | vị thuốc |").doesNotContain("知母");
    }

    @Test
    void shouldLetTheOverridingGlossaryReplaceEveryRowOfTheSameTerm() {
        var base = new Glossary(List.of(
                new GlossaryEntry("石膏", "Thạch cao", "a"), new GlossaryEntry("石膏", "Đá", "b"), new GlossaryEntry("知母", "Tri mẫu")));
        var user = new Glossary(List.of(new GlossaryEntry("石膏", "Thạch cao (riêng)")));

        var merged = base.overriddenBy(user);

        assertThat(merged.entries())
                .containsExactly(new GlossaryEntry("知母", "Tri mẫu"), new GlossaryEntry("石膏", "Thạch cao (riêng)"));
    }
}
