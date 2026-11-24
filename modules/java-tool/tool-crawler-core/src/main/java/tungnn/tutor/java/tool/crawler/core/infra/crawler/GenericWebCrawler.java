package tungnn.tutor.java.tool.crawler.core.infra.crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.selenium.element.ElementFinder;
import tungnn.tutor.java.selenium.element.ElementInfo;
import tungnn.tutor.java.tool.crawler.core.domain.AbstractWebCrawler;

public class GenericWebCrawler extends AbstractWebCrawler {

    // Locators defined as constants for code clarity and reuse
    private static final By FIRST_HEADING_LOCATOR = By.cssSelector("h1, h2, h3, h4, h5, h6");
    private static final By ROOT_HTML_LOCATOR = By.tagName("html");

    public GenericWebCrawler(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String getTitle() {
        var headingElements = ElementFinder.findElements(driver, FIRST_HEADING_LOCATOR);
        if (headingElements.isEmpty()) {
            return "";
        }
        return headingElements.getFirst().getText();
    }

    @Override
    protected String getContentAsHtml() {
        var rootElement = ElementFinder.findElement(driver, ROOT_HTML_LOCATOR);
        return ElementInfo.getAttribute(rootElement, "outerHTML");
    }
}
