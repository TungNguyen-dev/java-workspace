package tungnn.tutor.java.docker.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;
import java.io.Closeable;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;
import tungnn.tutor.java.docker.support.DockerAwait;
import tungnn.tutor.java.docker.support.FrameCollector;

public final class DockerLogUtils {
    private DockerLogUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String logs(DockerClient client, String containerId, int tail, Duration timeout) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(containerId, "containerId must not be null");
        Objects.requireNonNull(timeout, "timeout must not be null");
        var cmd = client.logContainerCmd(containerId).withStdOut(true).withStdErr(true);
        if (tail >= 0) {
            cmd.withTail(tail);
        }
        var collector = DockerAwait.await(cmd.exec(new FrameCollector()), timeout, "read logs of " + containerId);
        return collector.stdout() + collector.stderr();
    }

    public static Closeable follow(DockerClient client, String containerId, Consumer<String> lineConsumer) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(containerId, "containerId must not be null");
        Objects.requireNonNull(lineConsumer, "lineConsumer must not be null");
        return client.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .withFollowStream(true)
                .exec(new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame frame) {
                        lineConsumer.accept(new String(frame.getPayload()));
                    }
                });
    }
}
