package tungnn.tutor.java.tool.devdesk.desk;

import java.util.List;

/** Một khay cùng các widget bên trong, đã sắp xếp để hiển thị. */
public record TrayView(TrayId tray, List<DeskWidget> widgets) {

  public long readyCount() {
    return widgets.stream().filter(DeskWidget::enabled).count();
  }
}
