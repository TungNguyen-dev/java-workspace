package tungnn.tutor.java.docker.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.PruneResponse;
import com.github.dockerjava.api.model.PruneType;
import java.util.List;
import java.util.Objects;

public final class DockerVolumeUtils {
    private DockerVolumeUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String create(DockerClient client, String name) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(name, "name must not be null");
        return client.createVolumeCmd().withName(name).exec().getName();
    }

    public static void remove(DockerClient client, String name) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(name, "name must not be null");
        client.removeVolumeCmd(name).exec();
    }

    public static List<InspectVolumeResponse> list(DockerClient client) {
        Objects.requireNonNull(client, "client must not be null");
        var response = client.listVolumesCmd().exec();
        return response.getVolumes() == null ? List.of() : response.getVolumes();
    }

    public static InspectVolumeResponse inspect(DockerClient client, String name) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(name, "name must not be null");
        return client.inspectVolumeCmd(name).exec();
    }

    public static boolean exists(DockerClient client, String name) {
        try {
            inspect(client, name);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    public static PruneResponse prune(DockerClient client) {
        Objects.requireNonNull(client, "client must not be null");
        return client.pruneCmd(PruneType.VOLUMES).exec();
    }
}
