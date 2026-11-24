package tungnn.tutor.java.docker.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import tungnn.tutor.java.docker.model.ContainerSpec;
import tungnn.tutor.java.docker.support.DockerAwait;

public final class DockerContainerUtils {
    private DockerContainerUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String create(DockerClient client, ContainerSpec spec) {
        return create(client, spec, UnaryOperator.identity());
    }

    public static String create(DockerClient client, ContainerSpec spec, UnaryOperator<CreateContainerCmd> customizer) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(spec, "spec must not be null");
        Objects.requireNonNull(customizer, "customizer must not be null");
        var cmd = customizer.apply(toCreateCommand(client, spec));
        return cmd.exec().getId();
    }

    public static String run(DockerClient client, ContainerSpec spec) {
        var id = create(client, spec);
        start(client, id);
        return id;
    }

    public static void start(DockerClient client, String containerId) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(containerId, "containerId must not be null");
        client.startContainerCmd(containerId).exec();
    }

    public static void stop(DockerClient client, String containerId, Duration gracePeriod) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(containerId, "containerId must not be null");
        Objects.requireNonNull(gracePeriod, "gracePeriod must not be null");
        client.stopContainerCmd(containerId)
                .withTimeout((int) gracePeriod.toSeconds())
                .exec();
    }

    public static void restart(DockerClient client, String containerId) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(containerId, "containerId must not be null");
        client.restartContainerCmd(containerId).exec();
    }

    public static void kill(DockerClient client, String containerId) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(containerId, "containerId must not be null");
        client.killContainerCmd(containerId).exec();
    }

    public static void remove(DockerClient client, String containerId, boolean force, boolean removeVolumes) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(containerId, "containerId must not be null");
        client.removeContainerCmd(containerId)
                .withForce(force)
                .withRemoveVolumes(removeVolumes)
                .exec();
    }

    public static InspectContainerResponse inspect(DockerClient client, String containerId) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(containerId, "containerId must not be null");
        return client.inspectContainerCmd(containerId).exec();
    }

    public static List<Container> list(DockerClient client, boolean includeStopped) {
        Objects.requireNonNull(client, "client must not be null");
        return client.listContainersCmd().withShowAll(includeStopped).exec();
    }

    public static List<Container> listByLabel(DockerClient client, String key, String value, boolean includeStopped) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(key, "key must not be null");
        var label = value == null ? key : key + "=" + value;
        return client.listContainersCmd()
                .withShowAll(includeStopped)
                .withLabelFilter(List.of(label))
                .exec();
    }

    public static Optional<Container> findByName(DockerClient client, String name) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(name, "name must not be null");
        var expected = name.startsWith("/") ? name : "/" + name;
        return client.listContainersCmd().withShowAll(true).withNameFilter(List.of(name)).exec().stream()
                .filter(c -> c.getNames() != null && List.of(c.getNames()).contains(expected))
                .findFirst();
    }

    public static boolean isRunning(DockerClient client, String containerId) {
        try {
            var state = inspect(client, containerId).getState();
            return state != null && Boolean.TRUE.equals(state.getRunning());
        } catch (NotFoundException e) {
            return false;
        }
    }

    public static int waitFor(DockerClient client, String containerId, Duration timeout) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(containerId, "containerId must not be null");
        Objects.requireNonNull(timeout, "timeout must not be null");
        var callback = client.waitContainerCmd(containerId).exec(new WaitContainerResultCallback());
        DockerAwait.await(callback, timeout, "wait for container " + containerId);
        return callback.awaitStatusCode();
    }

    public static void copyToContainer(DockerClient client, String containerId, String hostPath, String remotePath) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(containerId, "containerId must not be null");
        Objects.requireNonNull(hostPath, "hostPath must not be null");
        Objects.requireNonNull(remotePath, "remotePath must not be null");
        client.copyArchiveToContainerCmd(containerId)
                .withHostResource(hostPath)
                .withRemotePath(remotePath)
                .exec();
    }

    public static InputStream copyFromContainer(DockerClient client, String containerId, String resource) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(containerId, "containerId must not be null");
        Objects.requireNonNull(resource, "resource must not be null");
        return client.copyArchiveFromContainerCmd(containerId, resource).exec();
    }

    static CreateContainerCmd toCreateCommand(DockerClient client, ContainerSpec spec) {
        var cmd = client.createContainerCmd(spec.image());
        if (spec.name() != null) {
            cmd.withName(spec.name());
        }
        if (!spec.cmd().isEmpty()) {
            cmd.withCmd(spec.cmd());
        }
        if (!spec.env().isEmpty()) {
            cmd.withEnv(spec.env().entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .toList());
        }
        if (!spec.labels().isEmpty()) {
            cmd.withLabels(spec.labels());
        }

        var hostConfig = HostConfig.newHostConfig().withAutoRemove(spec.autoRemove());
        if (!spec.ports().isEmpty()) {
            var bindings = spec.ports().entrySet().stream()
                    .map(e -> new PortBinding(Ports.Binding.bindPort(e.getKey()), ExposedPort.tcp(e.getValue())))
                    .toList();
            hostConfig.withPortBindings(bindings);
            cmd.withExposedPorts(
                    spec.ports().values().stream().map(ExposedPort::tcp).toList());
        }
        if (!spec.binds().isEmpty()) {
            hostConfig.withBinds(spec.binds().stream().map(Bind::parse).toList());
        }
        if (spec.network() != null) {
            hostConfig.withNetworkMode(spec.network());
        }
        return cmd.withHostConfig(hostConfig);
    }
}
