package tungnn.tutor.java.tool.meddoc.core.translate;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtils;
import tungnn.tutor.java.tool.meddoc.core.glossary.ClasspathGlossarySource;

class PromptResourcesTest {

    private static String prompt(String name) {
        return ResourceUtils.getResourceAsString("prompts/" + name);
    }

    @Test
    void shouldKeepTheTranslationPromptFreeOfBookSpecificTerms() {
        var translate = prompt("translate-tcm-text.md");

        assertThat(translate)
                .contains("Bảng thuật ngữ bắt buộc")
                .contains("(Trang N – tên sách)")
                .contains("{languageRules}")
                .doesNotContain("白虎汤")
                .doesNotContain("组成")
                .doesNotContain("Bài thuốc thanh nhiệt");
    }

    @Test
    void shouldKeepTheVietnameseRulesInTheLanguageSupplement() {
        assertThat(prompt("translate-tcm-text.vi.md"))
                .contains("Không dùng từ \"tễ\"")
                .contains("Bài thuốc thanh nhiệt");
    }

    @Test
    void shouldKeepTheOcrExamples() {
        assertThat(prompt("ocr-correct-chinese.md")).contains("`【送用】` → `【运用】`");
    }

    @Test
    void shouldNotCallTheBookPhuongTeHocAnywhere() {
        for (var name : List.of("translate-tcm-text.md", "translate-tcm-text.vi.md", "polish-vietnamese.md")) {
            assertThat(prompt(name)).as(name).doesNotContain("Phương tễ học");
        }
    }

    @Test
    void shouldShipADefaultGlossaryWithTheTermsMovedOutOfThePrompts() throws IOException {
        var glossary = ClasspathGlossarySource.defaults().load();

        assertThat(glossary.matching("方剂学 组成")).extracting(e -> e.target()).contains("Phương thuốc học", "Thành phần");
    }
}
