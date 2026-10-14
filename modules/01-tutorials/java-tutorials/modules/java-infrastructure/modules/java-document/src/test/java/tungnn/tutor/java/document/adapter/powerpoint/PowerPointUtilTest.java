package tungnn.tutor.java.document.adapter.powerpoint;

import java.io.IOException;
import tungnn.tutor.java.core.lib.io.resource.ResourceUtil;

public class PowerPointUtilTest {

  static void main() throws IOException {
    PowerPointUtilTest tests = new PowerPointUtilTest();
    tests.testCollectTextRuns();
  }

  void testCollectTextRuns() throws IOException {
    try (var document =
        PowerPointUtil.readPresentation(ResourceUtil.getResourceAsStream("SamplePPTX-All.pptx"))) {

      var textRuns = PowerPointUtil.collectTextRuns(document, text -> true);

      textRuns.forEach(
          (text, runs) -> {
            runs.forEach(System.out::println);
          });
    }
  }
}
