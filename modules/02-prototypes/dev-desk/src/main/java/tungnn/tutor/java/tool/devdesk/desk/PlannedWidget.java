package tungnn.tutor.java.tool.devdesk.desk;

/** Công cụ đã có chỗ trên bàn nhưng chưa được triển khai. Thay bằng widget thật khi sẵn sàng. */
public record PlannedWidget(String id, String title, String summary, TrayId tray, int order)
    implements DeskWidget {

  @Override
  public boolean enabled() {
    return false;
  }
}
