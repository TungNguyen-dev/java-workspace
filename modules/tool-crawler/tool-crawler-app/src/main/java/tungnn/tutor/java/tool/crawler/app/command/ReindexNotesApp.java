package tungnn.tutor.java.tool.crawler.app.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ReindexNotesApp {

    private static final Path TARGET_DIR = Path.of("storage", "jls26", "15 - Expressions");
    private static final Pattern NUMBERED_NAME = Pattern.compile("^(\\d+)\\s*-\\s*(.*)$");

    static void main() throws IOException {
        reorder(TARGET_DIR);
    }

    private static void reorder(Path dir) throws IOException {
        var files = listNumberedFiles(dir);
        var indexWidth = String.valueOf(files.size()).length();
        var pattern = "%0" + indexWidth + "d - %s";

        // Two phases so a new name can never collide with a file that has not been renamed yet.
        var temporary = new java.util.ArrayList<Path>();
        for (var path : files) {
            var tmp = path.resolveSibling(path.getFileName() + ".reindex");
            Files.move(path, tmp);
            temporary.add(tmp);
        }

        var counter = 0;
        for (var tmp : temporary) {
            var original = tmp.getFileName().toString().replaceFirst("\\.reindex$", "");
            var title = NUMBERED_NAME.matcher(original).replaceFirst("$2");
            Files.move(tmp, tmp.resolveSibling(pattern.formatted(++counter, title)));
        }
    }

    private static java.util.List<Path> listNumberedFiles(Path dir) throws IOException {
        try (Stream<Path> list = Files.list(dir)) {
            return list.filter(Files::isRegularFile)
                    .filter(path -> NUMBERED_NAME.matcher(path.getFileName().toString()).matches())
                    .sorted(Comparator.comparingInt(ReindexNotesApp::numericPrefix))
                    .toList();
        }
    }

    private static int numericPrefix(Path path) {
        var matcher = NUMBERED_NAME.matcher(path.getFileName().toString());
        return matcher.matches() ? Integer.parseInt(matcher.group(1)) : Integer.MAX_VALUE;
    }
}
