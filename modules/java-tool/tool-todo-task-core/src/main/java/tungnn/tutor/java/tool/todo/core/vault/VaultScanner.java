package tungnn.tutor.java.tool.todo.core.vault;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import tungnn.tutor.java.tool.todo.core.model.Task;
import tungnn.tutor.java.tool.todo.core.parse.TaskLineParser;
import tungnn.tutor.java.tool.todo.core.parse.TaskPatterns;

public final class VaultScanner {

    private final Path vault;
    private final List<String> excludes;
    private final TaskLineParser parser;
    private final MarkdownFileStore store;

    public VaultScanner(Path vault, List<String> excludes, TaskLineParser parser, MarkdownFileStore store) {
        this.vault = vault;
        this.excludes = excludes;
        this.parser = parser;
        this.store = store;
    }

    public List<Task> scan() {
        var tasks = new ArrayList<Task>();
        for (var file : markdownFiles()) {
            var relative = relativePath(file);
            var lines = store.read(file).lines();
            var inFence = false;
            for (var i = 0; i < lines.size(); i++) {
                var line = lines.get(i);
                if (TaskPatterns.FENCE.matcher(line).find()) {
                    inFence = !inFence;
                    continue;
                }
                if (!inFence) {
                    parser.parse(line, relative, i + 1).ifPresent(tasks::add);
                }
            }
        }
        return tasks;
    }

    private List<Path> markdownFiles() {
        var files = new ArrayList<Path>();
        if (!Files.isDirectory(vault)) {
            throw new IllegalArgumentException("Vault directory does not exist: " + vault);
        }
        try {
            Files.walkFileTree(vault, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    var skip =
                            !dir.equals(vault) && (dir.getFileName().toString().startsWith(".") || isExcluded(dir));
                    return skip ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (file.getFileName().toString().endsWith(".md") && !isExcluded(file)) {
                        files.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot scan " + vault, e);
        }
        files.sort(null);
        return files;
    }

    private boolean isExcluded(Path path) {
        var relative = relativePath(path);
        return excludes.stream().anyMatch(exclude -> relative.equals(exclude) || relative.startsWith(exclude + "/"));
    }

    private String relativePath(Path path) {
        return vault.relativize(path).toString().replace('\\', '/');
    }
}
