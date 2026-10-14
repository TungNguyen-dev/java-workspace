package tungnn.tutor.java.tool.crawler.core.infrastructure.crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import tungnn.tutor.java.selenium.util.ElementUtil;
import tungnn.tutor.java.tool.crawler.core.domain.AbstractWebCrawler;
import tungnn.tutor.java.tool.crawler.core.domain.CrawlRequest;

public class GenericWebCrawler extends AbstractWebCrawler {

  // Locators defined as constants for code clarity and reuse
  private static final By FIRST_HEADING_LOCATOR = By.cssSelector("h1, h2, h3, h4, h5, h6");
  private static final By ROOT_HTML_LOCATOR = By.tagName("html");

  public GenericWebCrawler(WebDriver driver) {
    super(driver);
  }

  @Override
  public boolean supports(CrawlRequest request) {
    return true;
  }

  /**
   * Finds and returns the text of the first heading element (h1-h6) on the page. Returns an empty
   * string if no heading is found.
   */
  @Override
  protected String getTitle() {
    var headingElements = ElementUtil.findElements(driver, FIRST_HEADING_LOCATOR);
    if (headingElements.isEmpty()) {
      return "";
    }
    return headingElements.getFirst().getText();
  }

  /** Captures and returns the complete outer HTML content of the root element. */
  @Override
  protected String getContentAsHtml() {
    var rootElement = ElementUtil.findElement(driver, ROOT_HTML_LOCATOR);
    return ElementUtil.getAttribute(rootElement, "outerHTML");
  }
}
