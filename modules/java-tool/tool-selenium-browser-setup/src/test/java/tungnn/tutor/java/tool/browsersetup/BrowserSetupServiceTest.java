package tungnn.tutor.java.tool.browsersetup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import tungnn.tutor.java.selenium.pool.WebDriverPool;
import tungnn.tutor.java.tool.browsersetup.core.BrowserSetupPage;
import tungnn.tutor.java.tool.browsersetup.service.BrowserSetupService;

class BrowserSetupServiceTest {

    private static final class RecordingPool implements WebDriverPool {
        final int size;
        final List<WebDriver> borrowed = Collections.synchronizedList(new ArrayList<>());
        final List<String> events = Collections.synchronizedList(new ArrayList<>());
        Supplier<WebDriver> driverFactory = () -> mock(WebDriver.class, RETURNS_DEEP_STUBS);
        boolean failOnReturn;

        RecordingPool(int size) {
            this.size = size;
        }

        @Override
        public WebDriver borrowDriver() {
            var driver = driverFactory.get();
            borrowed.add(driver);
            return driver;
        }

        @Override
        public void returnDriver(WebDriver driver) {
            events.add("return");
            if (failOnReturn) {
                throw new IllegalStateException("return failed");
            }
        }

        @Override
        public void invalidateDriver(WebDriver driver) {}

        @Override
        public int size() {
            return size;
        }

        @Override
        public void close() {
            events.add("close");
        }
    }

    @Test
    void opensEveryPageOnEveryDriver() {
        var pool = new RecordingPool(2);
        var runs = new AtomicInteger();
        var pages = List.of(
                BrowserSetupPage.of("a", "https://a.test", d -> runs.incrementAndGet()),
                BrowserSetupPage.of("b", "https://b.test", d -> runs.incrementAndGet()));

        var report = new BrowserSetupService(pool, pages).openAllPages();

        assertThat(pool.borrowed).hasSize(2);
        assertThat(runs).hasValue(4);
        assertThat(report.hasFailures()).isFalse();
        assertThat(report.driverCount()).isEqualTo(2);
        for (var driver : pool.borrowed) {
            verify(driver.navigate()).to("https://a.test");
            verify(driver.navigate()).to("https://b.test");
        }
    }

    @Test
    void failingActionDoesNotStopOtherPages() {
        var pool = new RecordingPool(1);
        var second = new AtomicInteger();
        var pages = List.of(
                BrowserSetupPage.of("bad", "https://a.test", d -> {
                    throw new IllegalStateException("boom");
                }),
                BrowserSetupPage.of("good", "https://b.test", d -> second.incrementAndGet()));

        var report = new BrowserSetupService(pool, pages).openAllPages();

        assertThat(second).hasValue(1);
        assertThat(report.pageFailures()).hasSize(1);
        assertThat(report.pageFailures().getFirst().pageName()).isEqualTo("bad");
    }

    @Test
    void assertionErrorInActionIsReportedAsPageFailure() {
        var pool = new RecordingPool(1);
        var second = new AtomicInteger();
        var pages = List.of(
                BrowserSetupPage.of("bad", "https://a.test", d -> {
                    throw new AssertionError("boom");
                }),
                BrowserSetupPage.of("good", "https://b.test", d -> second.incrementAndGet()));

        var report = new BrowserSetupService(pool, pages).openAllPages();

        assertThat(second).hasValue(1);
        assertThat(report.pageFailures()).extracting("pageName").containsExactly("bad");
    }

    @Test
    void borrowFailureIsReportedAndOtherDriversStillOpenPages() {
        var pool = new RecordingPool(2);
        var calls = new AtomicInteger();
        pool.driverFactory = () -> {
            if (calls.getAndIncrement() == 0) {
                throw new IllegalStateException("no chrome");
            }
            return mock(WebDriver.class, RETURNS_DEEP_STUBS);
        };
        var pages = List.of(BrowserSetupPage.of("a", "https://a.test"));

        var report = new BrowserSetupService(pool, pages).openAllPages();

        assertThat(report.driverFailures()).hasSize(1);
        assertThat(report.driverCount()).isEqualTo(1);
        assertThat(pool.borrowed).hasSize(1);
        verify(pool.borrowed.getFirst().navigate()).to("https://a.test");
    }

    @Test
    void newTabFailureSkipsRemainingPagesInsteadOfOverwritingPreviousTab() {
        var pool = new RecordingPool(1);
        pool.driverFactory = () -> {
            var driver = mock(WebDriver.class, RETURNS_DEEP_STUBS);
            when(driver.switchTo().newWindow(any())).thenThrow(new WebDriverException("no tab"));
            return driver;
        };
        var pages = List.of(
                BrowserSetupPage.of("a", "https://a.test"),
                BrowserSetupPage.of("b", "https://b.test"),
                BrowserSetupPage.of("c", "https://c.test"));

        var report = new BrowserSetupService(pool, pages).openAllPages();

        var driver = pool.borrowed.getFirst();
        verify(driver.navigate()).to("https://a.test");
        verify(driver.navigate(), never()).to("https://b.test");
        verify(driver.navigate(), never()).to("https://c.test");
        assertThat(report.pageFailures()).extracting("pageName").containsExactly("b", "c");
    }

    @Test
    void closeReturnsDriversBeforeClosingPool() {
        var pool = new RecordingPool(2);
        var service = new BrowserSetupService(pool, List.of(BrowserSetupPage.of("a", "https://a.test")));
        service.openAllPages();

        service.close();
        service.close();

        assertThat(pool.events).containsExactly("return", "return", "close");
    }

    @Test
    void closeStillClosesPoolWhenReturningADriverFails() {
        var pool = new RecordingPool(2);
        pool.failOnReturn = true;
        var service = new BrowserSetupService(pool, List.of(BrowserSetupPage.of("a", "https://a.test")));
        service.openAllPages();

        service.close();

        assertThat(pool.events).containsExactly("return", "return", "close");
    }
}
