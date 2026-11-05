package tungnn.tutor.java.tool.devdesk.status;

import java.util.function.Supplier;

/** Một phép kiểm tra thuộc một nhóm hiển thị trong widget System Status. */
public record StatusCheck(String group, String label, Supplier<CheckResult> probe) {

  public CheckResult run() {
    return probe.get();
  }
}
