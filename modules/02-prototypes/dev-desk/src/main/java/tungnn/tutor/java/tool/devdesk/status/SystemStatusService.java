package tungnn.tutor.java.tool.devdesk.status;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import tungnn.tutor.java.core.lib.multithread.ConcurrentUtils;

/** Chạy mọi {@link StatusCheck} song song và gom kết quả theo nhóm, giữ nguyên thứ tự khai báo. */
public class SystemStatusService {

  private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

  private final List<StatusCheck> checks;

  public SystemStatusService(List<StatusCheck> checks) {
    this.checks = List.copyOf(checks);
  }

  public StatusReport report() {
    var entries = ConcurrentUtils.executeConcurrently(checks, SystemStatusService::runSafely);

    var byGroup = new LinkedHashMap<String, List<StatusReport.Entry>>();
    for (var entry : entries) {
      byGroup.computeIfAbsent(entry.group(), g -> new ArrayList<>()).add(entry);
    }
    var groups =
        byGroup.entrySet().stream()
            .map(e -> new StatusReport.Group(e.getKey(), List.copyOf(e.getValue())))
            .toList();

    return new StatusReport(groups, LocalTime.now().format(TIME));
  }

  /** Một check lỗi không được làm hỏng cả báo cáo. */
  private static StatusReport.Entry runSafely(StatusCheck check) {
    CheckResult result;
    try {
      result = check.run();
    } catch (Exception e) {
      result = CheckResult.down("Không chạy được phép kiểm tra: " + e.getClass().getSimpleName());
    }
    return new StatusReport.Entry(check.group(), check.label(), result);
  }
}
