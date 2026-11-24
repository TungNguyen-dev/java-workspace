package tungnn.tutor.java.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.docker.client.DockerClientUtils;
import tungnn.tutor.java.docker.model.ContainerSpec;
import tungnn.tutor.java.docker.model.ExecResult;
import tungnn.tutor.java.docker.service.DockerContainerUtils;
import tungnn.tutor.java.docker.service.DockerExecUtils;
import tungnn.tutor.java.docker.support.DockerOperationException;
import tungnn.tutor.java.docker.support.FrameCollector;

class DockerUnitTest {

    @Test
    void frameCollectorSeparatesStdoutAndStderr() {
        var collector = new FrameCollector();
        collector.onNext(new Frame(StreamType.STDOUT, "out".getBytes()));
        collector.onNext(new Frame(StreamType.STDERR, "err".getBytes()));

        assertThat(collector.stdout()).isEqualTo("out");
        assertThat(collector.stderr()).isEqualTo("err");
    }

    @Test
    void containerSpecBuilderCollectsValues() {
        var spec = ContainerSpec.builder("alpine:3.20")
                .name("demo")
                .cmd("sleep", "10")
                .env("A", "1")
                .port(8080, 80)
                .bind("/tmp", "/data")
                .label("k", "v")
                .network("net")
                .autoRemove(true)
                .build();

        assertThat(spec.cmd()).containsExactly("sleep", "10");
        assertThat(spec.env()).containsEntry("A", "1");
        assertThat(spec.ports()).containsEntry(8080, 80);
        assertThat(spec.binds()).containsExactly("/tmp:/data");
        assertThat(spec.autoRemove()).isTrue();
    }

    @Test
    void containerSpecRequiresImage() {
        assertThatThrownBy(() -> ContainerSpec.builder(null).build()).isInstanceOf(NullPointerException.class);
    }

    @Test
    void createMapsSpecToCommand() {
        var client = mock(DockerClient.class, RETURNS_DEEP_STUBS);
        var createCmd = client.createContainerCmd("alpine:3.20");
        when(createCmd.withName("demo")).thenReturn(createCmd);
        when(createCmd.withHostConfig(org.mockito.ArgumentMatchers.any())).thenReturn(createCmd);
        when(createCmd.exec().getId()).thenReturn("abc");

        var id = DockerContainerUtils.create(
                client, ContainerSpec.builder("alpine:3.20").name("demo").build());

        assertThat(id).isEqualTo("abc");
        verify(createCmd).withName("demo");
    }

    @Test
    void execRejectsEmptyCommand() {
        var client = mock(DockerClient.class);

        assertThatThrownBy(() -> DockerExecUtils.exec(client, "id", Duration.ofSeconds(1)))
                .isInstanceOf(DockerOperationException.class);
    }

    @Test
    void execResultSuccessReflectsExitCode() {
        assertThat(new ExecResult(0, "", "").success()).isTrue();
        assertThat(new ExecResult(1, "", "").success()).isFalse();
    }

    @Test
    void isAvailableReturnsFalseWhenPingFails() {
        var client = mock(DockerClient.class, RETURNS_DEEP_STUBS);
        when(client.pingCmd().exec()).thenThrow(new RuntimeException("down"));

        assertThat(DockerClientUtils.isAvailable(client)).isFalse();
    }
}
