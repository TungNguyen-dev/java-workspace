package tungnn.tutor.java.tool.todo.core.vault;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MarkdownFileStore {

    public record Document(List<String> lines, String eol) {}

    public Document read(Path file) {
        try {
            var content = Files.readString(file, StandardCharsets.UTF_8);
            var eol = content.contains("\r\n") ? "\r\n" : "\n";
            return new Document(new ArrayList<>(Arrays.asList(content.split("\r?\n", -1))), eol);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + file, e);
        }
    }

    public Document readOrEmpty(Path file) {
        return Files.exists(file) ? read(file) : new Document(new ArrayList<>(), "\n");
    }

    public void write(Path file, Document document) {
        try {
            var dir = file.toAbsolutePath().getParent();
            Files.createDirectories(dir);
            var temp = Files.createTempFile(dir, ".todo-", ".tmp");
            try {
                Files.writeString(temp, String.join(document.eol(), document.lines()), StandardCharsets.UTF_8);
                try {
                    Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(temp);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write " + file, e);
        }
    }
}
