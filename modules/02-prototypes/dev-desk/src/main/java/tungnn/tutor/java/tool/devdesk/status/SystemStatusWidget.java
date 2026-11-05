package tungnn.tutor.java.tool.devdesk.status;

import org.springframework.stereotype.Component;
import tungnn.tutor.java.tool.devdesk.desk.DeskWidget;
import tungnn.tutor.java.tool.devdesk.desk.TrayId;

@Component
class SystemStatusWidget implements DeskWidget {

  @Override
  public String id() {
    return "system-status";
  }

  @Override
  public String title() {
    return "Trạng thái hệ thống";
  }

  @Override
  public String summary() {
    return "Kiểm tra Java, trình duyệt tự động, khoá API và các dịch vụ cục bộ.";
  }

  @Override
  public TrayId tray() {
    return TrayId.SYSTEM;
  }

  @Override
  public int order() {
    return 10;
  }

  @Override
  public String endpoint() {
    return "/widgets/system-status";
  }

  @Override
  public int refreshSeconds() {
    return 15;
  }
}
