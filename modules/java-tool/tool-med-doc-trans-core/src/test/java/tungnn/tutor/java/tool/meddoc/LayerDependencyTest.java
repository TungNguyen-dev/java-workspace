package tungnn.tutor.java.tool.meddoc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class LayerDependencyTest {

    private static final Path ROOT = Path.of("src/main/java/tungnn/tutor/java/tool/meddoc");
    private static final Pattern IMPORT =
            Pattern.compile("^import (?:static )?tungnn\\.tutor\\.java\\.tool\\.meddoc\\.(\\w+)", Pattern.MULTILINE);
    private static final Map<String, Set<String>> ALLOWED = Map.of(
            "core", Set.of("core"),
            "llm", Set.of("llm", "core"),
            "service", Set.of("service", "llm", "core"));

    @Test
    void shouldOnlyDependOnLowerLayers() throws IOException {
        var violations = new ArrayList<String>();
        try (var files = Files.walk(ROOT)) {
            for (var file : files.filter(f -> f.toString().endsWith(".java")).toList()) {
                var layer = ROOT.relativize(file).getName(0).toString();
                var allowed = ALLOWED.get(layer);
                if (allowed == null) {
                    continue;
                }
                var matcher = IMPORT.matcher(Files.readString(file));
                while (matcher.find()) {
                    var target = matcher.group(1);
                    if (!allowed.contains(target)) {
                        violations.add(ROOT.relativize(file) + " imports " + target);
                    }
                }
            }
        }
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldKnowEveryTopLevelPackage() throws IOException {
        try (var dirs = Files.list(ROOT)) {
            var layers = dirs.filter(Files::isDirectory)
                    .map(d -> d.getFileName().toString())
                    .toList();
            assertThat(layers).allMatch(ALLOWED::containsKey);
        }
    }
}
