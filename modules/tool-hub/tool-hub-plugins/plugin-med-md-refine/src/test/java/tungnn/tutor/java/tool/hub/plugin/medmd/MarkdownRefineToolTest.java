package tungnn.tutor.java.tool.hub.plugin.medmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.hub.api.ToolResult;

class MarkdownRefineToolTest {

  @Test
  void shouldApplyNormalizeRuleFromCore() throws Exception {
    var tool = new MarkdownRefineTool();

    ToolResult result =
        tool.execute(
            null,
            Map.of(
                "markdown",
                "Thanh nhiệt , giải độc 12g",
                MarkdownRefineTool.RULE_PARAM_PREFIX + "normalize",
                true));

    assertTrue(result.success());
    assertEquals("Thanh nhiệt, giải độc 12 g\n", result.data());
  }
}
