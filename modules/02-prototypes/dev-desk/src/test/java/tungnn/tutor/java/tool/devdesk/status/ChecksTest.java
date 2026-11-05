package tungnn.tutor.java.tool.devdesk.status;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tungnn.tutor.java.core.lib.net.socket.utils.SocketUtils;
import tungnn.tutor.java.tool.devdesk.status.CheckResult.Level;
import tungnn.tutor.java.tool.devdesk.status.Checks.Requirement;

class ChecksTest {

  @Test
  void envIsOkWithoutRevealingValue() {
    var env = Map.of("API_KEY", "super-secret");
    var check = Checks.env("g", "Key", "API_KEY", Requirement.REQUIRED, "x", env::get);

    var result = check.run();

    assertThat(result.level()).isEqualTo(Level.OK);
    assertThat(result.detail()).doesNotContain("super-secret");
  }

  @Test
  void missingEnvIsWarnWhenOptionalAndDownWhenRequired() {
    var optional = Checks.env("g", "Key", "NOPE", Requirement.OPTIONAL, "x", n -> null).run();
    var required = Checks.env("g", "Key", "NOPE", Requirement.REQUIRED, "x", n -> " ").run();

    assertThat(optional.level()).isEqualTo(Level.WARN);
    assertThat(required.level()).isEqualTo(Level.DOWN);
  }

  @Test
  void pathChecksExistence(@TempDir Path dir) {
    assertThat(Checks.path("g", "P", dir, Requirement.REQUIRED, "x").run().level())
        .isEqualTo(Level.OK);
    assertThat(Checks.path("g", "P", dir.resolve("missing"), Requirement.OPTIONAL, "x").run().level())
        .isEqualTo(Level.WARN);
  }

  @Test
  void tcpDetectsOpenAndClosedPorts() throws IOException {
    int port;
    try (var server = SocketUtils.createTcpServerOnFreePort()) {
      port = server.getLocalPort();
      assertThat(Checks.tcp("g", "T", "localhost:" + port, Requirement.OPTIONAL, "x").run().level())
          .isEqualTo(Level.OK);
    }
    assertThat(Checks.tcp("g", "T", "localhost:" + port, Requirement.OPTIONAL, "x").run().level())
        .isEqualTo(Level.WARN);
  }

  @Test
  void malformedHostPortIsDown() {
    assertThat(Checks.tcp("g", "T", "no-port", Requirement.OPTIONAL, "x").run().level())
        .isEqualTo(Level.DOWN);
    assertThat(Checks.tcp("g", "T", "host:abc", Requirement.OPTIONAL, "x").run().level())
        .isEqualTo(Level.DOWN);
  }
}
