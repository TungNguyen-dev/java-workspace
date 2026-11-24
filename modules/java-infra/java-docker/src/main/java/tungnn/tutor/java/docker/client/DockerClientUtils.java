package tungnn.tutor.java.docker.client;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.Version;
import java.util.Objects;

public final class DockerClientUtils {
    private DockerClientUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void ping(DockerClient client) {
        Objects.requireNonNull(client, "client must not be null");
        client.pingCmd().exec();
    }

    public static boolean isAvailable(DockerClient client) {
        try {
            ping(client);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static Info info(DockerClient client) {
        Objects.requireNonNull(client, "client must not be null");
        return client.infoCmd().exec();
    }

    public static Version version(DockerClient client) {
        Objects.requireNonNull(client, "client must not be null");
        return client.versionCmd().exec();
    }
}
