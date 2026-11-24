package tungnn.tutor.java.tool.crawler.core.infra.crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.selenium.element.ElementFinder;
import tungnn.tutor.java.selenium.element.ElementInfo;
import tungnn.tutor.java.tool.crawler.core.domain.AbstractWebCrawler;

public class AwsDocSdkWebCrawler extends AbstractWebCrawler {

    private static final By TITLE_LOCATOR = By.id("skip-link");
    private static final By CONTENT_LOCATOR = By.id("main-col-body");

    public AwsDocSdkWebCrawler(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getTitle() {
        return ElementFinder.findElement(driver, TITLE_LOCATOR).getText();
    }

    @Override
    protected String getContentAsHtml() {
        return ElementInfo.getAttribute(ElementFinder.findElement(driver, CONTENT_LOCATOR), "innerHTML");
    }
}
