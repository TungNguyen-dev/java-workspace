package tungnn.tutor.java.tool.devdesk.desk;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class DeskCatalogTest {

  private static PlannedWidget widget(String id, TrayId tray, int order) {
    return new PlannedWidget(id, "Title " + id, "summary", tray, order);
  }

  @Test
  void ordersTraysByDeclarationAndSkipsEmptyOnes() {
    var catalog =
        new DeskCatalog(
            List.of(widget("s", TrayId.SYSTEM, 1), widget("d", TrayId.DOCUMENTS, 1)));

    assertThat(catalog.trays()).extracting(TrayView::tray)
        .containsExactly(TrayId.DOCUMENTS, TrayId.SYSTEM);
  }

  @Test
  void ordersWidgetsInsideATrayByOrderThenTitle() {
    var catalog =
        new DeskCatalog(
            List.of(
                widget("b", TrayId.CLOUD, 20),
                widget("c", TrayId.CLOUD, 10),
                widget("a", TrayId.CLOUD, 10)));

    assertThat(catalog.trays().getFirst().widgets())
        .extracting(DeskWidget::id)
        .containsExactly("a", "c", "b");
  }

  @Test
  void readyCountIgnoresPlannedWidgets() {
    var ready = new DeskWidget() {
      public String id() { return "ready"; }
      public String title() { return "Ready"; }
      public String summary() { return ""; }
      public TrayId tray() { return TrayId.SYSTEM; }
      public int order() { return 1; }
    };
    var catalog = new DeskCatalog(List.of(ready, widget("p", TrayId.SYSTEM, 2)));

    assertThat(catalog.trays().getFirst().readyCount()).isEqualTo(1);
  }
}
