package tungnn.tutor.java.docker.service;

import com.github.dockerjava.api.DockerClient;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import tungnn.tutor.java.docker.model.ExecResult;
import tungnn.tutor.java.docker.support.DockerAwait;
import tungnn.tutor.java.docker.support.DockerOperationException;
import tungnn.tutor.java.docker.support.FrameCollector;

public final class DockerExecUtils {
    private DockerExecUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ExecResult exec(DockerClient client, String containerId, Duration timeout, String... command) {
        return execAs(client, containerId, timeout, null, null, command);
    }

    public static ExecResult execAs(
            DockerClient client,
            String containerId,
            Duration timeout,
            String workingDir,
            String user,
            String... command) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(containerId, "containerId must not be null");
        Objects.requireNonNull(timeout, "timeout must not be null");
        Objects.requireNonNull(command, "command must not be null");
        if (command.length == 0) {
            throw new DockerOperationException("command must not be empty");
        }

        var execCmd = client.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd(command);
        if (workingDir != null) {
            execCmd.withWorkingDir(workingDir);
        }
        if (user != null) {
            execCmd.withUser(user);
        }
        var execId = execCmd.exec().getId();

        var collector = DockerAwait.await(
                client.execStartCmd(execId).exec(new FrameCollector()), timeout, "exec " + List.of(command));
        var exitCode = client.inspectExecCmd(execId).exec().getExitCodeLong();
        return new ExecResult(exitCode == null ? -1 : exitCode, collector.stdout(), collector.stderr());
    }
}
