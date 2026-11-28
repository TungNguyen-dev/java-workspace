package tungnn.tutor.java.tool.meddoc.core.translator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtils;

class PromptResourcesTest {

    private static String prompt(String name) {
        return ResourceUtils.getResourceAsString("prompts/" + name);
    }

    @Test
    void shouldKeepTheTranslationRulesFromTheCourseTranslationSkill() {
        var translate = prompt("translate-tcm-text.md");

        assertThat(translate)
                .contains("Không dùng từ \"tễ\"")
                .contains("Bài thuốc thanh nhiệt")
                .contains("Đặc điểm phối ngũ của phương này")
                .contains("(Trang N – tên sách)")
                .contains("`【送用】` → `【运用】`");
    }

    @Test
    void shouldNotCallTheBookPhuongTeHocAnywhere() {
        for (var name : List.of("translate-tcm-text.md", "polish-vietnamese.md", "ocr-correct-chinese.md")) {
            assertThat(prompt(name)).as(name).doesNotContain("Phương tễ học").contains("Phương thuốc học");
        }
    }
}
