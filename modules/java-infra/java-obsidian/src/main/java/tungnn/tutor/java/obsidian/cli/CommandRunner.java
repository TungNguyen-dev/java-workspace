package tungnn.tutor.java.obsidian.cli;

import java.util.List;

public interface CommandRunner {

    CommandResult run(List<String> command);
}
