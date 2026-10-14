package tungnn.tutor.java.starter.infrastructure.webpage;

import java.time.Duration;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import tungnn.tutor.java.core.lib.multithread.ThreadUtil;
import tungnn.tutor.java.selenium.util.ElementUtil;
import tungnn.tutor.java.starter.infrastructure.webpage.crawler.PageCrawlResult;
import tungnn.tutor.java.starter.infrastructure.webpage.crawler.PageCrawler;

public class CourseraPage extends BasePage implements PageCrawler {

  public CourseraPage(WebDriver driver) {
    super(driver);
  }

  @Override
  protected Duration timeout() {
    return Duration.ofSeconds(30);
  }

  @Override
  protected String homeUrl() {
    return "https://www.coursera.org";
  }

  @Override
  public PageCrawlResult crawl(String url) {
    navigateTo(url);
    ThreadUtil.sleep(1000);

    var buttonTranscriptLocator =
        By.cssSelector("[data-testid='item-tool-panel-button-transcript']");

    var buttonTranscript =
        ElementUtil.waitUntil(
            driver, ExpectedConditions.elementToBeClickable(buttonTranscriptLocator), timeout());

    var attr = ElementUtil.getAttribute(buttonTranscript, "aria-pressed");
    if (!"true".equalsIgnoreCase(attr)) {
      buttonTranscript.click();
      ThreadUtil.sleep(1000);
    }

    return new PageCrawlResult(url, getLessonTitle(), getLessonContent());
  }

  private String getLessonTitle() {
    return ElementUtil.waitUntil(
            driver,
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1.video-name")),
            timeout())
        .getText();
  }

  private String getLessonContent() {
    ElementUtil.waitUntil(
        driver,
        ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.phrases")),
        timeout());

    return ElementUtil.findElements(driver, By.cssSelector("div.phrases span[data-cue]")).stream()
        .map(WebElement::getText)
        .map(String::trim)
        .filter(text -> !text.isEmpty())
        .collect(Collectors.joining(" "));
  }
}
