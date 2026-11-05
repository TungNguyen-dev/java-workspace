package tungnn.tutor.java.tool.devdesk.desk;

/** Các khay trên bàn làm việc. Thứ tự khai báo cũng là thứ tự hiển thị. */
public enum TrayId {
  DOCUMENTS("documents", "Tài liệu", "Dịch, đọc và xử lý file văn phòng"),
  WEB_DATA("web-data", "Web và dữ liệu", "Thu thập trang web, xử lý CSV và JSON"),
  CLOUD("cloud", "Cloud và hạ tầng", "AWS, Kafka, lịch chạy"),
  SYSTEM("system", "Hệ thống", "Trạng thái máy và cấu hình");

  private final String id;
  private final String label;
  private final String description;

  TrayId(String id, String label, String description) {
    this.id = id;
    this.label = label;
    this.description = description;
  }

  public String id() {
    return id;
  }

  public String label() {
    return label;
  }

  public String description() {
    return description;
  }
}
