package tungnn.tutor.java.obsidian.cli;

import java.util.ArrayList;
import java.util.List;
import tungnn.tutor.java.obsidian.config.ObsidianConfig;

public class ObsidianCli {

    private final ObsidianConfig config;
    private final CommandRunner runner;

    public ObsidianCli(ObsidianConfig config, CommandRunner runner) {
        this.config = config;
        this.runner = runner;
    }

    public static ObsidianCli create(ObsidianConfig config) {
        return new ObsidianCli(config, new ProcessCommandRunner(config.timeout()));
    }

    public String execute(ObsidianCommand command) {
        var full = new ArrayList<String>();
        full.add(config.executable().toString());
        full.addAll(command.toArgs(config.vault()));
        return check(runner.run(List.copyOf(full)), full);
    }

    public List<String> executeLines(ObsidianCommand command) {
        return execute(command)
                .lines()
                .map(String::strip)
                .filter(l -> !l.isEmpty())
                .toList();
    }

    private static String check(CommandResult result, List<String> command) {
        var stdout = result.stdout() == null ? "" : result.stdout();
        var stderr = result.stderr() == null ? "" : result.stderr();
        // The CLI exits with 0 even on failure (e.g. "The CLI is unable to find Obsidian"), so output text is checked
        // too
        var failed = result.exitCode() != 0 || looksLikeError(stdout);
        if (failed) {
            var detail = stderr.isBlank() ? stdout : stderr;
            throw new ObsidianCliException("Obsidian CLI failed (exit " + result.exitCode() + ") for "
                    + command.subList(1, command.size()) + ": " + detail.strip());
        }
        return stdout;
    }

    private static boolean looksLikeError(String stdout) {
        var head = stdout.stripLeading();
        return head.startsWith("Error") || head.startsWith("The CLI is unable") || head.startsWith("Unknown command");
    }
}
