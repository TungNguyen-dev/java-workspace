package tungnn.tutor.java.infra.document.adapter.powerpoint;

import java.io.IOException;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtil;
import tungnn.tutor.java.infra.document.adapter.office.powerpoint.PowerPointUtils;

public class PowerPointUtilTest {

  static void main() throws IOException {
    PowerPointUtilTest tests = new PowerPointUtilTest();
    tests.testCollectTextRuns();
  }

  void testCollectTextRuns() throws IOException {
    try (var document =
        PowerPointUtils.readPresentation(ResourceUtil.getResourceAsStream("SamplePPTX-All.pptx"))) {

      var textRuns = PowerPointUtils.collectTextRuns(document, text -> true);

      textRuns.forEach(
          (text, runs) -> {
            runs.forEach(System.out::println);
          });
    }
  }
}
