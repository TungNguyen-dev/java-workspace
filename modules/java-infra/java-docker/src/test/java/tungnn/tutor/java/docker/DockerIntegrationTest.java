package tungnn.tutor.java.docker;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.dockerjava.api.DockerClient;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.docker.client.DockerClientFactory;
import tungnn.tutor.java.docker.client.DockerClientUtils;
import tungnn.tutor.java.docker.model.ContainerSpec;
import tungnn.tutor.java.docker.service.DockerContainerUtils;
import tungnn.tutor.java.docker.service.DockerExecUtils;
import tungnn.tutor.java.docker.service.DockerImageUtils;
import tungnn.tutor.java.docker.service.DockerLogUtils;
import tungnn.tutor.java.docker.service.DockerNetworkUtils;
import tungnn.tutor.java.docker.service.DockerVolumeUtils;

class DockerIntegrationTest {
    private static final String IMAGE = "alpine:3.20";
    private static final Duration TIMEOUT = Duration.ofMinutes(2);

    private static DockerClient client;
    private static String containerId;
    private static String networkId;
    private static String volumeName;

    @BeforeAll
    static void setUp() {
        client = DockerClientFactory.create();
        org.junit.jupiter.api.Assumptions.assumeTrue(DockerClientUtils.isAvailable(client), "Docker is not available");
    }

    @AfterAll
    static void tearDown() {
        if (client == null) {
            return;
        }
        try {
            if (containerId != null) {
                DockerContainerUtils.remove(client, containerId, true, true);
            }
            if (networkId != null) {
                DockerNetworkUtils.remove(client, networkId);
            }
            if (volumeName != null) {
                DockerVolumeUtils.remove(client, volumeName);
            }
            client.close();
        } catch (Exception ignored) {
            // cleanup is best effort
        }
    }

    @Test
    void containerLifecycleExecAndLogs() {
        DockerImageUtils.pull(client, IMAGE, TIMEOUT);
        assertThat(DockerImageUtils.exists(client, IMAGE)).isTrue();

        var label = UUID.randomUUID().toString();
        containerId = DockerContainerUtils.run(
                client,
                ContainerSpec.builder(IMAGE)
                        .cmd("sh", "-c", "echo started; sleep 60")
                        .label("java-docker-test", label)
                        .build());

        assertThat(DockerContainerUtils.isRunning(client, containerId)).isTrue();
        assertThat(DockerContainerUtils.listByLabel(client, "java-docker-test", label, false))
                .hasSize(1);

        var result = DockerExecUtils.exec(client, containerId, Duration.ofSeconds(30), "echo", "hi");
        assertThat(result.success()).isTrue();
        assertThat(result.stdout().trim()).isEqualTo("hi");

        assertThat(DockerLogUtils.logs(client, containerId, 10, Duration.ofSeconds(30)))
                .contains("started");

        DockerContainerUtils.stop(client, containerId, Duration.ofSeconds(2));
        assertThat(DockerContainerUtils.isRunning(client, containerId)).isFalse();
    }

    @Test
    void networkAndVolumeLifecycle() {
        var suffix = UUID.randomUUID().toString().substring(0, 8);

        networkId = DockerNetworkUtils.create(client, "java-docker-net-" + suffix);
        assertThat(DockerNetworkUtils.exists(client, networkId)).isTrue();

        volumeName = DockerVolumeUtils.create(client, "java-docker-vol-" + suffix);
        assertThat(DockerVolumeUtils.exists(client, volumeName)).isTrue();
    }
}
