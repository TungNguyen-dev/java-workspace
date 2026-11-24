package tungnn.tutor.java.docker.client;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import java.time.Duration;
import java.util.Objects;

public final class DockerClientFactory {
    private static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration RESPONSE_TIMEOUT = Duration.ofSeconds(45);
    private static final int MAX_CONNECTIONS = 100;

    private DockerClientFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static DockerClient create() {
        return create(DefaultDockerClientConfig.createDefaultConfigBuilder().build());
    }

    public static DockerClient create(String dockerHost) {
        Objects.requireNonNull(dockerHost, "dockerHost must not be null");
        return create(DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .build());
    }

    public static DockerClient create(DockerClientConfig config) {
        Objects.requireNonNull(config, "config must not be null");
        var httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(MAX_CONNECTIONS)
                .connectionTimeout(CONNECTION_TIMEOUT)
                .responseTimeout(RESPONSE_TIMEOUT)
                .build();
        return DockerClientImpl.getInstance(config, httpClient);
    }
}
