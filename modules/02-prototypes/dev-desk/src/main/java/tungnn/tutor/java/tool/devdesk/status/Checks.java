package tungnn.tutor.java.tool.devdesk.status;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import tungnn.tutor.java.core.lib.net.socket.utils.SocketUtils;

/** Các phép kiểm tra dựng sẵn. */
public final class Checks {

  private static final int TCP_TIMEOUT_MS = 500;

  private Checks() {}

  /** Mức độ khi thiếu: bắt buộc thì báo lỗi, tuỳ chọn thì chỉ cảnh báo. */
  public enum Requirement {
    REQUIRED,
    OPTIONAL;

    CheckResult missing(String message) {
      return this == REQUIRED ? CheckResult.down(message) : CheckResult.warn(message);
    }
  }

  public static StatusCheck env(
      String group, String label, String name, Requirement requirement, String impact) {
    return env(group, label, name, requirement, impact, System::getenv);
  }

  static StatusCheck env(
      String group,
      String label,
      String name,
      Requirement requirement,
      String impact,
      Function<String, String> lookup) {
    return new StatusCheck(
        group,
        label,
        () -> {
          var value = lookup.apply(name);
          return value == null || value.isBlank()
              ? requirement.missing("Chưa đặt biến " + name + ". " + impact)
              : CheckResult.ok("Đã đặt biến " + name);
        });
  }

  public static StatusCheck path(
      String group, String label, Path path, Requirement requirement, String impact) {
    return new StatusCheck(
        group,
        label,
        () ->
            Files.exists(path)
                ? CheckResult.ok(path.toString())
                : requirement.missing("Không thấy " + path + ". " + impact));
  }

  public static StatusCheck tcp(
      String group, String label, String hostPort, Requirement requirement, String impact) {
    return new StatusCheck(
        group,
        label,
        () -> {
          int colon = hostPort.lastIndexOf(':');
          if (colon <= 0) {
            return CheckResult.down("Cấu hình sai, cần dạng host:port nhưng nhận được " + hostPort);
          }
          int port;
          try {
            port = Integer.parseInt(hostPort.substring(colon + 1));
          } catch (NumberFormatException e) {
            return CheckResult.down("Cổng không hợp lệ trong " + hostPort);
          }
          var host = hostPort.substring(0, colon);
          return SocketUtils.isTcpPortOpen(host, port, TCP_TIMEOUT_MS)
              ? CheckResult.ok("Đang lắng nghe tại " + hostPort)
              : requirement.missing("Không kết nối được " + hostPort + ". " + impact);
        });
  }

  public static StatusCheck jvm() {
    return new StatusCheck(
        "Máy",
        "Java",
        () -> {
          var runtime = Runtime.getRuntime();
          long usedMb = (runtime.totalMemory() - runtime.freeMemory()) >> 20;
          long maxMb = runtime.maxMemory() >> 20;
          var text = "Java %s, đã dùng %d / %d MB".formatted(Runtime.version().feature(), usedMb, maxMb);
          return usedMb * 10 > maxMb * 9 ? CheckResult.warn(text) : CheckResult.ok(text);
        });
  }
}
