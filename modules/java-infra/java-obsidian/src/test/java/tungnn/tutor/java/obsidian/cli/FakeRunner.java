package tungnn.tutor.java.obsidian.cli;

import java.util.ArrayList;
import java.util.List;

public class FakeRunner implements CommandRunner {

    public final List<List<String>> calls = new ArrayList<>();
    public CommandResult next = new CommandResult(0, "", "");

    @Override
    public CommandResult run(List<String> command) {
        calls.add(command);
        return next;
    }
}
