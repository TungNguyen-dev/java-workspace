package tungnn.tutor.java.tool.devdesk.status;

import java.util.Comparator;
import java.util.List;
import tungnn.tutor.java.tool.devdesk.status.CheckResult.Level;

public record StatusReport(List<Group> groups, String checkedAt) {

  public record Group(String name, List<Entry> entries) {}

  public record Entry(String group, String label, CheckResult result) {}

  /** Mức tệ nhất trong mọi mục; {@code OK} nếu không có mục nào. */
  public Level overall() {
    return groups.stream()
        .flatMap(g -> g.entries().stream())
        .map(e -> e.result().level())
        .max(Comparator.naturalOrder())
        .orElse(Level.OK);
  }

  public long count(Level level) {
    return groups.stream()
        .flatMap(g -> g.entries().stream())
        .filter(e -> e.result().level() == level)
        .count();
  }

  public String headline() {
    return switch (overall()) {
      case OK -> "Mọi thứ đang ổn";
      case WARN -> count(Level.WARN) + " mục cần xem";
      case DOWN -> count(Level.DOWN) + " mục bị lỗi";
    };
  }
}
