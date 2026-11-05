package tungnn.tutor.java.tool.devdesk.desk;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Gom mọi {@link DeskWidget} thành các khay theo thứ tự hiển thị. Khay rỗng bị bỏ qua. */
@Component
public class DeskCatalog {

  private static final Comparator<DeskWidget> WIDGET_ORDER =
      Comparator.comparingInt(DeskWidget::order).thenComparing(DeskWidget::title);

  private final List<TrayView> trays;

  public DeskCatalog(List<DeskWidget> widgets) {
    Map<TrayId, List<DeskWidget>> byTray =
        widgets.stream().collect(Collectors.groupingBy(DeskWidget::tray));

    this.trays =
        java.util.Arrays.stream(TrayId.values())
            .filter(byTray::containsKey)
            .map(
                tray ->
                    new TrayView(tray, byTray.get(tray).stream().sorted(WIDGET_ORDER).toList()))
            .toList();
  }

  public List<TrayView> trays() {
    return trays;
  }
}
