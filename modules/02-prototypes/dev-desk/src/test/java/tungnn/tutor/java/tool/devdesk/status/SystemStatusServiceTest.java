package tungnn.tutor.java.tool.devdesk.status;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.devdesk.status.CheckResult.Level;

class SystemStatusServiceTest {

  private static StatusCheck fixed(String group, String label, CheckResult result) {
    return new StatusCheck(group, label, () -> result);
  }

  @Test
  void groupsEntriesKeepingDeclarationOrder() {
    var service =
        new SystemStatusService(
            List.of(
                fixed("A", "a1", CheckResult.ok("")),
                fixed("B", "b1", CheckResult.ok("")),
                fixed("A", "a2", CheckResult.ok(""))));

    var report = service.report();

    assertThat(report.groups()).extracting(StatusReport.Group::name).containsExactly("A", "B");
    assertThat(report.groups().getFirst().entries())
        .extracting(StatusReport.Entry::label)
        .containsExactly("a1", "a2");
  }

  @Test
  void overallIsTheWorstLevel() {
    var service =
        new SystemStatusService(
            List.of(
                fixed("A", "ok", CheckResult.ok("")),
                fixed("A", "warn", CheckResult.warn("")),
                fixed("A", "down", CheckResult.down(""))));

    var report = service.report();

    assertThat(report.overall()).isEqualTo(Level.DOWN);
    assertThat(report.headline()).isEqualTo("1 mục bị lỗi");
  }

  @Test
  void throwingCheckBecomesDownWithoutBreakingTheReport() {
    var boom = new StatusCheck("A", "boom", () -> { throw new IllegalStateException("x"); });
    var service = new SystemStatusService(List.of(boom, fixed("A", "ok", CheckResult.ok(""))));

    var report = service.report();

    assertThat(report.count(Level.DOWN)).isEqualTo(1);
    assertThat(report.count(Level.OK)).isEqualTo(1);
  }

  @Test
  void emptyChecksReportIsOk() {
    var report = new SystemStatusService(List.of()).report();

    assertThat(report.overall()).isEqualTo(Level.OK);
    assertThat(report.headline()).isEqualTo("Mọi thứ đang ổn");
  }
}
