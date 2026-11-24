package tungnn.tutor.java.obsidian.report;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReportRendererTest {

    @Test
    void rendersLinkHealth() {
        var md = ReportRenderer.render(
                new LinkHealthReport(Map.of("Missing", List.of("a/B.md")), List.of("Lonely.md"), List.of()));

        assertThat(md)
                .contains("- `Missing` referenced by [[a/B]]")
                .contains("- [[Lonely]]")
                .contains("None.");
    }

    @Test
    void rendersWeekly() {
        var md = ReportRenderer.render(new PeriodicReport(
                PeriodicReport.Period.WEEKLY,
                LocalDate.of(2026, 11, 23),
                LocalDate.of(2026, 11, 29),
                List.of("n.md"),
                List.of()));

        assertThat(md).startsWith("# Weekly report 2026-11-23 to 2026-11-29").contains("- [[n]]");
    }
}
