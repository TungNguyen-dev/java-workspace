package tungnn.tutor.java.tool.devdesk.desk;

/**
 * Một công cụ trên bàn làm việc. Thêm công cụ mới chỉ cần khai báo một bean implements interface
 * này; {@link DeskCatalog} sẽ tự xếp nó vào đúng khay.
 */
public interface DeskWidget {

  /** Định danh ổn định, dùng làm id HTML và route. */
  String id();

  String title();

  /** Một câu mô tả công cụ làm gì. */
  String summary();

  TrayId tray();

  /** Thứ tự trong khay, số nhỏ hiển thị trước. */
  int order();

  /** {@code false} nghĩa là công cụ đã được lên kế hoạch nhưng chưa dùng được. */
  default boolean enabled() {
    return true;
  }

  /** URL trả về HTML fragment của phần thân widget; {@code null} nếu widget không có phần thân. */
  default String endpoint() {
    return null;
  }

  /** Chu kỳ tự làm mới phần thân (giây); 0 là không tự làm mới. */
  default int refreshSeconds() {
    return 0;
  }
}
