package tungnn.tutor.java.tool.browsersetup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import tungnn.tutor.java.selenium.driver.chrome.ChromeProcessLauncher;
import tungnn.tutor.java.selenium.pool.GenericWebDriverPool;
import tungnn.tutor.java.selenium.pool.WebDriverPoolConfig;
import tungnn.tutor.java.tool.browsersetup.core.BrowserSetupPage;
import tungnn.tutor.java.tool.browsersetup.core.SetupReport;
import tungnn.tutor.java.tool.browsersetup.core.WindowBounds;
import tungnn.tutor.java.tool.browsersetup.service.BrowserSetupService;
import tungnn.tutor.java.tool.browsersetup.utils.BrowserSetupPageUtils;
import tungnn.tutor.java.tool.browsersetup.utils.ScreenUtils;
import tungnn.tutor.java.tool.browsersetup.utils.WindowLayoutUtils;

public class BrowserSetupApplication {

    static void main() throws InterruptedException {
        var config = WebDriverPoolConfig.fromEnv();
        var screen = config.headless() ? Optional.<WindowBounds>empty() : ScreenUtils.availableBounds();
        reportLayout(screen, config.maxTotal());

        if ("login".equalsIgnoreCase(System.getenv("BROWSER_SETUP_MODE"))) {
            runLoginMode(config, screen);
            return;
        }
        try (var pool = new GenericWebDriverPool(config);
                var service = new BrowserSetupService(pool, BrowserSetupPageUtils.defaults(), screen)) {
            Runtime.getRuntime().addShutdownHook(new Thread(service::close));

            printReport(service.openAllPages());
            waitForUserToFinish();
        }
    }

    // Chrome started without ChromeDriver carries no automation markers, so Google accepts the sign-in.
    private static void runLoginMode(WebDriverPoolConfig config, Optional<WindowBounds> screen)
            throws InterruptedException {
        var urls = BrowserSetupPageUtils.defaults().stream()
                .map(BrowserSetupPage::url)
                .toList();
        var processes = new ArrayList<Process>();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> processes.forEach(Process::destroy)));
        for (var i = 0; i < config.maxTotal(); i++) {
            var index = i;
            var windowArgs = screen.map(bounds -> WindowLayoutUtils.grid(bounds, config.maxTotal(), index))
                    .map(cell -> List.of(
                            "--window-position=%d,%d".formatted(cell.x(), cell.y()),
                            "--window-size=%d,%d".formatted(cell.width(), cell.height())))
                    .orElse(List.of());
            processes.add(ChromeProcessLauncher.launch(config.profilePrefix() + i, urls, windowArgs));
        }
        System.out.printf("Launched %d plain Chrome profile(s) for manual login.%n", processes.size());
        waitForUserToFinish();
    }

    private static void reportLayout(Optional<WindowBounds> screen, int count) {
        if (screen.isEmpty()) {
            System.out.println("Screen size unavailable; windows stay maximized.");
            return;
        }
        var bounds = screen.get();
        System.out.printf(
                "Tiling %d window(s) in a %dx%d grid on %dx%d screen.%n",
                count,
                WindowLayoutUtils.columns(count),
                WindowLayoutUtils.rows(count),
                bounds.width(),
                bounds.height());
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
