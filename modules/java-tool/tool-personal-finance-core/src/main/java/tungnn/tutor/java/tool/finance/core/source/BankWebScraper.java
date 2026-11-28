package tungnn.tutor.java.tool.finance.core.source;

import java.util.List;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.tool.finance.core.model.RawDocument;

public interface BankWebScraper {

    String bankId();

    List<RawDocument> scrape(WebDriver driver, SyncCursor cursor);
}
