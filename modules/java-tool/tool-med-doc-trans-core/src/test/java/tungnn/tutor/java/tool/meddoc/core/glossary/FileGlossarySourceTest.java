package tungnn.tutor.java.tool.meddoc.core.glossary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileGlossarySourceTest {

    @TempDir
    Path dir;

    private Glossary load(String name, String content) throws IOException {
        var file = dir.resolve(name);
        Files.writeString(file, content);
        return new FileGlossarySource(file).load();
    }

    @Test
    void shouldReadACsvWithAnOptionalContextColumn() throws IOException {
        var glossary = load("g.csv", "Source,Target,Context\n石膏,Thạch cao,\n生地,Sinh địa,vị thuốc\n生地,Đất sống,nghĩa đen\n");

        assertThat(glossary.entries())
                .containsExactly(
                        new GlossaryEntry("石膏", "Thạch cao"),
                        new GlossaryEntry("生地", "Sinh địa", "vị thuốc"),
                        new GlossaryEntry("生地", "Đất sống", "nghĩa đen"));
    }

    @Test
    void shouldReadACsvWithoutAContextColumnAndSkipIncompleteRows() throws IOException {
        var glossary = load("g.csv", "source,target\n石膏,Thạch cao\n知母,\n,Lạ\n");

        assertThat(glossary.entries()).containsExactly(new GlossaryEntry("石膏", "Thạch cao"));
    }

    @Test
    void shouldReadATsv() throws IOException {
        var glossary = load("g.tsv", "source\ttarget\tcontext\n石膏\tThạch cao\ttên vị\n");

        assertThat(glossary.entries()).containsExactly(new GlossaryEntry("石膏", "Thạch cao", "tên vị"));
    }

    @Test
    void shouldReadAMarkdownTableAndIgnoreHeaderAndSeparator() throws IOException {
        var glossary = load("g.md", "| Source | Translation | Context |\n| --- | --- | --- |\n| 石膏 | Thạch cao | |\n| 白虎汤 | Bạch hổ thang | tên phương |\n");

        assertThat(glossary.entries())
                .containsExactly(new GlossaryEntry("石膏", "Thạch cao"), new GlossaryEntry("白虎汤", "Bạch hổ thang", "tên phương"));
    }

    @Test
    void shouldRejectACsvWithoutTheRequiredHeader() throws IOException {
        assertThatThrownBy(() -> load("g.csv", "term,meaning\n石膏,Thạch cao\n"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("source,target");
    }

    @Test
    void shouldRejectAMissingOrUnsupportedFile() throws IOException {
        assertThatThrownBy(() -> new FileGlossarySource(dir.resolve("none.csv")).load())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
        assertThatThrownBy(() -> load("g.xlsx", "x"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported");
    }
}
