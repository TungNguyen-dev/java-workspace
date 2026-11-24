package tungnn.tutor.java.obsidian.report;

import java.time.LocalDate;
import java.util.List;

public record PeriodicReport(
        Period period, LocalDate from, LocalDate to, List<String> createdNotes, List<String> modifiedNotes) {

    public enum Period {
        DAILY,
        WEEKLY
    }
}
