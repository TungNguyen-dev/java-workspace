package tungnn.tutor.java.tool.devdesk.status;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class SystemStatusController {

  private final SystemStatusService service;

  SystemStatusController(SystemStatusService service) {
    this.service = service;
  }

  /** Trả về fragment HTML, được nhúng vào thẻ widget bằng JavaScript. */
  @GetMapping("/widgets/system-status")
  String body(Model model) {
    model.addAttribute("report", service.report());
    return "widgets/system-status :: body";
  }
}
