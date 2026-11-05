package tungnn.tutor.java.tool.devdesk.desk;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class DeskController {

  private final DeskCatalog catalog;

  DeskController(DeskCatalog catalog) {
    this.catalog = catalog;
  }

  @GetMapping("/")
  String desk(Model model) {
    model.addAttribute("trays", catalog.trays());
    return "desk";
  }
}
