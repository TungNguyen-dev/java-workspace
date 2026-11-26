package tungnn.tutor.java.tool.browsersetup.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.core.lib.multithread.ConcurrentUtils;
import tungnn.tutor.java.selenium.browser.NavigationUtils;
import tungnn.tutor.java.selenium.browser.WindowUtils;
import tungnn.tutor.java.selenium.pool.WebDriverPool;
import tungnn.tutor.java.tool.browsersetup.core.BrowserSetupPage;
import tungnn.tutor.java.tool.browsersetup.core.SetupReport;
import tungnn.tutor.java.tool.browsersetup.core.WindowBounds;
import tungnn.tutor.java.tool.browsersetup.utils.WindowLayoutUtils;

public class BrowserSetupService implements AutoCloseable {

    private static final System.Logger LOGGER = System.getLogger(BrowserSetupService.class.getName());

    private final WebDriverPool pool;
    private final List<BrowserSetupPage> pages;
    private final Optional<WindowBounds> screen;
    private final List<WebDriver> borrowedDrivers = new CopyOnWriteArrayList<>();
    private boolean closed;

    public BrowserSetupService(WebDriverPool pool, List<BrowserSetupPage> pages) {
        this(pool, pages, Optional.empty());
    }

    public BrowserSetupService(WebDriverPool pool, List<BrowserSetupPage> pages, Optional<WindowBounds> screen) {
        this.pool = Objects.requireNonNull(pool, "pool must not be null");
        this.pages = List.copyOf(Objects.requireNonNull(pages, "pages must not be null"));
        this.screen = Objects.requireNonNull(screen, "screen must not be null");
    }

    public SetupReport openAllPages() {
        var driverFailures = Collections.synchronizedList(new ArrayList<SetupReport.DriverFailure>());
        var pageFailures = Collections.synchronizedList(new ArrayList<SetupReport.PageFailure>());
        var driverIndexes = IntStream.range(0, pool.size()).boxed().toList();

        var driverOpened = ConcurrentUtils.executeConcurrently(driverIndexes, driverIndex -> {
            var driver = borrowDriver(driverIndex, driverFailures);
            driver.ifPresent(d -> {
                arrangeWindow(driverIndex, d);
                openPagesOnDriver(driverIndex, d, pageFailures);
            });
            return driver.isPresent();
        });

        var openedDriverCount =
                (int) driverOpened.stream().filter(Boolean::booleanValue).count();
        return new SetupReport(openedDriverCount, pages.size(), driverFailures, pageFailures);
    }

    private Optional<WebDriver> borrowDriver(int driverIndex, List<SetupReport.DriverFailure> driverFailures) {
        try {
            var driver = pool.borrowDriver();
            borrowedDrivers.add(driver);
            return Optional.of(driver);
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.WARNING, "Unable to borrow driver " + driverIndex, e);
            driverFailures.add(new SetupReport.DriverFailure(driverIndex, e));
            return Optional.empty();
        }
    }

    private void arrangeWindow(int driverIndex, WebDriver driver) {
        screen.ifPresent(bounds -> {
            try {
                var cell = WindowLayoutUtils.grid(bounds, pool.size(), driverIndex);
                WindowUtils.setWindowPosition(driver, cell.x(), cell.y());
                WindowUtils.setWindowSize(driver, cell.width(), cell.height());
            } catch (RuntimeException e) {
                LOGGER.log(System.Logger.Level.WARNING, "Unable to arrange the window of driver " + driverIndex, e);
            }
        });
    }

    private void openPagesOnDriver(int driverIndex, WebDriver driver, List<SetupReport.PageFailure> pageFailures) {
        var firstTab = driver.getWindowHandle();
        for (var i = 0; i < pages.size(); i++) {
            if (i > 0 && !openNewTab(driverIndex, driver, i, pageFailures)) {
                break;
            }
            openPage(driverIndex, driver, pages.get(i), pageFailures);
        }
        try {
            WindowUtils.switchToWindow(driver, firstTab);
        } catch (RuntimeException e) {
            LOGGER.log(
                    System.Logger.Level.WARNING, "Unable to switch back to the first tab on driver " + driverIndex, e);
        }
    }

    // Without a fresh tab the next page would navigate over the previous one and wipe its session.
    private boolean openNewTab(
            int driverIndex, WebDriver driver, int pageIndex, List<SetupReport.PageFailure> pageFailures) {
        try {
            WindowUtils.createNewTabAndSwitch(driver);
            return true;
        } catch (RuntimeException e) {
            LOGGER.log(System.Logger.Level.WARNING, "Unable to open a new tab on driver " + driverIndex, e);
            for (var skipped : pages.subList(pageIndex, pages.size())) {
                pageFailures.add(new SetupReport.PageFailure(driverIndex, skipped.name(), e));
            }
            return false;
        }
    }

    // AssertionError is caught as well so a failing custom action cannot abort the other pages of this driver.
    private void openPage(
            int driverIndex, WebDriver driver, BrowserSetupPage page, List<SetupReport.PageFailure> pageFailures) {
        try {
            NavigationUtils.navigateTo(driver, page.url());
            page.action().run(driver);
        } catch (Exception | AssertionError e) {
            LOGGER.log(
                    System.Logger.Level.WARNING,
                    "Setup failed for page '%s' on driver %d".formatted(page.name(), driverIndex),
                    e);
            pageFailures.add(new SetupReport.PageFailure(driverIndex, page.name(), e));
        }
    }

    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }
        closed = true;
        try {
            // The pool only destroys idle objects on close, so borrowed drivers must be returned first or Chrome
            // stays running.
            for (var driver : borrowedDrivers) {
                returnDriverQuietly(driver);
            }
            borrowedDrivers.clear();
        } finally {
            pool.close();
        }
    }

    private void returnDriverQuietly(WebDriver driver) {
        try {
            pool.returnDriver(driver);
        } catch (RuntimeException e) {
            LOGGER.log(System.Logger.Level.WARNING, "Unable to return a driver to the pool", e);
        }
    }
}
