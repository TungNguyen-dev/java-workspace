package tungnn.tutor.java.tool.todo;

import java.io.PrintStream;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import tungnn.tutor.java.tool.todo.config.TodoConfig;
import tungnn.tutor.java.tool.todo.core.model.Priority;
import tungnn.tutor.java.tool.todo.core.model.Task;
import tungnn.tutor.java.tool.todo.core.model.TaskRef;
import tungnn.tutor.java.tool.todo.core.query.TaskFilter;
import tungnn.tutor.java.tool.todo.service.NewTask;
import tungnn.tutor.java.tool.todo.service.StaleTaskException;
import tungnn.tutor.java.tool.todo.service.TaskPatch;
import tungnn.tutor.java.tool.todo.service.TaskService;

public final class TodoTaskApp {

    private static final String USAGE = """
        Usage:
          list   [--vault dir] [--status todo|done|all] [--due-before d] [--overdue] [--tag t] [--path p] [--text s]
          add    "<description>" [--vault dir] [--note Inbox.md] [--heading h] [--due d] [--priority p] [--tag t]...
          done   <id> [--vault dir]
          undone <id> [--vault dir]
          edit   <id> [--vault dir] [--text s] [--due d|none] [--priority p|none] [--add-tag t] [--remove-tag t]
          delete <id> [--vault dir] [--with-children]\
        """;

    private static final Set<String> COMMANDS = Set.of("list", "add", "done", "undone", "edit", "delete");
    private static final Set<String> BOOLEAN_FLAGS = Set.of("overdue", "with-children");

    private TodoTaskApp() {}

    public static void main(String[] args) {
        var code = run(args, System.getenv(), Clock.systemDefaultZone(), System.out, System.err);
        if (code != 0) {
            System.exit(code);
        }
    }

    static int run(String[] args, Map<String, String> env, Clock clock, PrintStream out, PrintStream err) {
        if (args.length == 0 || !COMMANDS.contains(args[0])) {
            err.println(USAGE);
            return 2;
        }
        try {
            var parsed = Arguments.parse(args);
            var config = TodoConfig.fromEnv(env, parsed.single("vault"));
            var service = new TaskService(config, clock);
            execute(args[0], parsed, service, LocalDate.now(clock), out);
            return 0;
        } catch (StaleTaskException | IllegalStateException e) {
            err.println(e.getMessage());
            return 1;
        } catch (IllegalArgumentException e) {
            err.println(e.getMessage());
            err.println(USAGE);
            return 2;
        }
    }

    private static void execute(String command, Arguments args, TaskService service, LocalDate today, PrintStream out) {
        switch (command) {
            case "list" -> {
                var filter = new TaskFilter(
                        args.single("status") == null
                                ? TaskFilter.Scope.OPEN
                                : TaskFilter.Scope.parse(args.single("status")),
                        args.date("due-before"),
                        args.flag("overdue"),
                        args.single("tag"),
                        args.single("path"),
                        args.single("text"));
                service.list(filter).forEach(task -> out.println(format(task)));
            }
            case "add" -> {
                var request = new NewTask(
                        String.join(" ", args.positionals()),
                        args.single("note"),
                        args.single("heading"),
                        args.single("priority") == null ? Priority.NONE : Priority.parse(args.single("priority")),
                        args.date("due"),
                        args.all("tag"));
                out.println(service.add(request));
            }
            case "done" -> out.println(service.complete(args.id()));
            case "undone" -> out.println(service.reopen(args.id()));
            case "edit" -> {
                var due = args.single("due");
                var priority = args.single("priority");
                var clearPriority = "none".equalsIgnoreCase(priority);
                var patch = new TaskPatch(
                        args.single("text"),
                        due == null || "none".equalsIgnoreCase(due) ? null : args.date("due"),
                        "none".equalsIgnoreCase(due),
                        priority == null ? null : clearPriority ? Priority.NONE : Priority.parse(priority),
                        args.all("add-tag"),
                        args.all("remove-tag"));
                if (patch.isEmpty()) {
                    throw new IllegalArgumentException("Nothing to edit: pass at least one option");
                }
                out.println(service.update(args.id(), patch));
            }
            case "delete" ->
                out.println("Deleted " + service.delete(args.id(), args.flag("with-children")) + " line(s)");
            default -> throw new IllegalArgumentException("Unknown command: " + command);
        }
    }

    private static String format(Task task) {
        return "%s  [%c] %s %s %s"
                .formatted(
                        task.ref(),
                        task.symbol(),
                        task.priority() == Priority.NONE
                                ? "-"
                                : task.priority().name().toLowerCase(),
                        task.due() == null ? "----------" : task.due(),
                        task.description())
                .stripTrailing();
    }

    private record Arguments(Map<String, List<String>> options, List<String> positionals) {

        static Arguments parse(String[] args) {
            var options = new HashMap<String, List<String>>();
            var positionals = new ArrayList<String>();
            for (var i = 1; i < args.length; i++) {
                if (!args[i].startsWith("--")) {
                    positionals.add(args[i]);
                    continue;
                }
                var name = args[i].substring(2);
                if (BOOLEAN_FLAGS.contains(name)) {
                    options.computeIfAbsent(name, k -> new ArrayList<>()).add("true");
                } else if (i + 1 < args.length) {
                    options.computeIfAbsent(name, k -> new ArrayList<>()).add(args[++i]);
                } else {
                    throw new IllegalArgumentException("Missing value for --" + name);
                }
            }
            return new Arguments(options, positionals);
        }

        String single(String name) {
            var values = options.get(name);
            return values == null ? null : values.getLast();
        }

        List<String> all(String name) {
            return options.getOrDefault(name, List.of());
        }

        boolean flag(String name) {
            return options.containsKey(name);
        }

        LocalDate date(String name) {
            var value = single(name);
            try {
                return value == null ? null : LocalDate.parse(value);
            } catch (java.time.format.DateTimeParseException e) {
                throw new IllegalArgumentException(
                        "Invalid date for --" + name + ": " + value + " (use yyyy-MM-dd)", e);
            }
        }

        TaskRef id() {
            if (positionals.size() != 1) {
                throw new IllegalArgumentException("Expected exactly one task id");
            }
            return TaskRef.parse(positionals.getFirst());
        }
    }
}
