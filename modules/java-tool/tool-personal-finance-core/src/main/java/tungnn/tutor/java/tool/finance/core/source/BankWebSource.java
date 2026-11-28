package tungnn.tutor.java.tool.finance.core.source;

import java.util.Objects;
import java.util.stream.Stream;
import tungnn.tutor.java.selenium.pool.WebDriverPool;
import tungnn.tutor.java.tool.finance.core.model.RawDocument;

public final class BankWebSource implements FinanceSource {

    private final String id;
    private final WebDriverPool pool;
    private final BankWebScraper scraper;

    public BankWebSource(String id, WebDriverPool pool, BankWebScraper scraper) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.pool = Objects.requireNonNull(pool, "pool must not be null");
        this.scraper = Objects.requireNonNull(scraper, "scraper must not be null");
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Stream<RawDocument> fetch(SyncCursor cursor) {
        var driver = pool.borrowDriver();
        try {
            // TODO: implement a BankWebScraper per bank (login with a persistent Chrome profile, manual OTP once).
            var documents = scraper.scrape(driver, cursor);
            pool.returnDriver(driver);
            return documents.stream();
        } catch (RuntimeException e) {
            pool.invalidateDriver(driver);
            throw e;
        }
    }
}
