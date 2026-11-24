package tungnn.tutor.java.tool.hub.plugin.meddoc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.hub.api.ToolException;
import tungnn.tutor.java.tool.hub.api.ToolFile;

class MedDocTranslateToolTest {

    private final MedDocTranslateTool tool = new MedDocTranslateTool();

    @Test
    void shouldExposeFileLanguageAndProviderParams() {
        assertThat(tool.params()).extracting(p -> p.name()).contains("file", "targetLang", "provider", "refineVietnamese");
    }

    @Test
    void shouldRejectMissingFile() {
        assertThatThrownBy(() -> tool.execute(null, Map.of())).isInstanceOf(ToolException.class);
    }

    @Test
    void shouldRejectUnsupportedExtension() {
        var pdf = new ToolFile("book.pdf", "application/pdf", new byte[] {1});

        assertThatThrownBy(() -> tool.execute(null, Map.of("file", pdf, "targetLang", "VI")))
                .isInstanceOf(ToolException.class)
                .hasMessageContaining("Unsupported file");
    }
}
