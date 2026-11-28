package tungnn.tutor.java.tool.todo.service;

import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tungnn.tutor.java.tool.todo.config.TodoConfig;
import tungnn.tutor.java.tool.todo.core.model.DateField;
import tungnn.tutor.java.tool.todo.core.model.Priority;
import tungnn.tutor.java.tool.todo.core.model.Task;
import tungnn.tutor.java.tool.todo.core.model.TaskRef;
import tungnn.tutor.java.tool.todo.core.parse.TaskLineEditor;
import tungnn.tutor.java.tool.todo.core.parse.TaskLineFormatter;
import tungnn.tutor.java.tool.todo.core.parse.TaskLineParser;
import tungnn.tutor.java.tool.todo.core.query.TaskFilter;
import tungnn.tutor.java.tool.todo.core.vault.MarkdownFileStore;
import tungnn.tutor.java.tool.todo.core.vault.MarkdownFileStore.Document;
import tungnn.tutor.java.tool.todo.core.vault.VaultScanner;

public final class TaskService {

    private static final Pattern DATE_PLACEHOLDER = Pattern.compile("\\{([^}]+)}");
    private static final Pattern HEADING = Pattern.compile("^(#{1,6})\\s+(.+?)\\s*$");
    private static final String STALE_HINT = "Task changed since it was listed, run list again";

    private final TodoConfig config;
    private final Clock clock;
    private final MarkdownFileStore store = new MarkdownFileStore();
    private final TaskLineParser parser;
    private final VaultScanner scanner;

    public TaskService(TodoConfig config, Clock clock) {
        this.config = config;
        this.clock = clock;
        this.parser = new TaskLineParser(config.globalFilter());
        this.scanner = new VaultScanner(config.vaultDir(), config.excludes(), parser, store);
    }

    public List<Task> list(TaskFilter filter) {
        var today = today();
        return scanner.scan().stream()
                .filter(task -> filter.matches(task, today))
                .sorted(TaskFilter.ORDER)
                .toList();
    }

    public TaskRef add(NewTask request) {
        var description =
                request.description() == null ? "" : request.description().strip();
        if (description.isEmpty() || description.contains("\n")) {
            throw new IllegalArgumentException("Task description must be a non-empty single line");
        }
        var line = TaskLineFormatter.format(description, request.priority(), today(), request.due(), request.tags());
        var filter = config.globalFilter();
        if (filter != null && !line.contains(filter)) {
            line = line + " " + filter;
        }

        var usesInbox = request.note() == null;
        var notePath = expandNote(usesInbox ? config.inboxNote() : request.note());
        var heading = request.heading() != null || !usesInbox ? request.heading() : config.inboxHeading();
        var file = resolveInVault(notePath);
        var document = store.readOrEmpty(file);
        var lines = document.lines();
        if (!lines.isEmpty() && lines.getLast().isEmpty()) {
            lines.removeLast();
        }

        var index = heading == null ? lines.size() : sectionEnd(lines, heading);
        lines.add(index, line);
        lines.add("");
        store.write(file, document);
        return TaskRef.of(notePath.replace('\\', '/'), index + 1, line);
    }

    public TaskRef complete(TaskRef ref) {
        var today = today();
        return mutate(ref, line -> {
            var done = TaskLineEditor.setStatus(line, 'x');
            return DateField.DONE.pattern().matcher(done).find()
                    ? done
                    : TaskLineEditor.setDate(done, DateField.DONE, today);
        });
    }

    public TaskRef reopen(TaskRef ref) {
        return mutate(ref, line -> TaskLineEditor.setDate(TaskLineEditor.setStatus(line, ' '), DateField.DONE, null));
    }

    public TaskRef update(TaskRef ref, TaskPatch patch) {
        return mutate(ref, line -> {
            var result = line;
            if (patch.text() != null) {
                result = TaskLineEditor.setDescription(result, patch.text());
            }
            if (patch.clearDue()) {
                result = TaskLineEditor.setDate(result, DateField.DUE, null);
            } else if (patch.due() != null) {
                result = TaskLineEditor.setDate(result, DateField.DUE, patch.due());
            }
            if (patch.priority() != null) {
                result = TaskLineEditor.setPriority(result, patch.priority());
            }
            for (var tag : patch.removeTags()) {
                result = TaskLineEditor.removeTag(result, tag);
            }
            for (var tag : patch.addTags()) {
                result = TaskLineEditor.addTag(result, tag);
            }
            return result;
        });
    }

    public int delete(TaskRef ref, boolean withChildren) {
        var located = locate(ref);
        var lines = located.document().lines();
        var childEnd = located.index() + 1;
        while (childEnd < lines.size()
                && !lines.get(childEnd).isBlank()
                && indentOf(lines.get(childEnd)) > indentOf(located.line())) {
            childEnd++;
        }
        var children = childEnd - located.index() - 1;
        if (children > 0 && !withChildren) {
            throw new IllegalStateException(
                    "Task has " + children + " sub-item line(s); use --with-children to delete them too");
        }
        lines.subList(located.index(), childEnd).clear();
        store.write(located.file(), located.document());
        return childEnd - located.index();
    }

    private record Located(Path file, Document document, int index, String line) {}

    private TaskRef mutate(TaskRef ref, UnaryOperator<String> change) {
        var located = locate(ref);
        var updated = change.apply(located.line());
        if (!updated.equals(located.line())) {
            located.document().lines().set(located.index(), updated);
            store.write(located.file(), located.document());
        }
        return TaskRef.of(ref.path(), ref.line(), updated);
    }

    private Located locate(TaskRef ref) {
        var file = resolveInVault(ref.path());
        if (!file.toFile().isFile()) {
            throw new StaleTaskException(STALE_HINT + " (note not found: " + ref.path() + ")");
        }
        var document = store.read(file);
        var index = ref.line() - 1;
        if (index < 0 || index >= document.lines().size()) {
            throw new StaleTaskException(STALE_HINT);
        }
        var line = document.lines().get(index);
        if (!TaskRef.hashOf(line).equals(ref.hash())
                || parser.parse(line, ref.path(), ref.line()).isEmpty()) {
            throw new StaleTaskException(STALE_HINT);
        }
        return new Located(file, document, index, line);
    }

    private Path resolveInVault(String relative) {
        var file = config.vaultDir().resolve(relative).normalize();
        if (!file.startsWith(config.vaultDir())) {
            throw new IllegalArgumentException("Path is outside the vault: " + relative);
        }
        return file;
    }

    private int sectionEnd(List<String> lines, String heading) {
        var wanted = heading.strip().replaceFirst("^#+\\s*", "");
        for (var i = 0; i < lines.size(); i++) {
            var found = HEADING.matcher(lines.get(i));
            if (found.matches() && found.group(2).equals(wanted)) {
                var level = found.group(1).length();
                var end = lines.size();
                for (var j = i + 1; j < lines.size(); j++) {
                    var next = HEADING.matcher(lines.get(j));
                    if (next.matches() && next.group(1).length() <= level) {
                        end = j;
                        break;
                    }
                }
                while (end > i + 1 && lines.get(end - 1).isBlank()) {
                    end--;
                }
                return end;
            }
        }
        if (!lines.isEmpty()) {
            lines.add("");
        }
        lines.add("## " + wanted);
        return lines.size();
    }

    private static int indentOf(String line) {
        return line.length() - line.stripLeading().length();
    }

    private String expandNote(String template) {
        var today = today();
        return DATE_PLACEHOLDER
                .matcher(template)
                .replaceAll(match -> Matcher.quoteReplacement(
                        today.format(DateTimeFormatter.ofPattern(match.group(1)))));
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}
