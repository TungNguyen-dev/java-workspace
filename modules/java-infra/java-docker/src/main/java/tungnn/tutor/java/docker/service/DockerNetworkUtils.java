package tungnn.tutor.java.docker.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Network;
import java.util.List;
import java.util.Objects;

public final class DockerNetworkUtils {
    private DockerNetworkUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String create(DockerClient client, String name) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(name, "name must not be null");
        return client.createNetworkCmd().withName(name).exec().getId();
    }

    public static void remove(DockerClient client, String networkId) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(networkId, "networkId must not be null");
        client.removeNetworkCmd(networkId).exec();
    }

    public static List<Network> list(DockerClient client) {
        Objects.requireNonNull(client, "client must not be null");
        return client.listNetworksCmd().exec();
    }

    public static Network inspect(DockerClient client, String networkId) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(networkId, "networkId must not be null");
        return client.inspectNetworkCmd().withNetworkId(networkId).exec();
    }

    public static boolean exists(DockerClient client, String networkId) {
        try {
            inspect(client, networkId);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    public static void connect(DockerClient client, String networkId, String containerId) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(networkId, "networkId must not be null");
        Objects.requireNonNull(containerId, "containerId must not be null");
        client.connectToNetworkCmd()
                .withNetworkId(networkId)
                .withContainerId(containerId)
                .exec();
    }

    public static void disconnect(DockerClient client, String networkId, String containerId) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(networkId, "networkId must not be null");
        Objects.requireNonNull(containerId, "containerId must not be null");
        client.disconnectFromNetworkCmd()
                .withNetworkId(networkId)
                .withContainerId(containerId)
                .exec();
    }
}
