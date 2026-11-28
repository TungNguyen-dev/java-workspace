package tungnn.tutor.java.claude.cli;

import tungnn.tutor.java.claude.support.ClaudeException;

public class ClaudeCliFailure extends ClaudeException {

    private final transient ClaudeCliResult partialResult;

    public ClaudeCliFailure(String message, ClaudeCliResult partialResult) {
        super(message);
        this.partialResult = partialResult;
    }

    public ClaudeCliResult partialResult() {
        return partialResult;
    }
}
