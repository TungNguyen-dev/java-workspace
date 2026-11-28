package tungnn.tutor.java.tool.meddoc.core.glossary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GlossarySourcesTest {

    @TempDir
    Path dir;

    @Test
    void shouldLoadTheBuiltInGlossary() throws IOException {
        var glossary = ClasspathGlossarySource.defaults().load();

        assertThat(glossary.isEmpty()).isFalse();
        assertThat(glossary.matching("白虎汤")).extracting(GlossaryEntry::target).containsExactly("Bạch hổ thang");
    }

    @Test
    void shouldFailOnAMissingResource() {
        assertThatThrownBy(() -> new ClasspathGlossarySource("glossary/none.csv").load())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void shouldMergeSourcesSoTheLaterOneWins() throws IOException {
        var mine = dir.resolve("mine.csv");
        Files.writeString(mine, "source,target\n石膏,Gypsum\n");
        var composite = new CompositeGlossarySource(
                List.of(ClasspathGlossarySource.defaults(), new FileGlossarySource(mine)));

        var glossary = composite.load();

        assertThat(glossary.matching("石膏")).extracting(GlossaryEntry::target).containsExactly("Gypsum");
        assertThat(glossary.matching("知母")).isNotEmpty();
        assertThat(composite.describe()).contains("built-in").contains("mine.csv");
    }
}
