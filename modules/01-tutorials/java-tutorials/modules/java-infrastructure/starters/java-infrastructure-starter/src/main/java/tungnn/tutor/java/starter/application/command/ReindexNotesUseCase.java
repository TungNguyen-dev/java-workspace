package tungnn.tutor.java.starter.application.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class ReindexNotesUseCase {

  private static final Path TARGET_DIR = Path.of("storage", "jls26", "15 - Expressions");

  static void main() throws IOException {
    new ReindexNotesUseCase().reorder(TARGET_DIR);
  }

  private void reorder(Path dir) throws IOException {
    List<Path> files;
    try (Stream<Path> walk = Files.walk(dir)) {
      files = walk.filter(Files::isRegularFile).sorted().toList();
    }

    var indexWidth = String.valueOf(files.size()).length();
    var pattern = "%0" + indexWidth + "d - %s";
    var counter = 0;
    for (var path : files) {
      var newName = pattern.formatted(++counter, extractTitle(path.getFileName().toString()));
      Files.move(path, path.resolveSibling(newName));
    }
  }

  private String extractTitle(String fileName) {
    return fileName.substring(fileName.indexOf('-') + 1).strip();
  }
}
