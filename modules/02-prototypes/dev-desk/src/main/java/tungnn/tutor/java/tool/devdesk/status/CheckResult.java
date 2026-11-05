package tungnn.tutor.java.tool.devdesk.status;

/** Kết quả của một lần kiểm tra. {@code detail} không bao giờ chứa giá trị bí mật. */
public record CheckResult(Level level, String detail) {

  public static CheckResult ok(String detail) {
    return new CheckResult(Level.OK, detail);
  }

  public static CheckResult warn(String detail) {
    return new CheckResult(Level.WARN, detail);
  }

  public static CheckResult down(String detail) {
    return new CheckResult(Level.DOWN, detail);
  }

  /** Thứ tự khai báo tăng dần theo mức nghiêm trọng. Ký hiệu khác hình để không chỉ dựa vào màu. */
  public enum Level {
    OK("Ổn", "●"),
    WARN("Cần xem", "▲"),
    DOWN("Lỗi", "■");

    private final String label;
    private final String symbol;

    Level(String label, String symbol) {
      this.label = label;
      this.symbol = symbol;
    }

    public String label() {
      return label;
    }

    public String symbol() {
      return symbol;
    }

    public String css() {
      return name().toLowerCase();
    }
  }
}
