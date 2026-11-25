package tungnn.tutor.java.tool.browsersetup;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import tungnn.tutor.java.selenium.pool.GenericWebDriverPool;
import tungnn.tutor.java.selenium.pool.WebDriverPoolConfig;
import tungnn.tutor.java.tool.browsersetup.core.SetupReport;
import tungnn.tutor.java.tool.browsersetup.service.BrowserSetupService;
import tungnn.tutor.java.tool.browsersetup.utils.BrowserSetupPageUtils;

public class BrowserSetupApplication {

    static void main() throws InterruptedException {
        try (var pool = new GenericWebDriverPool(WebDriverPoolConfig.fromEnv());
                var service = new BrowserSetupService(pool, BrowserSetupPageUtils.defaults())) {
            Runtime.getRuntime().addShutdownHook(new Thread(service::close));

            printReport(service.openAllPages());
            waitForUserToFinish();
        }
    }

    private static void printReport(SetupReport report) {
        System.out.printf("Opened %d page(s) on %d driver(s).%n", report.pageCount(), report.driverCount());
        report.driverFailures()
                .forEach(f -> System.out.printf(
                        "  FAILED driver %d: %s%n", f.driverIndex(), f.error().getMessage()));
        report.pageFailures()
                .forEach(f -> System.out.printf(
                        "  FAILED driver %d page '%s': %s%n",
                        f.driverIndex(), f.pageName(), f.error().getMessage()));
    }

    private static void waitForUserToFinish() throws InterruptedException {
        var scanner = new Scanner(System.in);
        System.out.println("Finish the setup in the browsers, then press Enter to quit...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
            return;
        }
        // Closed stdin (nohup, CI, redirected input) must not end the session; wait until the process is killed.
        System.out.println("Standard input is closed; keeping browsers open until the process is stopped.");
        new CountDownLatch(1).await();
    }
}
