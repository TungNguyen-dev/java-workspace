package tungnn.tutor.java.tool.browsersetup.core;

import java.util.List;

public record SetupReport(
        int driverCount, int pageCount, List<DriverFailure> driverFailures, List<PageFailure> pageFailures) {

    public SetupReport {
        driverFailures = List.copyOf(driverFailures);
        pageFailures = List.copyOf(pageFailures);
    }

    public boolean hasFailures() {
        return !driverFailures.isEmpty() || !pageFailures.isEmpty();
    }

    public record DriverFailure(int driverIndex, Throwable error) {}

    public record PageFailure(int driverIndex, String pageName, Throwable error) {}
}
